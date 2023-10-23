package it.marcodemartino.server.handler;

import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.application.ApplicationIO;
import it.marcodemartino.common.commands.JsonCommandManager;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.services.EncryptionService;
import it.marcodemartino.common.json.JSONMethods;
import it.marcodemartino.server.commands.*;
import it.marcodemartino.server.services.*;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Application {

    private final Socket socket;
    private final ApplicationIO applicationIO;

    public ClientHandler(Socket socket, EncryptionService encryptionService, RegistrationService registrationService, CertificatesService certificatesService, MessagingService messagingService) throws IOException {
        this.socket = socket;
        this.applicationIO = new ClientHandlerIO(socket.getInputStream(), socket.getOutputStream());

        AsymmetricEncryption asymmetricEncryption = encryptionService.getLocalAsymmetricEncryption();
        JsonCommandManager commandManager = new JsonCommandManager();
        commandManager.registerCommand(JSONMethods.REGISTER_EMAIL, new RegisterCommand(applicationIO, registrationService));
        commandManager.registerCommand(JSONMethods.REQUEST_PUBLIC_KEY, new RequestPublicKeyCommand(asymmetricEncryption, asymmetricEncryption.getPublicKey(), applicationIO));
        commandManager.registerCommand(JSONMethods.ENCRYPTED_MESSAGE, new EncryptedMessageCommand(applicationIO.getEventManager(), asymmetricEncryption));
        commandManager.registerCommand(JSONMethods.ENCRYPTED_SIGNED_CERTIFIED_MESSAGE, new EncryptedSignedCertifiedMessageCommand(messagingService));
        commandManager.registerCommand(JSONMethods.EMAIL_VERIFICATION, new EmailVerificationCommand(asymmetricEncryption, applicationIO, registrationService, certificatesService, encryptionService, messagingService));
        commandManager.registerCommand(JSONMethods.REQUEST_PUBLIC_KEY_OF, new RequestPublicKeyOfCommand(applicationIO, encryptionService.getKeysService(), encryptionService));
        commandManager.registerCommand(JSONMethods.IDENTITY_CERTIFICATE, new ReceiveIdentityCertificateCommand(applicationIO, encryptionService, messagingService));
        this.applicationIO.registerInputListener(commandManager);
    }

    @Override
    public ApplicationIO getIO() {
        return applicationIO;
    }

    @Override
    public void stop() {
        applicationIO.stop();
        tryClose();
    }

    private void tryClose() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        applicationIO.start();
    }
}
