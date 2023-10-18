package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.*;
import it.marcodemartino.server.services.RegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterCommand extends JsonCommand<RegisterEmailObject> {

    private final Logger logger = LogManager.getLogger(RegisterCommand.class);
    private final OutputEmitter outputEmitter;
    private final RegistrationService registrationService;

    public RegisterCommand(OutputEmitter outputEmitter, RegistrationService registrationService) {
        super(RegisterEmailObject.class);
        this.outputEmitter = outputEmitter;
        this.registrationService = registrationService;
    }

    @Override
    protected void execute(RegisterEmailObject object) {
        logger.info("Received a request to register a user with the email {}", object.getEmail());
        String email = object.getEmail();

        RegistrationResult registrationResult;

        if (registrationService.isRegistrationInProgress(email)) {
            registrationResult = RegistrationResult.REGISTRATION_ALREADY_IN_PROGRESS;
        } else if (registrationService.isEmailAlreadyUsed(object.getEmail())) {
            registrationResult = RegistrationResult.EMAIL_ALREADY_USED;
        } else {
            registrationService.sendConfirmationCode(object.getEmail());
            registrationResult = RegistrationResult.CODE_SENT;
        }

        JSONObject jsonObject = new RegistrationResultObject(registrationResult);
        outputEmitter.sendOutput(jsonObject);
    }
}
