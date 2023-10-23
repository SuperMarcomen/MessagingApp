package it.marcodemartino.client.certificates;

import it.marcodemartino.common.certificates.IdentityCertificate;

public interface CertificateWriter {

    void writeCertificate(IdentityCertificate identityCertificate);

}
