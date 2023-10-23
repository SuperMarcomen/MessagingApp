package it.marcodemartino.client.certificates;

import it.marcodemartino.common.certificates.IdentityCertificate;

public interface CertificateReader {

    IdentityCertificate readCertificate();

}
