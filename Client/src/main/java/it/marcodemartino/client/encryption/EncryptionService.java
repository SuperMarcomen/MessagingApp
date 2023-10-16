package it.marcodemartino.client.encryption;

import it.marcodemartino.common.encryption.*;

import java.nio.file.*;
import java.security.KeyPair;
import java.security.PublicKey;

public class EncryptionService {

    private final AsymmetricEncryption asymmetricEncryption;
    private final AsymmetricKeyWriter keyWriter;
    private final AsymmetricKeyReader keyReader;
    private PublicKey serverPublicKey;

    public EncryptionService() {
        asymmetricEncryption = new RSAEncryption(2048);
        keyWriter = new AsymmetricKeyFileWriter();
        keyReader = new AsymmetricKeyFileReader();
        loadOrGenerateKeys();
    }

    private void loadOrGenerateKeys() {
        if (doKeysExist()) {
            asymmetricEncryption.setKeys(keyReader.readKeyPair("public_key.pem", "private_key.pem"));
        } else {
            asymmetricEncryption.generateKeyPair();
            KeyPair keyPair = new KeyPair(asymmetricEncryption.getPublicKey(), asymmetricEncryption.getPrivateKey());
            keyWriter.writeToFile(keyPair, Paths.get(""));
        }
    }


    private boolean doKeysExist() {
        return Files.exists(Paths.get("public_key.pem")) && Files.exists(Paths.get("private_key.pem"));
    }

    public void setServerPublicKey(PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public AsymmetricEncryption getAsymmetricEncryption() {
        return asymmetricEncryption;
    }
}
