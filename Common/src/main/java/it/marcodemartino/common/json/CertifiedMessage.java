package it.marcodemartino.common.json;

import it.marcodemartino.common.entities.User;

public class CertifiedMessage implements JSONObject {

    private final JSONMethods method = JSONMethods.CERTIFIED_MESSAGE;
    private final String message;
    private final User fromUser;

    public CertifiedMessage(String message, User fromUser) {
        this.message = message;
        this.fromUser = fromUser;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getMessage() {
        return message;
    }

    public User getFromUser() {
        return fromUser;
    }
}
