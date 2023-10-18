package it.marcodemartino.common.json;

public class EmailVerificationObject implements JSONObject {

    private final JSONMethods method = JSONMethods.EMAIL_VERIFICATION;
    private final String email;
    private final int verificationCode;
    private final String username;
    private final String publicKey;

    public EmailVerificationObject(String email, int verificationCode, String username, String publicKey) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.username = username;
        this.publicKey = publicKey;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getEmail() {
        return email;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public String getUsername() {
        return username;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
