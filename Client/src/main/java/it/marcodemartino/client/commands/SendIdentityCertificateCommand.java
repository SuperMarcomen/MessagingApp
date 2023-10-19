package it.marcodemartino.client.commands;

import it.marcodemartino.client.certificates.CertificateWriter;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.EncryptionService;
import it.marcodemartino.common.json.SendIdentityCertificateObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public class SendIdentityCertificateCommand extends JsonCommand<SendIdentityCertificateObject> {

    private final Logger logger = LogManager.getLogger(SendIdentityCertificateCommand.class);
    private final CertificateWriter certificateWriter;
    private final EncryptionService encryptionService;

    public SendIdentityCertificateCommand(CertificateWriter certificateWriter, EncryptionService encryptionService) {
        super(SendIdentityCertificateObject.class);
        this.certificateWriter = certificateWriter;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SendIdentityCertificateObject sendIdentityCertificateObject) {
        IdentityCertificate identityCertificate = sendIdentityCertificateObject.getIdentityCertificate();
        boolean isSignatureAuthentic = encryptionService.verifyIdentityCertificate(identityCertificate);
        logger.info("Received a certificate for the email: {}. Is it authentic? {}", identityCertificate.getUser().getEmail(), isSignatureAuthentic);
        // TODO save certificate to file and load on startup
        certificateWriter.writeCertificate(identityCertificate, Paths.get(""));
    }
}
