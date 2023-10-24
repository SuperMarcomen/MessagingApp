package it.marcodemartino.common.json;

import it.marcodemartino.common.certificates.IdentityCertificate;

public class SendIdentityCertificateObject implements JSONObject {

    private final JSONMethods method = JSONMethods.IDENTITY_CERTIFICATE;
    private final IdentityCertificate identityCertificate;
    private final byte[][] signature;

    public SendIdentityCertificateObject(IdentityCertificate identityCertificate, byte[][] signature) {
        this.identityCertificate = identityCertificate;
        this.signature = signature;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }

    public byte[][] getSignature() {
        return signature;
    }
}
