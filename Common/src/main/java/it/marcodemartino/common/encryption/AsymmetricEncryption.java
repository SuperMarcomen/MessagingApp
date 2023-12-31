package it.marcodemartino.common.encryption;

import java.security.PublicKey;

public interface AsymmetricEncryption extends AsymmetricKeyConstructor {

    byte[][] encryptFromString(String input);
    byte[][] encrypt(byte[] input);
    String decryptToString(byte[][] input);
    byte[] decrypt(byte[][] input);
    byte[][] signFromString(String input);
    byte[][] sign(byte[] input);
    boolean checkSignatureFromString(byte[][] toBeChecked, String shouldBe, PublicKey publicKey);
    boolean checkSignature(byte[][] toBeChecked, byte[] shouldBe, PublicKey publicKey);

}
