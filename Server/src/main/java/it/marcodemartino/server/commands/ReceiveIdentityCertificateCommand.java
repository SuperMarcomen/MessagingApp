package it.marcodemartino.server.commands;

import com.google.gson.Gson;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.GsonInstance;
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
    private final Gson gson;

    public ReceiveIdentityCertificateCommand(OutputEmitter out, EncryptionService encryptionService, MessagingService messagingService) {
        super(SendIdentityCertificateObject.class);
        this.out = out;
        this.encryptionService = encryptionService;
        this.messagingService = messagingService;
        this.gson = GsonInstance.get();
    }

    @Override
    protected void execute(SendIdentityCertificateObject sendIdentityCertificateObject) {
        IdentityCertificate identityCertificate = sendIdentityCertificateObject.getIdentityCertificate();
        boolean certificateValid = encryptionService.verifyIdentityCertificate(identityCertificate, true);

        if (!certificateValid) {
            logger.warn("Received an invalid identity certificate!");
            return;
        }

        AsymmetricKeyConstructor keyConstructor = encryptionService.getLocalAsymmetricEncryption();
        String json = gson.toJson(identityCertificate);
        boolean signatureValid = encryptionService.verifyOtherSignature(sendIdentityCertificateObject.getSignature(), json, keyConstructor.constructKeyFromString(identityCertificate.getUser().getPublicKey()));

        if (!signatureValid) {
            logger.warn("Received an identity certificate with an invalid user signature! (It was not sent by the owner)");
            return;
        }

        logger.info("Received a certificate from {}. The server signature and user signature are both valid", identityCertificate.getUser().getEmail());
        messagingService.addClient(identityCertificate.getUser().getEmail(), out);

    }
}
