package it.marcodemartino.client.certificates;

import it.marcodemartino.common.certificates.IdentityCertificate;

import java.nio.file.Path;

public interface CertificateReader {

    IdentityCertificate readCertificate(Path path);

}
