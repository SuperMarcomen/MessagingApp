package it.marcodemartino.client.commands;

import it.marcodemartino.client.services.CertificateService;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.json.SendIdentityCertificateObject;
import it.marcodemartino.common.services.EncryptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendIdentityCertificateCommand extends JsonCommand<SendIdentityCertificateObject> {

    private final Logger logger = LogManager.getLogger(SendIdentityCertificateCommand.class);
    private final CertificateService certificateService;
    private final EncryptionService encryptionService;

    public SendIdentityCertificateCommand(CertificateService certificateService, EncryptionService encryptionService) {
        super(SendIdentityCertificateObject.class);
        this.certificateService = certificateService;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SendIdentityCertificateObject sendIdentityCertificateObject) {
        IdentityCertificate identityCertificate = sendIdentityCertificateObject.getIdentityCertificate();
        boolean isSignatureAuthentic = encryptionService.verifyIdentityCertificate(identityCertificate, false);
        logger.info("Received a certificate for the email: {}. Is it authentic? {}", identityCertificate.getUser().getEmail(), isSignatureAuthentic);
        // TODO save certificate to file and load on startup
        certificateService.writeCertificate(identityCertificate);
    }
}
