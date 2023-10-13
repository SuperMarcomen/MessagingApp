package it.marcodemartino.client;

import it.marcodemartino.client.commands.RegisterEmail;
import it.marcodemartino.client.errrors.ConsoleErrorManager;
import it.marcodemartino.client.inputs.ConsoleInputEmitter;
import it.marcodemartino.client.socket.SSLSocketClient;
import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.commands.UserCommandManager;
import it.marcodemartino.common.io.emitters.InputEmitter;

public class MessagingApp {

    public static void main(String[] args) throws Exception {
        Application application = new SSLSocketClient("127.0.0.1", 8443);
        Thread thread = new Thread(application);
        thread.start();

        InputEmitter inputEmitter = new ConsoleInputEmitter();
        UserCommandManager commandManager = new UserCommandManager(new ConsoleErrorManager());
        commandManager.registerUserCommand("register", new RegisterEmail(application.getIO()));
        inputEmitter.registerInputListener(commandManager);
        inputEmitter.start();
    }
}
