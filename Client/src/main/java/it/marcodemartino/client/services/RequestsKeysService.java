package it.marcodemartino.client.services;

import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.JSONObject;
import it.marcodemartino.common.json.RequestPublicKeyOfObject;
import it.marcodemartino.common.services.KeysService;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RequestsKeysService implements KeysService {

    private final OutputEmitter out;
    private final AsymmetricKeyConstructor asymmetricKeyConstructor;
    private final Map<String, PublicKey> emailPubKeyMap;
    private final Map<String, CompletableFuture<String>> emailPubKeyRequestsMap;

    public RequestsKeysService(OutputEmitter out, AsymmetricKeyConstructor asymmetricKeyConstructor) {
        this.out = out;
        this.asymmetricKeyConstructor = asymmetricKeyConstructor;
        emailPubKeyMap = new HashMap<>();
        emailPubKeyRequestsMap = new HashMap<>();
    }

    @Override
    public void getPublicKeyOf(String email, CompletableFuture<String> keyFuture) {
        if (emailPubKeyMap.containsKey(email)) {
            keyFuture.complete(asymmetricKeyConstructor.publicKeyToString(emailPubKeyMap.get(email)));
            emailPubKeyMap.get(email);
            return;
        }
        requestPublicKey(email, keyFuture);
    }

    @Override
    public void receiveKeyRequestResult(String email, String result) {
        CompletableFuture<String> keyFeature = emailPubKeyRequestsMap.get(email);
        if (result == null) {
            keyFeature.complete(null);
            return;
        }

        PublicKey publicKey = asymmetricKeyConstructor.constructKeyFromString(result);
        keyFeature.complete(result);
        emailPubKeyMap.put(email, publicKey);
    }

    private void requestPublicKey(String email, CompletableFuture<String> keyFuture) {
        emailPubKeyRequestsMap.put(email, keyFuture);
        JSONObject jsonObject = new RequestPublicKeyOfObject(email);
        out.sendOutput(jsonObject);
    }
}
