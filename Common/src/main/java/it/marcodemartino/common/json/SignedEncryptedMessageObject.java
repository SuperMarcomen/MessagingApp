package it.marcodemartino.common.json;

public class SignedEncryptedMessageObject implements JSONObject {

    private final JSONMethods method = JSONMethods.ENCRYPTED_SIGNED_MESSAGE;
    private final byte[][] encryptedMessage;
    private final byte[][] signature;

    public SignedEncryptedMessageObject(byte[][] encryptedMessage, byte[][] signature) {
        this.encryptedMessage = encryptedMessage;
        this.signature = signature;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public byte[][] getEncryptedMessage() {
        return encryptedMessage;
    }

    public byte[][] getSignature() {
        return signature;
    }
}
