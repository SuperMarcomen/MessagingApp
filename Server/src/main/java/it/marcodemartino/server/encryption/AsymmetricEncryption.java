package it.marcodemartino.server.encryption;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface AsymmetricEncryption {

    void generateKeyPair();
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
    byte[] encryptFromString(String input);
    byte[] encrypt(byte[] input);
    String decryptToString(byte[] input);
    byte[] decrypt(byte[] input);
    byte[] sign(byte[] input);
    boolean checkSignature(byte[] toBeChecked, byte[] shouldBe, PublicKey publicKey);

}
