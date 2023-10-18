package it.marcodemartino.server.services;

import it.marcodemartino.common.dao.IUserDao;
import it.marcodemartino.common.email.EmailProvider;
import it.marcodemartino.common.entities.User;
import it.marcodemartino.server.entities.Registration;

import java.util.*;

public class RegistrationService {

    private static final String EMAIL_SUBJECT = "E2EE verification code";
    private static final String EMAIL_TEXT = "This is your verification code: <b>%d</b>";
    private static final int CODE_LENGTH = 6;
    private final EmailProvider emailProvider;
    private final IUserDao userDao;
    private final Map<String, Registration> registrations;

    public RegistrationService(EmailProvider emailProvider, IUserDao userDao) {
        this.emailProvider = emailProvider;
        this.userDao = userDao;
        this.registrations = new HashMap<>();
    }

    public boolean isEmailAlreadyUsed(String email) {
        return userDao.isEmailUsed(email);
    }

    public boolean isRegistrationInProgress(String email) {
        return registrations.containsKey(email);
    }

    public boolean isUserTimeOut(String email) {
        return registrations.get(email).isTimeout();
    }

    public void sendConfirmationCode(String email) {
        Registration registration = new Registration(CODE_LENGTH);
        registrations.put(email, registration);
        emailProvider.sendEmail(email, EMAIL_SUBJECT, EMAIL_TEXT.formatted(registration.getVerificationCode()));
    }

    public boolean isConfirmationCodeCorrect(String email, int code) {
        Registration registration = registrations.get(email);
        boolean result = registration.validateCode(code);
        if (registration.isNewCodeNeeded()) {
            registration.generateNewCode(CODE_LENGTH);
            emailProvider.sendEmail(email, EMAIL_SUBJECT, EMAIL_TEXT.formatted(registration.getVerificationCode()));
        }
        return result;
    }

    public UUID generateUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (userDao.getByUUID(uuid) != null);
        return uuid;
    }

    public void registerUser(User user) {
        registrations.remove(user.getEmail());
        userDao.insert(user);
    }
}
