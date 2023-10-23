package it.marcodemartino.client.services;

import it.marcodemartino.client.certificates.CertificateReaderWriter;
import it.marcodemartino.common.certificates.IdentityCertificate;

public class CertificateService {

    private final CertificateReaderWriter certificateReaderWriter;
    private IdentityCertificate identityCertificate;

    public CertificateService(CertificateReaderWriter certificateReaderWriter) {
        this.certificateReaderWriter = certificateReaderWriter;
        if (certificateReaderWriter.doesCertificateExist()) {
            identityCertificate = certificateReaderWriter.readCertificate();
        }
    }

    public void writeCertificate(IdentityCertificate identityCertificate) {
        this.identityCertificate = identityCertificate;
        certificateReaderWriter.writeCertificate(identityCertificate);
    }

    public boolean doesCertificateExist() {
        return identityCertificate != null;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
