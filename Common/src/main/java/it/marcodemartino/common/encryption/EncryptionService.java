package it.marcodemartino.common.encryption;

import com.google.gson.Gson;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.json.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PublicKey;

public class EncryptionService {

    private final AsymmetricEncryption localAsymmetricEncryption;
    private final AsymmetricEncryption otherAsymmetricEncryption;
    private final AsymmetricKeyWriter keyWriter;
    private final AsymmetricKeyReader keyReader;
    private final Gson gson;

    public EncryptionService(int keySize) {
        localAsymmetricEncryption = new RSAEncryption(keySize);
        otherAsymmetricEncryption = new RSAEncryption(keySize);
        keyWriter = new AsymmetricKeyFileWriter();
        keyReader = new AsymmetricKeyFileReader();
        gson = GsonInstance.get();
    }

    public JSONObject encryptAndSignMessage(JSONObject jsonObject, PublicKey publicKey) {
        setOtherPublicKey(publicKey);
        String json = gson.toJson(jsonObject);
        byte[][] encryptedJson = otherAsymmetricEncryption.encryptFromString(json);
        byte[][] signedJson = localAsymmetricEncryption.signFromString(json);
        return new SignedEncryptedMessageObject(encryptedJson, signedJson);
    }

    public JSONObject encryptMessage(JSONObject jsonObject) {
        String json = gson.toJson(jsonObject);
        byte[][] encryptedJson = otherAsymmetricEncryption.encryptFromString(json);
        return new EncryptedMessageObject(encryptedJson);
    }

    public JSONObject encryptMessage(JSONObject jsonObject, PublicKey otherPublicKey) {
        setOtherPublicKey(otherPublicKey);
        String json = gson.toJson(jsonObject);
        byte[][] encryptedJson = otherAsymmetricEncryption.encryptFromString(json);
        return new EncryptedMessageObject(encryptedJson);
    }

    public boolean verifyIdentityCertificate(IdentityCertificate identityCertificate) {
        String json = gson.toJson(identityCertificate.getUser());
        return verifyOtherSignature(identityCertificate.getSignature(), json);
    }

    public boolean verifyOtherSignature(byte[][] toBeChecked, String shouldBe) {
        return otherAsymmetricEncryption.checkSignature(toBeChecked, shouldBe.getBytes(StandardCharsets.UTF_8), otherAsymmetricEncryption.getPublicKey());
    }

    public void loadKeysIfExist() {
        if (doKeysExist()) {
            localAsymmetricEncryption.setKeys(keyReader.readKeyPair("public_key.pem", "private_key.pem"));
        }
    }

    public void loadOrGenerateKeys() {
        if (doKeysExist()) {
            localAsymmetricEncryption.setKeys(keyReader.readKeyPair("public_key.pem", "private_key.pem"));
        } else {
            localAsymmetricEncryption.generateKeyPair();
            KeyPair keyPair = new KeyPair(localAsymmetricEncryption.getPublicKey(), localAsymmetricEncryption.getPrivateKey());
            keyWriter.writeToFile(keyPair, Paths.get(""));
        }
    }

    private boolean doKeysExist() {
        return Files.exists(Paths.get("public_key.pem")) && Files.exists(Paths.get("private_key.pem"));
    }

    public void setOtherPublicKey(PublicKey otherPublicKey) {
        otherAsymmetricEncryption.setKeys(new KeyPair(otherPublicKey, null));
    }

    public AsymmetricEncryption getLocalAsymmetricEncryption() {
        return localAsymmetricEncryption;
    }
}
