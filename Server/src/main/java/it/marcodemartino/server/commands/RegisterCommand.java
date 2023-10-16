package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.json.RegisterEmailObject;
import it.marcodemartino.server.services.RegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterCommand extends JsonCommand<RegisterEmailObject> {

    private final Logger logger = LogManager.getLogger(RegisterCommand.class);
    private final RegistrationService registrationService;

    public RegisterCommand(RegistrationService registrationService) {
        super(RegisterEmailObject.class);
        this.registrationService = registrationService;
    }

    @Override
    protected void execute(RegisterEmailObject object) {
        logger.info("Received a request to register {} with the email {}", object.getUsername(), object.getEmail());
        if (registrationService.isEmailAlreadyUsed(object.getEmail())) {

        }
        registrationService.sendConfirmationCode(object.getEmail());
    }
}
