package it.marcodemartino.server;

import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.dao.IUserDao;
import it.marcodemartino.common.dao.UserDao;
import it.marcodemartino.common.database.Database;
import it.marcodemartino.common.database.UserDatabase;
import it.marcodemartino.common.email.EmailProvider;
import it.marcodemartino.common.email.GmailProvider;
import it.marcodemartino.common.encryption.*;
import it.marcodemartino.common.services.*;
import it.marcodemartino.server.dao.IMessagesDao;
import it.marcodemartino.server.dao.MessagesDao;
import it.marcodemartino.server.database.MessagesDatabase;
import it.marcodemartino.server.handler.ClientHandler;
import it.marcodemartino.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class SSLServer implements Server {

    private final Logger logger = LogManager.getLogger(SSLServer.class);
    private final int port;
    private final String emailPassword;
    private final List<Application> clients;
    private boolean running;
    private SSLServerSocket serverSocket;

    public SSLServer(int port, String emailPassword) {
        this.port = port;
        this.emailPassword = emailPassword;
        this.running = true;
        clients = new ArrayList<>();
    }

    @Override
    public void run() {
        tryStart();
    }

    @Override
    public void stop() {
        running = false;
        tryStop();
        for (Application client : clients) {
            client.stop();
        }
    }

    private void start(int port) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        SSLServerSocketFactory serverSocketFactory = getSslServerSocketFactory();
        serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
        enableSSLProtocols(serverSocket);
        logger.info("Started the SSL socket server on the IP: {}", serverSocket.getInetAddress());

        Database userDatabase = new UserDatabase();
        userDatabase.initDatabase();
        IUserDao userDao = new UserDao(userDatabase);

        AsymmetricEncryption localEncryption = new RSAEncryption(2048);
        AsymmetricEncryption otherEncryption = new RSAEncryption(2048);
        CertificatesService certificatesService = new CertificatesService(localEncryption);
        KeysService keysService = new DatabaseKeysService(userDao);

        EmailProvider emailProvider = new GmailProvider("e2ee.messaging.app@gmail.com", emailPassword);

        RegistrationService registrationService = new RegistrationService(emailProvider, userDao);

        EncryptionService encryptionService = new EncryptionService(localEncryption, otherEncryption, keysService);
        encryptionService.loadKeysIfExist();

        Database messagesDatabase = new MessagesDatabase();
        messagesDatabase.initDatabase();
        IMessagesDao messagesDao = new MessagesDao(messagesDatabase);

        MessagingService messagingService = new MessagingService(messagesDao);

        while (running) {
            if (serverSocket.isClosed()) return;
            SSLSocket clientSocket = acceptNewConnection();
            if (clientSocket == null) return;

            logger.info("Received a connection with IP: {}", clientSocket.getInetAddress());
            Application clientHandler = new ClientHandler(clientSocket, encryptionService, registrationService, certificatesService, messagingService);
            new Thread(clientHandler).start();
        }
    }

    private SSLSocket acceptNewConnection() throws IOException {
        try {
            return (SSLSocket) serverSocket.accept();
        } catch (SocketException e) {
            if (!running && serverSocket.isClosed()) return null;
            throw new RuntimeException(e);
        }
    }

    private static void enableSSLProtocols(SSLServerSocket serverSocket) {
        // Enable the desired SSL protocols and ciphers
        serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
        serverSocket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_CBC_SHA"});
    }

    private static SSLServerSocketFactory getSslServerSocketFactory() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyManagerFactory keyManagerFactory = getKeyManagerFactoryAndLoadKey();
        SSLContext sslContext = getSslContext(keyManagerFactory);
        return sslContext.getServerSocketFactory();
    }

    private static SSLContext getSslContext(KeyManagerFactory keyManagerFactory) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

    private static KeyManagerFactory getKeyManagerFactoryAndLoadKey() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        char[] keystorePassword = "123456".toCharArray();
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("keystore.jks"), keystorePassword);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keystore, keystorePassword);
        return keyManagerFactory;
    }

    private void tryStart() {
        try {
            start(port);
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                 UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void tryStop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Handle the exception gracefully, e.g., log it, but don't re-throw it.
            e.printStackTrace(); // You can replace this with your preferred logging mechanism.
        }
    }
}
