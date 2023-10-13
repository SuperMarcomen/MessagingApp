package it.marcodemartino.client.inputs;

import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.io.emitters.InputEmitter;
import it.marcodemartino.common.io.listeners.InputListener;

import java.util.Scanner;

public class ConsoleInputEmitter implements InputEmitter {

    private final Scanner scanner;
    private final EventManager eventManager;
    private boolean running;

    public ConsoleInputEmitter() {
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.eventManager = new EventManager();
    }

    @Override
    public void start() {
        while (running) {
            String input = scanner.nextLine();
            eventManager.notifyInputListeners(input);
        }
    }

    @Override
    public void registerInputListener(InputListener inputListener) {
        eventManager.registerInputListener(inputListener);
    }

    @Override
    public void stop() {
        running = false;
        scanner.close();
    }

    public boolean isRunning() {
        return running;
    }
}
