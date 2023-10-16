package it.marcodemartino.server.commands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.*;

import java.security.PublicKey;

public class RequestPublicKeyCommand extends JsonCommand<RequestPublicKeyObject> {

    private final AsymmetricKeyConstructor keyConstructor;
    private final PublicKey publicKey;
    private final OutputEmitter outputEmitter;

    public RequestPublicKeyCommand(AsymmetricKeyConstructor keyConstructor, PublicKey publicKey, OutputEmitter outputEmitter) {
        super(RequestPublicKeyObject.class);
        this.keyConstructor = keyConstructor;
        this.publicKey = publicKey;
        this.outputEmitter = outputEmitter;
    }

    @Override
    protected void execute(RequestPublicKeyObject requestPublicKeyObject) {
        JSONObject jsonObject = new SendPublicKeyObject(keyConstructor.publicKeyToString(publicKey));
        outputEmitter.sendOutput(jsonObject);
    }
}
