package it.marcodemartino.server.handler;

import com.google.gson.Gson;
import it.marcodemartino.common.application.ApplicationIO;
import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.io.listeners.InputListener;
import it.marcodemartino.common.json.GsonInstance;
import it.marcodemartino.common.json.JSONObject;
import it.marcodemartino.server.services.MessagingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ClientHandlerIO implements ApplicationIO {

    private final Logger logger = LogManager.getLogger(ClientHandlerIO.class);
    private final BufferedReader in;
    private final PrintWriter out;
    private final EventManager eventManager;
    private final MessagingService messagingService;
    private final Gson gson;
    private boolean running;

    public ClientHandlerIO(InputStream inputStream, OutputStream outputStream, MessagingService messagingService) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = new PrintWriter(outputStream, true);
        this.messagingService = messagingService;
        eventManager = new EventManager();
        gson = GsonInstance.get();
        running = true;
    }

    @Override
    public void sendOutput(JSONObject object) {
        String output = gson.toJson(object);
        logger.info("Sending this message to the server: {}", object.getMethod());
        out.println(output);
    }

    @Override
    public void start() {
        logger.info("Started listening for inputs from the client");
        while (running) {
            String input = getInput();
            if (!running) return;
            logger.info("Received the input: {}", input);
            eventManager.notifyInputListeners(input);
        }
    }

    @Override
    public void stop() {
        messagingService.disconnectClient(this);
        running = false;
        tryClose();
        out.close();
    }

    @Override
    public void registerInputListener(InputListener inputListener) {
        eventManager.registerInputListener(inputListener);
    }

    private String getInput() {
        try {
            return in.readLine();
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) {
                logger.warn("The client disconnected suddenly! Closing the socket");
                stop();
                return "";
            }
            throw new RuntimeException(e);
        }
    }

    private void tryClose() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }
}
