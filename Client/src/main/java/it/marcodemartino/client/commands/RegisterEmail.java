package it.marcodemartino.client.commands;

import it.marcodemartino.common.commands.UserCommand;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.JSONObject;
import it.marcodemartino.common.json.RegisterEmailObject;

public class RegisterEmail implements UserCommand {

    // Matches "/register <username> <email>" where <email> is a valid email
    private static final String COMMAND_REGEX = "/register\\s+\\w.+\\s+[\\w.-]+@[\\w.-]+\\.\\w+";
    private final OutputEmitter out;
    private String input;

    public RegisterEmail(OutputEmitter out) {
        this.out = out;
    }

    @Override
    public void execute(String input) {
        String[] args = input.split(" ");
        String username = args[0];
        String email = args[1];
        JSONObject jsonObject = new RegisterEmailObject(username, email);
        out.sendOutput(jsonObject);
    }

    @Override
    public boolean isFormatCorrect(String input) {
        return input.matches(COMMAND_REGEX);
    }

    @Override
    public String getCorrectFormat() {
        return "/register <username> <email>";
    }
}
