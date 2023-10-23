package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.SendIdentityCertificateObject;
import it.marcodemartino.common.services.EncryptionService;
import it.marcodemartino.server.services.MessagingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReceiveIdentityCertificateCommand extends JsonCommand<SendIdentityCertificateObject> {

    private final Logger logger = LogManager.getLogger(ReceiveIdentityCertificateCommand.class);
    private final OutputEmitter out;
    private final EncryptionService encryptionService;
    private final MessagingService messagingService;

    public ReceiveIdentityCertificateCommand(OutputEmitter out, EncryptionService encryptionService, MessagingService messagingService) {
        super(SendIdentityCertificateObject.class);
        this.out = out;
        this.encryptionService = encryptionService;
        this.messagingService = messagingService;
    }

    @Override
    protected void execute(SendIdentityCertificateObject sendIdentityCertificateObject) {
        boolean certificateValid = encryptionService.verifyIdentityCertificate(sendIdentityCertificateObject.getIdentityCertificate(), true);
        logger.info("Received a certificate from {}. Valid: {}", sendIdentityCertificateObject.getIdentityCertificate().getUser().getEmail(), certificateValid);
        if (certificateValid) {
            messagingService.addClient(sendIdentityCertificateObject.getIdentityCertificate().getUser().getEmail(), out);
        }
    }
}
