package it.marcodemartino.server.encryption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSAEncryption implements AsymmetricEncryption {

    private final Logger logger = LogManager.getLogger(RSAEncryption.class);
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyPairGenerator generator;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public RSAEncryption(int keySize) {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySize);
        } catch (NoSuchAlgorithmException e) {
            logger.fatal("Could not instantiate the keypair generator. Error message: {}", e.getMessage());
            System.exit(1);
            return;
        }

        try {
            encryptCipher = Cipher.getInstance("RSA");
            decryptCipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.fatal("Could not instantiate the cipher. Error message: {}", e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void generateKeyPair() {
        KeyPair pair = generator.generateKeyPair();

        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (InvalidKeyException e) {
            logger.error("Could not init the cipher. Error message: {}", e.getMessage());
        }
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public byte[] encryptFromString(String input) {
        return encrypt(input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] encrypt(byte[] input) {
        try {
            return encryptCipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while encrypting the input: {}. Error message: {} ", new String(input, StandardCharsets.UTF_8), e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public String decryptToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decrypt(byte[] input) {
        try {
            return decryptCipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while decrypting the input: {}. Error message: {} ", new String(input, StandardCharsets.UTF_8), e.getMessage());
        }
        return new byte[0];
    }


    @Override
    public byte[] sign(byte[] input) {
        return decrypt(input);
    }

    @Override
    public boolean checkSignature(byte[] toBeChecked, byte[] shouldBe, PublicKey publicKey) {
        return false;
    }
}
