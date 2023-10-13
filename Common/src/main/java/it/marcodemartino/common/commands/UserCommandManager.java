package it.marcodemartino.common.commands;

import it.marcodemartino.common.errors.ErrorManager;

import java.util.HashMap;
import java.util.Map;

public class UserCommandManager extends CommandManager {

    private final Map<String, UserCommand> userCommands;
    private final ErrorManager errorManager;

    public UserCommandManager(ErrorManager errorManager) {
        super();
        this.errorManager = errorManager;
        userCommands = new HashMap<>();
    }

    @Override
    protected void executeCommand(String commandName, String input) {
        UserCommand userCommand = userCommands.get(commandName);
        if (userCommand != null) {
            executeUserCommand(userCommand, input, getArgs(commandName, input));
            return;
        }

        super.executeCommand(commandName, input);
    }

    private void executeUserCommand(UserCommand command, String input, String args) {
        if (!command.isFormatCorrect(input)) {
            errorManager.wrongCommandFormat(command.getCorrectFormat());
            return;
        }

        command.execute(args);
    }

    private String getArgs(String firstWord, String input) {
        return input.substring(firstWord.length() + 2);
    }

    public void registerUserCommand(String name, UserCommand userCommand) {
        userCommands.put(name, userCommand);
    }
}
