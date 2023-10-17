package it.marcodemartino.client.commands;

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
            case CODE_SENT -> {
                logger.info("The verification code was sent to your email.");
                logger.info("Use the command 'verify' to verify yourself");
                logger.info("/verify <email> <verification code> <username>");
            }
            case EMAIL_ALREADY_USED -> {
                logger.info("The email you wrote is already in use!");
            }
        }
    }
}
