package it.marcodemartino.client.socket.io;

import com.google.gson.Gson;
import it.marcodemartino.common.application.ApplicationIO;
import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.io.listeners.InputListener;
import it.marcodemartino.common.json.GsonInstance;
import it.marcodemartino.common.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class SocketIO implements ApplicationIO {

    private static final String QUIT_MESSAGE = "quit";
    private final Logger logger = LogManager.getLogger(SocketIO.class);
    private final EventManager eventManager;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Gson gson;
    private boolean running;

    public SocketIO(InputStream inputStream, OutputStream outputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = new PrintWriter(outputStream, true);
        this.eventManager = new EventManager();
        this.gson = GsonInstance.get();
        this.running = true;
    }

    @Override
    public void start() {
        logger.info("Starting to listen for inputs from the server");
        while (running) {
            String input = tryGetInput();
            if (input.equals(QUIT_MESSAGE)) return;

            eventManager.notifyInputListeners(input);
        }
    }

    @Override
    public void stop() {
        running = false;
        tryToCloseInput();
        out.close();
    }

    @Override
    public void sendOutput(JSONObject object) {
        String output = gson.toJson(object);
        out.println(output);
    }

    @Override
    public void registerInputListener(InputListener inputListener) {
        eventManager.registerInputListener(inputListener);
    }

    private String tryGetInput() {
        try {
            return in.readLine();
        } catch (IOException e) {
            if (!running) return QUIT_MESSAGE;
            if (e.getMessage().equals("Connection reset")) {
                logger.warn("The server shut down unexpectedly! Closing the app");
                System.exit(1);
            }
            throw new RuntimeException(e);
        }
    }

    private void tryToCloseInput() {
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
