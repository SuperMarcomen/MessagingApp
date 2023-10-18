package it.marcodemartino.server.handler;

import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.application.ApplicationIO;
import it.marcodemartino.common.commands.JsonCommandManager;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.encryption.EncryptionService;
import it.marcodemartino.common.json.JSONMethods;
import it.marcodemartino.server.commands.*;
import it.marcodemartino.server.services.CertificatesService;
import it.marcodemartino.server.services.RegistrationService;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Application {

    private final Socket socket;
    private final ApplicationIO applicationIO;

    public ClientHandler(Socket socket, EncryptionService encryptionService, RegistrationService registrationService, CertificatesService certificatesService) throws IOException {
        this.socket = socket;
        this.applicationIO = new ClientHandlerIO(socket.getInputStream(), socket.getOutputStream());

        AsymmetricEncryption asymmetricEncryption = encryptionService.getLocalAsymmetricEncryption();
        JsonCommandManager commandManager = new JsonCommandManager();
        commandManager.registerCommand(JSONMethods.REGISTER_EMAIL, new RegisterCommand(applicationIO, registrationService));
        commandManager.registerCommand(JSONMethods.REQUEST_PUBLIC_KEY, new RequestPublicKeyCommand(asymmetricEncryption, asymmetricEncryption.getPublicKey(), applicationIO));
        commandManager.registerCommand(JSONMethods.ENCRYPTED_MESSAGE, new EncryptedMessageCommand(applicationIO.getEventManager(), asymmetricEncryption));
        commandManager.registerCommand(JSONMethods.EMAIL_VERIFICATION, new EmailVerificationCommand(asymmetricEncryption, applicationIO, registrationService, certificatesService, encryptionService));
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
