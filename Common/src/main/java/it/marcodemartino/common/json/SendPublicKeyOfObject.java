package it.marcodemartino.common.json;

public class SendPublicKeyOfObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_PUBLIC_KEY_OF;
    private final String email;
    private final String publicKey;

    public SendPublicKeyOfObject(String email, String publicKey) {
        this.email = email;
        this.publicKey = publicKey;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getEmail() {
        return email;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
