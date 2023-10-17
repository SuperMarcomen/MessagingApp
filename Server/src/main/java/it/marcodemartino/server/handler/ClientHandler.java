package it.marcodemartino.server.handler;

import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.application.ApplicationIO;
import it.marcodemartino.common.commands.JsonCommandManager;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.json.JSONMethods;
import it.marcodemartino.server.commands.RegisterCommand;
import it.marcodemartino.server.commands.RequestPublicKeyCommand;
import it.marcodemartino.server.services.RegistrationService;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Application {

    private final Socket socket;
    private final ApplicationIO applicationIO;

    public ClientHandler(Socket socket, AsymmetricEncryption asymmetricEncryption, RegistrationService registrationService) throws IOException {
        this.socket = socket;
        this.applicationIO = new ClientHandlerIO(socket.getInputStream(), socket.getOutputStream());

        JsonCommandManager commandManager = new JsonCommandManager();
        commandManager.registerCommand(JSONMethods.REGISTER_EMAIL, new RegisterCommand(applicationIO, registrationService));
        commandManager.registerCommand(JSONMethods.REQUEST_PUBLIC_KEY, new RequestPublicKeyCommand(asymmetricEncryption, asymmetricEncryption.getPublicKey(), applicationIO));
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
