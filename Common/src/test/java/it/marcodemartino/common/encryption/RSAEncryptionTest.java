package it.marcodemartino.common.encryption;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RSAEncryptionTest {

    private static AsymmetricEncryption asymmetricEncryption;

    @BeforeAll
    static void init() {
        asymmetricEncryption = new RSAEncryption(2048);
        asymmetricEncryption.generateKeyPair();
    }

    @Test
    @DisplayName("Encrypt and decrypt a string and check if they are the same")
    void decryptToString() {
        String message = "Mammt";
        byte[] encryptedMessageBytes = asymmetricEncryption.encryptFromString(message);
        String decryptedMessage = asymmetricEncryption.decryptToString(encryptedMessageBytes);
        assertEquals(message, decryptedMessage);
    }

    @Test
    @DisplayName("Sign a string and check if it's signature is valid")
    void sign() {
        String message = "Mammt";
        byte[] signature = asymmetricEncryption.signFromString(message);
        boolean isSignatureValid = asymmetricEncryption.checkSignatureFromString(signature, message, asymmetricEncryption.getPublicKey());
        assertTrue(isSignatureValid);
    }
}