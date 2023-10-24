package it.marcodemartino.client.commands.jsoncommands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.json.SendPublicKeyOfObject;
import it.marcodemartino.common.services.EncryptionService;
import it.marcodemartino.common.services.KeysService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReceivePublicKeyOfCommand extends JsonCommand<SendPublicKeyOfObject> {

    private final Logger logger = LogManager.getLogger(ReceivePublicKeyOfCommand.class);
    private final KeysService keysService;
    private final EncryptionService encryptionService;

    public ReceivePublicKeyOfCommand(KeysService keysService, EncryptionService encryptionService) {
        super(SendPublicKeyOfObject.class);
        this.keysService = keysService;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SendPublicKeyOfObject sendPublicKeyOfObject) {
        boolean signatureValid = encryptionService.verifyOtherSignature(sendPublicKeyOfObject.getSignature(), sendPublicKeyOfObject.getPublicKey());

        if (!signatureValid) {
            logger.warn("Received a public key of a user from the server whose signature isn't valid!");
            return;
        }

        logger.info("Received the public key of {}. The signature is valid", sendPublicKeyOfObject.getEmail());
        keysService.receiveKeyRequestResult(sendPublicKeyOfObject.getEmail(), sendPublicKeyOfObject.getPublicKey());
    }
}
