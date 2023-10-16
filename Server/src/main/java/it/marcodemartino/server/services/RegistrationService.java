package it.marcodemartino.server.services;

import it.marcodemartino.common.dao.IUserDao;
import it.marcodemartino.common.email.EmailProvider;
import it.marcodemartino.common.entities.User;

import java.util.HashMap;
import java.util.Map;

public class RegistrationService {

    private static final String EMAIL_SUBJECT = "E2EE verification code";
    private static final String EMAIL_TEXT = "This is your verification code: <b>%d</b>";
    private final EmailProvider emailProvider;
    private final IUserDao userDao;
    private final Map<String, Integer> confirmationCodes;


    public RegistrationService(EmailProvider emailProvider, IUserDao userDao) {
        this.emailProvider = emailProvider;
        this.userDao = userDao;
        this.confirmationCodes = new HashMap<>();
    }

    public boolean isEmailAlreadyUsed(String email) {
        return userDao.isEmailUsed(email);
    }

    public void sendConfirmationCode(String email) {
        int confirmationCode = generateRandomNumber(6);
        emailProvider.sendEmail(email, EMAIL_SUBJECT, EMAIL_TEXT.formatted(confirmationCode));
        confirmationCodes.put(email, confirmationCode);
    }

    public boolean isConfirmationCodeCorrect(String email, int code) {
        int realCode = confirmationCodes.get(email);
        return realCode == code;
    }


    public void registerUser(User user) {
        userDao.insert(user);
    }

    private int generateRandomNumber(int length) {
        return (int) (Math.random() * Math.pow(10, length));
    }
}
