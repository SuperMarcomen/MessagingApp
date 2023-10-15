package it.marcodemartino.common.encryption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class RSAEncryption implements AsymmetricEncryption {

    private final Logger logger = LogManager.getLogger(RSAEncryption.class);
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyPairGenerator generator;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private Cipher signatureCipher;
    private Cipher signatureCheckCipher;

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
            signatureCipher = Cipher.getInstance("RSA");
            signatureCheckCipher = Cipher.getInstance("RSA");
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
            signatureCipher.init(Cipher.ENCRYPT_MODE, privateKey);
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
        return new String(decrypt(input), StandardCharsets.UTF_8);
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
    public byte[] signFromString(String input) {
        return sign(input.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public byte[] sign(byte[] input) {
        try {
            return signatureCipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while decrypting the input: {}. Error message: {} ", new String(input, StandardCharsets.UTF_8), e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public boolean checkSignatureFromString(byte[] toBeChecked, String shouldBe, PublicKey publicKey) {
        return checkSignature(toBeChecked, shouldBe.getBytes(StandardCharsets.UTF_8), publicKey);
    }

    @Override
    public boolean checkSignature(byte[] toBeChecked, byte[] shouldBe, PublicKey publicKey) {
        byte[] uncryptedBytes = new byte[0];
        try {
            signatureCheckCipher.init(Cipher.DECRYPT_MODE, publicKey);
        } catch (InvalidKeyException e) {
            logger.error("Could not init the cipher. Error message: {}", e.getMessage());
        }
        try {
            uncryptedBytes = signatureCheckCipher.doFinal(toBeChecked);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while decrypting the input: {}. Error message: {} ", new String(toBeChecked, StandardCharsets.UTF_8), e.getMessage());
        }
        return Arrays.equals(uncryptedBytes, shouldBe);
    }
}
