package it.marcodemartino.client.commands;

import it.marcodemartino.common.encryption.EncryptionService;
import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.json.SendPublicKeyObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendPublicKeyCommand extends JsonCommand<SendPublicKeyObject> {

    private final Logger logger = LogManager.getLogger(SendPublicKeyObject.class);
    private final AsymmetricKeyConstructor keyConstructor;
    private final EncryptionService encryptionService;


    public SendPublicKeyCommand(AsymmetricKeyConstructor keyConstructor, EncryptionService encryptionService) {
        super(SendPublicKeyObject.class);
        this.keyConstructor = keyConstructor;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SendPublicKeyObject sendPublicKeyObject) {
        logger.info("Received this public key from the server: {}", sendPublicKeyObject.getPublicKey());
        encryptionService.setOtherPublicKey(keyConstructor.constructKeyFromString(sendPublicKeyObject.getPublicKey()));
    }
}
