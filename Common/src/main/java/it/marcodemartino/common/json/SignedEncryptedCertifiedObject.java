package it.marcodemartino.common.json;

import it.marcodemartino.common.certificates.IdentityCertificate;

public class SignedEncryptedCertifiedObject implements JSONObject {

    private final JSONMethods method = JSONMethods.ENCRYPTED_SIGNED_CERTIFIED_MESSAGE;
    private final byte[][] encryptedMessage;
    private final byte[][] signature;
    private final String sendTo;
    private final IdentityCertificate identityCertificate;

    public SignedEncryptedCertifiedObject(byte[][] encryptedMessage, byte[][] signature, String sendTo, IdentityCertificate identityCertificate) {
        this.encryptedMessage = encryptedMessage;
        this.signature = signature;
        this.sendTo = sendTo;
        this.identityCertificate = identityCertificate;
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

    public String getSendTo() {
        return sendTo;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
