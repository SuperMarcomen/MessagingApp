package it.marcodemartino.client.commands.jsoncommands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.json.SignedEncryptedCertifiedObject;
import it.marcodemartino.common.services.EncryptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PublicKey;

public class SignedEncryptedCertifiedCommand extends JsonCommand<SignedEncryptedCertifiedObject> {

    private final Logger logger = LogManager.getLogger(SignedEncryptedCertifiedCommand.class);
    private final AsymmetricKeyConstructor keyConstructor;
    private final EventManager eventManager;
    private final EncryptionService encryptionService;

    public SignedEncryptedCertifiedCommand(AsymmetricKeyConstructor keyConstructor, EventManager eventManager, EncryptionService encryptionService) {
        super(SignedEncryptedCertifiedObject.class);
        this.keyConstructor = keyConstructor;
        this.eventManager = eventManager;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SignedEncryptedCertifiedObject signedEncryptedCertifiedObject) {
        String decryptedMessage = encryptionService.decryptMessage(signedEncryptedCertifiedObject.getEncryptedMessage());
        boolean certificateValid = encryptionService.verifyIdentityCertificate(signedEncryptedCertifiedObject.getIdentityCertificate(), false);

        if (!certificateValid) {
            logger.warn("Received a user message with an invalid identity certificate!");
            return;
        }

        PublicKey otherPubKey = keyConstructor.constructKeyFromString(signedEncryptedCertifiedObject.getIdentityCertificate().getUser().getPublicKey());
        boolean signatureValid = encryptionService.verifyOtherSignature(signedEncryptedCertifiedObject.getSignature(), decryptedMessage, otherPubKey);

        if (!signatureValid) {
            logger.warn("Received a user message with an invalid signature!");
            return;
        }

        logger.info("Received an encrypted message. The certificate and the signature are valid");
        eventManager.notifyInputListeners(decryptedMessage);
    }
}
