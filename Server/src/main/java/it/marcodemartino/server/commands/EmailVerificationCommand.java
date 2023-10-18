package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.entities.User;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.*;
import it.marcodemartino.server.services.RegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PublicKey;

public class EmailVerificationCommand extends JsonCommand<EmailVerificationObject> {

    private final Logger logger = LogManager.getLogger(EmailVerificationCommand.class);
    private final AsymmetricKeyConstructor asymmetricKeyConstructor;
    private final OutputEmitter outputEmitter;
    private final RegistrationService registrationService;

    public EmailVerificationCommand(AsymmetricKeyConstructor asymmetricKeyConstructor, OutputEmitter outputEmitter, RegistrationService registrationService) {
        super(EmailVerificationObject.class);
        this.asymmetricKeyConstructor = asymmetricKeyConstructor;
        this.outputEmitter = outputEmitter;
        this.registrationService = registrationService;
    }

    @Override
    protected void execute(EmailVerificationObject emailVerificationObject) {
        String email = emailVerificationObject.getEmail();
        PublicKey publicKey = asymmetricKeyConstructor.constructKeyFromString(emailVerificationObject.getPublicKey());
        String username = emailVerificationObject.getEmail();
        int verificationCode = emailVerificationObject.getVerificationCode();
        logger.info("Received verification code from {}: {}", email, verificationCode);

        RegistrationResult registrationResult;

        if (registrationService.isEmailAlreadyUsed(email)) {
            registrationResult = RegistrationResult.EMAIL_ALREADY_USED;
        } else if (!registrationService.isRegistrationInProgress(email)) {
            registrationResult = RegistrationResult.REGISTRATION_NOT_IN_PROGRESS;
        } else if (registrationService.isUserTimeOut(email)) {
            registrationResult = RegistrationResult.TIME_OUT;
        } else if (registrationService.isConfirmationCodeCorrect(email, verificationCode)) {
            registrationResult = RegistrationResult.SUCCESSFUL;
            User user = new User(username, email, registrationService.generateUUID(), publicKey);
            registrationService.registerUser(user);
        } else {
            registrationResult = RegistrationResult.WRONG_CODE;
            if (registrationService.isUserTimeOut(email)) registrationResult = RegistrationResult.TIME_OUT_NOTIFICATION;
        }

        JSONObject jsonObject = new RegistrationResultObject(registrationResult);
        outputEmitter.sendOutput(jsonObject);
    }
}
