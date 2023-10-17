package it.marcodemartino.client;

import it.marcodemartino.client.commands.*;
import it.marcodemartino.client.encryption.EncryptionService;
import it.marcodemartino.client.errrors.ConsoleErrorManager;
import it.marcodemartino.client.inputs.ConsoleInputEmitter;
import it.marcodemartino.client.socket.SSLSocketClient;
import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.commands.JsonCommandManager;
import it.marcodemartino.common.commands.UserCommandManager;
import it.marcodemartino.common.io.emitters.InputEmitter;
import it.marcodemartino.common.json.JSONMethods;
import it.marcodemartino.common.json.RequestPublicKeyObject;

public class MessagingApp {

    public static void main(String[] args) throws Exception {
        Application application = new SSLSocketClient("127.0.0.1", 8443);
        Thread thread = new Thread(application);

        EncryptionService encryptionService = new EncryptionService();

        InputEmitter inputEmitter = new ConsoleInputEmitter();
        UserCommandManager commandManager = new UserCommandManager(new ConsoleErrorManager());
        commandManager.registerUserCommand("register", new RegisterEmail(application.getIO()));

        JsonCommandManager jsonCommandManager = new JsonCommandManager();
        jsonCommandManager.registerCommand(JSONMethods.SEND_PUBLIC_KEY, new SendPublicKeyCommand(encryptionService.getAsymmetricEncryption(), encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.REGISTRATION_RESULT, new RegistrationResultCommand());

        inputEmitter.registerInputListener(commandManager);
        application.getIO().registerInputListener(jsonCommandManager);

        thread.start();
        application.getIO().sendOutput(new RequestPublicKeyObject());
        inputEmitter.start();

    }
}
