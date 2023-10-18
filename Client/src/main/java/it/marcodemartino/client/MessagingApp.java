package it.marcodemartino.client;

import it.marcodemartino.client.commands.*;
import it.marcodemartino.common.encryption.EncryptionService;
import it.marcodemartino.client.errrors.ConsoleErrorManager;
import it.marcodemartino.client.inputs.ConsoleInputEmitter;
import it.marcodemartino.client.socket.SSLSocketClient;
import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.commands.JsonCommandManager;
import it.marcodemartino.common.commands.UserCommandManager;
import it.marcodemartino.common.io.emitters.InputEmitter;
import it.marcodemartino.common.json.*;

public class MessagingApp {

    public static void main(String[] args) throws Exception {
        Application application = new SSLSocketClient("127.0.0.1", 8443);
        Thread thread = new Thread(application);

        EncryptionService encryptionService = new EncryptionService(2048);
        encryptionService.loadOrGenerateKeys();

        InputEmitter inputEmitter = new ConsoleInputEmitter();
        UserCommandManager commandManager = new UserCommandManager(new ConsoleErrorManager());
        commandManager.registerUserCommand("register", new RegisterEmail(application.getIO()));
        commandManager.registerUserCommand("verify", new VerifyCommand(application.getIO(), encryptionService, encryptionService.getLocalAsymmetricEncryption()));

        JsonCommandManager jsonCommandManager = new JsonCommandManager();
        jsonCommandManager.registerCommand(JSONMethods.SEND_PUBLIC_KEY, new SendPublicKeyCommand(encryptionService.getLocalAsymmetricEncryption(), encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.REGISTRATION_RESULT, new RegistrationResultCommand());
        jsonCommandManager.registerCommand(JSONMethods.ENCRYPTED_SIGNED_MESSAGE, new SignedEncryptedMessageCommand(application.getIO().getEventManager(), encryptionService.getLocalAsymmetricEncryption(), encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.IDENTITY_CERTIFICATE, new SendIdentityCertificateCommand(encryptionService));


        inputEmitter.registerInputListener(commandManager);
        application.getIO().registerInputListener(jsonCommandManager);

        thread.start();
        application.getIO().sendOutput(new RequestPublicKeyObject());
        inputEmitter.start();

    }
}
