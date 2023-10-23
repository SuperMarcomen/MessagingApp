package it.marcodemartino.common.json;

public class CertifiedMessage implements JSONObject {

    private final JSONMethods method = JSONMethods.CERTIFIED_MESSAGE;
    private final String message;
    private final String toEmail;

    public CertifiedMessage(String message, String toEmail) {
        this.message = message;
        this.toEmail = toEmail;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getMessage() {
        return message;
    }

    public String getToEmail() {
        return toEmail;
    }
}
