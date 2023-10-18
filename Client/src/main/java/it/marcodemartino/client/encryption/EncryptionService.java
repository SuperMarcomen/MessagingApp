package it.marcodemartino.client.encryption;

import com.google.gson.Gson;
import it.marcodemartino.common.encryption.*;
import it.marcodemartino.common.json.*;

import java.nio.file.*;
import java.security.KeyPair;
import java.security.PublicKey;

public class EncryptionService {

    private final AsymmetricEncryption asymmetricEncryption;
    private final AsymmetricEncryption serverEncryption;
    private final AsymmetricKeyWriter keyWriter;
    private final AsymmetricKeyReader keyReader;
    private final Gson gson;

    public EncryptionService() {
        asymmetricEncryption = new RSAEncryption(2048);
        serverEncryption = new RSAEncryption(2048);
        keyWriter = new AsymmetricKeyFileWriter();
        keyReader = new AsymmetricKeyFileReader();
        loadOrGenerateKeys();
        gson = GsonInstance.get();
    }

    public JSONObject encryptMessage(JSONObject jsonObject) {
        String json = gson.toJson(jsonObject);
        byte[][] encryptedJson = serverEncryption.encryptFromString(json);
        return new EncryptedMessageObject(encryptedJson);
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

    public PublicKey getClientPublicKey() {
        return asymmetricEncryption.getPublicKey();
    }

    private boolean doKeysExist() {
        return Files.exists(Paths.get("public_key.pem")) && Files.exists(Paths.get("private_key.pem"));
    }

    public void setServerPublicKey(PublicKey serverPublicKey) {
        serverEncryption.setKeys(new KeyPair(serverPublicKey, null));
    }

    public AsymmetricEncryption getAsymmetricEncryption() {
        return asymmetricEncryption;
    }
}
