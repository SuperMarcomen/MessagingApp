package it.marcodemartino.client.certificates;

import it.marcodemartino.common.certificates.IdentityCertificate;

import java.nio.file.Path;

public interface CertificateWriter {

    void writeCertificate(IdentityCertificate identityCertificate, Path path);

}
