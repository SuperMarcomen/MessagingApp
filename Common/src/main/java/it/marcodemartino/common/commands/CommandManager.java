package it.marcodemartino.common.commands;

import it.marcodemartino.common.io.listeners.InputListener;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements InputListener {

    private final Map<String, Command> commands;

    public CommandManager() {
        commands = new HashMap<>();
    }

    @Override
    public final void notify(String input) {
        if (!input.startsWith("/")) return;
        String firstWord = getFirstWord(input);
        executeCommand(firstWord, input);
    }

    protected void executeCommand(String commandName, String input) {
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(input);
        }
    }

    private static String getFirstWord(String input) {
        return input.split(" ")[0].substring(1);
    }


    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }
}
