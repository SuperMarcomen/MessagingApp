package it.marcodemartino.server.services;

import it.marcodemartino.common.dao.IUserDao;
import it.marcodemartino.common.services.KeysService;

import java.util.concurrent.CompletableFuture;

public class DatabaseKeysService implements KeysService {

    private final IUserDao userDao;

    public DatabaseKeysService(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void requestPublicKeyOf(String email, CompletableFuture<String> keyFuture) {
        String publicKey = userDao.getByEmail(email).getPublicKey();
        keyFuture.complete(publicKey);
    }

    @Override
    public void receiveKeyRequestResult(String email, String result) {

    }
}
