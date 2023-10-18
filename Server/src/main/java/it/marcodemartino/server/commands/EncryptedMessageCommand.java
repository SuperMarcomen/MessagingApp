package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.json.EncryptedMessageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptedMessageCommand extends JsonCommand<EncryptedMessageObject> {

    private final Logger logger = LogManager.getLogger(EncryptedMessageObject.class);
    private final EventManager inputEmitter;
    private final AsymmetricEncryption asymmetricEncryption;

    public EncryptedMessageCommand(EventManager inputEmitter, AsymmetricEncryption asymmetricEncryption) {
        super(EncryptedMessageObject.class);
        this.inputEmitter = inputEmitter;
        this.asymmetricEncryption = asymmetricEncryption;
    }

    @Override
    protected void execute(EncryptedMessageObject encryptedMessageObject) {
        logger.info("Received an encrypted message");
        byte[][] encryptedBytes = encryptedMessageObject.getEncryptedMessage();
        String decryptedMessage = asymmetricEncryption.decryptToString(encryptedBytes);
        logger.info("Decrypted message: {}", decryptedMessage);
        inputEmitter.notifyInputListeners(decryptedMessage);
    }
}
