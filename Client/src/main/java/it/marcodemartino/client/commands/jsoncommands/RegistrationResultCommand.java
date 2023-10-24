package it.marcodemartino.client.commands.jsoncommands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.json.RegistrationResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrationResultCommand extends JsonCommand<RegistrationResultObject> {

    private final Logger logger = LogManager.getLogger(RegistrationResultCommand.class);

    public RegistrationResultCommand() {
        super(RegistrationResultObject.class);
    }

    @Override
    protected void execute(RegistrationResultObject registrationResultObject) {
        switch (registrationResultObject.getRegistrationResult()) {
            // possible results when registering your email
            case REGISTRATION_ALREADY_IN_PROGRESS -> logger.info("A verification code was already sent");
            case CODE_SENT -> {
                logger.info("The verification code was sent to your email.");
                logger.info("Use the command 'verify' to verify yourself");
                logger.info("/verify <email> <verification code> <username>");
            }
            case EMAIL_ALREADY_USED -> logger.info("The email you wrote is already in use!");

            // possible results when verifying your email
            case REGISTRATION_NOT_IN_PROGRESS -> logger.info("Before you verify your email you have to register it with the command /register <email>");
            case TIME_OUT_NOTIFICATION -> {
                logger.info("Too many failed attempts! Retry in 30 seconds");
                logger.info("A new code was sent");
            }
            case TIME_OUT -> logger.info("Too many failed attempts! Wait before your next attempt");
            case WRONG_CODE -> logger.info("The code you wrote was wrong!");
            case SUCCESSFUL -> logger.info("The registration was successful");
        }
    }
}
