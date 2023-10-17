package it.marcodemartino.client.commands;

import it.marcodemartino.common.commands.UserCommand;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.JSONObject;
import it.marcodemartino.common.json.RegisterEmailObject;

public class RegisterEmail implements UserCommand {

    // Matches "/register <email>" where <email> is a valid email
    private static final String COMMAND_REGEX = "/register\\s+[\\w.-]+@[\\w.-]+\\.\\w+";
    private final OutputEmitter out;

    public RegisterEmail(OutputEmitter out) {
        this.out = out;
    }

    @Override
    public void execute(String input) {
        String[] args = input.split(" ");
        String email = args[0];
        JSONObject jsonObject = new RegisterEmailObject(email);
        out.sendOutput(jsonObject);
    }

    @Override
    public boolean isFormatCorrect(String input) {
        return input.matches(COMMAND_REGEX);
    }

    @Override
    public String getCorrectFormat() {
        return "/register <email>";
    }
}
