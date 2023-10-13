package it.marcodemartino.client.socket;

import it.marcodemartino.client.socket.io.SocketIO;
import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.application.ApplicationIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SSLSocketClient implements Application {

    private final Logger logger = LogManager.getLogger(SSLSocketClient.class);
    private final SSLSocket socket;
    private final ApplicationIO applicationIO;

    public SSLSocketClient(String ip, int port) throws Exception {
        // TODO this here should be temporary, since for now I'm using a self-signed certificate
        // Load the keystore
        logger.info("Creating a socket to connect to this address: {}:{}", ip, port);
        SSLContext sslContext = getSslContext();
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        socket = tryToCreateSocket(ip, port, socketFactory);
        if (socket == null) {
            System.exit(1);
        }

        this.applicationIO = new SocketIO(socket.getInputStream(), socket.getOutputStream());
    }

    private SSLSocket tryToCreateSocket(String ip, int port, SSLSocketFactory socketFactory) {
        try {
            return (SSLSocket) socketFactory.createSocket(ip, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Could not entsablish a connection with the server. Maybe it's offline or the address is wrong");
            return null;
        }
    }

    @Override
    public void run() {
        try {
            logger.info("Starting the handshake");
            socket.startHandshake();
            applicationIO.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationIO getIO() {
        return applicationIO;
    }

    @Override
    public void stop() {
        applicationIO.stop();
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext getSslContext() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        char[] keystorePassword = "123456".toCharArray();
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("keystore.jks"), keystorePassword);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keystore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
}
