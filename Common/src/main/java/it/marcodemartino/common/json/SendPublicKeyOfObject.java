package it.marcodemartino.common.json;

public class SendPublicKeyOfObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_PUBLIC_KEY_OF;
    private final String email;
    private final String publicKey;
    private final byte[][] signature;

    public SendPublicKeyOfObject(String email, String publicKey, byte[][] signature) {
        this.email = email;
        this.publicKey = publicKey;
        this.signature = signature;
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

    public byte[][] getSignature() {
        return signature;
    }
}
