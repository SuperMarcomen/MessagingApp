package it.marcodemartino.common.json;

public class SendPublicKeyObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_PUBLIC_KEY;
    private final String publicKey;

    public SendPublicKeyObject(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
