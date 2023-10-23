package it.marcodemartino.client.certificates;

public interface CertificateReaderWriter extends CertificateReader, CertificateWriter {

    boolean doesCertificateExist();

}
