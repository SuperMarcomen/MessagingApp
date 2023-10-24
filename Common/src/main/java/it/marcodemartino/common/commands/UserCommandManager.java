package it.marcodemartino.common.commands;

import it.marcodemartino.common.errors.ErrorManager;
import it.marcodemartino.common.io.listeners.InputListener;

import java.util.HashMap;
import java.util.Map;

public class UserCommandManager implements InputListener {

    private final Map<String, UserCommand> userCommands;
    private final ErrorManager errorManager;

    public UserCommandManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
        userCommands = new HashMap<>();
    }

    @Override
    public final void notify(String input) {
        if (!input.startsWith("/")) {
            errorManager.wrongCommandFormat();
            return;
        }
        String firstWord = getFirstWord(input);
        executeCommand(firstWord, input);
    }

    private void executeCommand(String commandName, String input) {
        UserCommand userCommand = userCommands.get(commandName);
        if (userCommand == null) {
            errorManager.commandNotRecognized(commandName);
            return;
        }
        checkFormatAndExecute(userCommand, input, getArgs(commandName, input));
    }

    private static String getFirstWord(String input) {
        return input.split(" ")[0].substring(1);
    }

    private void checkFormatAndExecute(UserCommand command, String input, String args) {
        if (!command.isFormatCorrect(input)) {
            errorManager.wrongCommandInputFormat(command.getCorrectFormat());
            return;
        }

        command.execute(args);
    }

    private String getArgs(String firstWord, String input) {
        if (firstWord.length() + 1 >= input.length()) return "";
        return input.substring(firstWord.length() + 2);
    }

    public void registerUserCommand(String name, UserCommand userCommand) {
        userCommands.put(name, userCommand);
    }
}
