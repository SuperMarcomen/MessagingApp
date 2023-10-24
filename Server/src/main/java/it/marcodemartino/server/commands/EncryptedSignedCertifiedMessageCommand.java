package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.SignedEncryptedCertifiedObject;
import it.marcodemartino.server.services.MessagingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptedSignedCertifiedMessageCommand extends JsonCommand<SignedEncryptedCertifiedObject> {

    private final Logger logger = LogManager.getLogger(EncryptedSignedCertifiedMessageCommand.class);
    private final MessagingService messagingService;

    public EncryptedSignedCertifiedMessageCommand(MessagingService messagingService) {
        super(SignedEncryptedCertifiedObject.class);
        this.messagingService = messagingService;
    }

    @Override
    protected void execute(SignedEncryptedCertifiedObject signedEncryptedCertifiedObject) {
        logger.info("Received an encrypted message for {}", signedEncryptedCertifiedObject.getSendTo());
        OutputEmitter out = messagingService.getClient(signedEncryptedCertifiedObject.getSendTo());
        if (out == null) {
            logger.info("The client is not online, storing the message");
            messagingService.storeMessage(signedEncryptedCertifiedObject);
            return;
        }
        out.sendOutput(signedEncryptedCertifiedObject);
    }
}
