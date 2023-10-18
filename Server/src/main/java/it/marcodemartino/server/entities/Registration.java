package it.marcodemartino.server.entities;

public class Registration {


    private static final int MAX_WRONG_TRIES = 3;
    private static final int TIMEOUT_SECONDS = 30;
    private int verificationCode;
    private long timeLastRequest;
    private int wrongTries;
    private boolean newCodeNeeded;

    public Registration(int length) {
        this.verificationCode = generateRandomNumber(length);
        this.timeLastRequest = 0;
        this.wrongTries = 0;
    }
    
    public boolean isTimeout() {
        if (timeLastRequest == 0) return false;
        long now = System.currentTimeMillis() / 1000L;
        long secondsPassed = now - timeLastRequest;
        return secondsPassed < TIMEOUT_SECONDS;
    }

    public boolean validateCode(int verificationCode) {
        if (this.verificationCode == verificationCode) {
            return true;
        }
        increaseTries();
        return false;
    }

    public void generateNewCode(int length) {
        verificationCode = generateRandomNumber(length);
        newCodeNeeded = false;
    }

    private void increaseTries() {
        wrongTries++;
        if (wrongTries < MAX_WRONG_TRIES) return;
        timeLastRequest = System.currentTimeMillis() / 1000L;
        newCodeNeeded = true;
        wrongTries = 0;
    }

    private int generateRandomNumber(int length) {
        return (int) (Math.random() * Math.pow(10, length));
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public boolean isNewCodeNeeded() {
        return newCodeNeeded;
    }
}
