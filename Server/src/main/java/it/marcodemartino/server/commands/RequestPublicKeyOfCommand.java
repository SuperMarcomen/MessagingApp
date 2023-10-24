package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.*;
import it.marcodemartino.common.services.EncryptionService;
import it.marcodemartino.common.services.KeysService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class RequestPublicKeyOfCommand extends JsonCommand<RequestPublicKeyOfObject> {

    private final Logger logger = LogManager.getLogger(RequestPublicKeyOfCommand.class);
    private final OutputEmitter out;
    private final KeysService keysService;
    private final EncryptionService encryptionService;

    public RequestPublicKeyOfCommand(OutputEmitter out, KeysService keysService, EncryptionService encryptionService) {
        super(RequestPublicKeyOfObject.class);
        this.out = out;
        this.keysService = keysService;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(RequestPublicKeyOfObject requestPublicKeyOfObject) {
        logger.info("Received request to provide public key of {}", requestPublicKeyOfObject.getRequestOf());
        CompletableFuture<String> pubKeyFuture = new CompletableFuture<>();
        keysService.requestPublicKeyOf(requestPublicKeyOfObject.getRequestOf(), pubKeyFuture);
        pubKeyFuture.thenAccept(publicKey -> {
            logger.info("Found the public key of {}. Sending it...", requestPublicKeyOfObject.getRequestOf());
            JSONObject jsonObject = new SendPublicKeyOfObject(requestPublicKeyOfObject.getRequestOf(), publicKey, encryptionService.signString(publicKey));
            out.sendOutput(jsonObject);
        });
    }
}
