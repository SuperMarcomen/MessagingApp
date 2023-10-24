package it.marcodemartino.common.services;

import java.util.concurrent.CompletableFuture;

public interface KeysService {

    void requestPublicKeyOf(String email, CompletableFuture<String> keyFuture);
    void receiveKeyRequestResult(String email, String result);

}
