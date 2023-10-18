package it.marcodemartino.client.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.encryption.EncryptionService;
import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.json.EncryptedMessageObject;
import it.marcodemartino.common.json.SignedEncryptedMessageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SignedEncryptedMessageCommand extends JsonCommand<SignedEncryptedMessageObject> {

    private final Logger logger = LogManager.getLogger(EncryptedMessageObject.class);
    private final EventManager inputEmitter;
    private final AsymmetricEncryption asymmetricEncryption;
    private final EncryptionService encryptionService;

    public SignedEncryptedMessageCommand(EventManager inputEmitter, AsymmetricEncryption asymmetricEncryption, EncryptionService encryptionService) {
        super(SignedEncryptedMessageObject.class);
        this.inputEmitter = inputEmitter;
        this.asymmetricEncryption = asymmetricEncryption;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SignedEncryptedMessageObject signedEncryptedMessageObject) {
        logger.info("Received an encrypted message");
        byte[][] encryptedBytes = signedEncryptedMessageObject.getEncryptedMessage();
        String decryptedMessage = asymmetricEncryption.decryptToString(encryptedBytes);
        boolean isSignatureAuthentic = encryptionService.verifyOtherSignature(signedEncryptedMessageObject.getSignature(), decryptedMessage);
        logger.info("Decrypted message: {}. Is signature authentic? {}", decryptedMessage, isSignatureAuthentic);
        inputEmitter.notifyInputListeners(decryptedMessage);
    }
}
