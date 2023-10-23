package it.marcodemartino.client.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.json.SendPublicKeyOfObject;
import it.marcodemartino.common.services.KeysService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReceivePublicKeyOfCommand extends JsonCommand<SendPublicKeyOfObject> {

    private final Logger logger = LogManager.getLogger(ReceivePublicKeyOfCommand.class);
    private final KeysService keysService;

    public ReceivePublicKeyOfCommand(KeysService keysService) {
        super(SendPublicKeyOfObject.class);
        this.keysService = keysService;
    }

    @Override
    protected void execute(SendPublicKeyOfObject sendPublicKeyOfObject) {
        logger.info("Received a public key of {}", sendPublicKeyOfObject.getEmail());
        keysService.receiveKeyRequestResult(sendPublicKeyOfObject.getEmail(), sendPublicKeyOfObject.getPublicKey());
    }
}
