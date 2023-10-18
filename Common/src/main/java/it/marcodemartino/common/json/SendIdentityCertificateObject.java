package it.marcodemartino.common.json;

import it.marcodemartino.common.certificates.IdentityCertificate;

public class SendIdentityCertificateObject implements JSONObject {

    private final JSONMethods method = JSONMethods.IDENTITY_CERTIFICATE;
    private final IdentityCertificate identityCertificate;

    public SendIdentityCertificateObject(IdentityCertificate identityCertificate) {
        this.identityCertificate = identityCertificate;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
