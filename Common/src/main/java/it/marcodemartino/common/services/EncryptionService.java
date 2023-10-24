package it.marcodemartino.common.services;

import com.google.gson.Gson;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.encryption.*;
import it.marcodemartino.common.json.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;

public class EncryptionService {

    private final AsymmetricEncryption localAsymmetricEncryption;
    private final AsymmetricEncryption otherAsymmetricEncryption;
    private final AsymmetricKeyWriter keyWriter;
    private final AsymmetricKeyReader keyReader;
    private final KeysService keysService;
    private final Gson gson;

    public EncryptionService(AsymmetricEncryption localAsymmetricEncryption, AsymmetricEncryption otherAsymmetricEncryption, KeysService keysService) {
        this.localAsymmetricEncryption = localAsymmetricEncryption;
        this.otherAsymmetricEncryption = otherAsymmetricEncryption;
        keyWriter = new AsymmetricKeyFileWriter();
        keyReader = new AsymmetricKeyFileReader();
        this.keysService = keysService;
        gson = GsonInstance.get();
    }

    public byte[][] signIdentityCertificate(IdentityCertificate identityCertificate) {
        String json = gson.toJson(identityCertificate);
        return signString(json);
    }

    public byte[][] signString(String string) {
        return localAsymmetricEncryption.signFromString(string);
    }

    public void encryptSignAndCertifyMessage(JSONObject jsonObject, String email, IdentityCertificate identityCertificate, CompletableFuture<JSONObject> jsonFuture) {
        CompletableFuture<String> keyFuture = new CompletableFuture<>();
        keyFuture.thenAccept(publicKeyString -> {
            if (publicKeyString == null) {
                jsonFuture.complete(null);
                return;
            }

            PublicKey otherPubKey = localAsymmetricEncryption.constructKeyFromString(publicKeyString);
            PublicKey currentPubKey = otherAsymmetricEncryption.getPublicKey();
            setOtherPublicKey(otherPubKey);

            String json = gson.toJson(jsonObject);
            byte[][] encryptedJson = otherAsymmetricEncryption.encryptFromString(json);
            byte[][] signedJson = localAsymmetricEncryption.signFromString(json);

            if (currentPubKey != null) setOtherPublicKey(currentPubKey);
            jsonFuture.complete(new SignedEncryptedCertifiedObject(encryptedJson, signedJson, email, identityCertificate));
        });

        keysService.requestPublicKeyOf(email, keyFuture);
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

    public String decryptMessage(byte[][] message) {
        return localAsymmetricEncryption.decryptToString(message);
    }

    public boolean verifyIdentityCertificate(IdentityCertificate identityCertificate, boolean localEncryption) {
        String json = gson.toJson(identityCertificate.getUser());
        if (localEncryption) {
            return verifySignature(identityCertificate.getSignature(), json);
        } else {
            return verifyOtherSignature(identityCertificate.getSignature(), json);
        }
    }

    public boolean verifyOtherSignature(byte[][] toBeChecked, String shouldBe, PublicKey otherPubKey) {
        PublicKey currentPubKey = otherAsymmetricEncryption.getPublicKey();
        setOtherPublicKey(otherPubKey);
        boolean result = verifyOtherSignature(toBeChecked, shouldBe);
        if (currentPubKey != null) setOtherPublicKey(currentPubKey);
        return result;
    }

    public boolean verifySignature(byte[][] toBeChecked, String shouldBe) {
        return localAsymmetricEncryption.checkSignature(toBeChecked, shouldBe.getBytes(StandardCharsets.UTF_8), localAsymmetricEncryption.getPublicKey());
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

    public KeysService getKeysService() {
        return keysService;
    }
}
