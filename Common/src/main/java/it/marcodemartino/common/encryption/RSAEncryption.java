package it.marcodemartino.common.encryption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class RSAEncryption implements AsymmetricEncryption {

    private final Logger logger = LogManager.getLogger(RSAEncryption.class);
    private static final int CHUNK_SIZE = 50;
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
            logger.fatal("Could not instantiate the keypair generator", e);
            System.exit(1);
            return;
        }

        try {
            encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            signatureCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            signatureCheckCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.fatal("Could not instantiate the cipher", e);
            System.exit(1);
        }
    }

    @Override
    public void generateKeyPair() {
        KeyPair pair = generator.generateKeyPair();

        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
        initCipher();
    }

    private void initCipher() {
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            if (privateKey == null) return;
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            signatureCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        } catch (InvalidKeyException e) {
            logger.error("Could not init the cipher", e);
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
    public void setKeys(KeyPair keyPair) {
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        initCipher();
    }

    @Override
    public PublicKey constructKeyFromString(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(X509publicKey);
        } catch(Exception e){
            logger.fatal("There was an error reconstructing the key {} from string", key, e);
            e.printStackTrace();
        }

        return null;
    }

    public String publicKeyToString(PublicKey publicKey) {
        byte[] encodedKey = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    @Override
    public byte[][] encryptFromString(String input) {
        return encrypt(input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[][] encrypt(byte[] input) {
        try {
            int inputLength = input.length;
            int numChunks = (inputLength + CHUNK_SIZE - 1) / CHUNK_SIZE;
            byte[][] encryptedChunks = new byte[numChunks][];

            int offset = 0;
            for (int i = 0; i < numChunks; i++) {
                int length = Math.min(CHUNK_SIZE, inputLength - offset);
                byte[] chunk = new byte[length];
                System.arraycopy(input, offset, chunk, 0, length);

                byte[] encryptedChunk = encryptCipher.doFinal(chunk);
                encryptedChunks[i] = encryptedChunk;

                offset += length;
            }
            return encryptedChunks;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while encrypting the input: {}", new String(input, StandardCharsets.UTF_8), e);
        }
        return new byte[0][];
    }

    @Override
    public String decryptToString(byte[][] input) {
        return new String(decrypt(input), StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decrypt(byte[][] input) {
        try {
            int totalSize = Arrays.stream(input).mapToInt(a -> a.length).sum();
            byte[] decryptedData = new byte[totalSize];
            int decryptedOffset = 0;

            for (byte[] encryptedChunk : input) {
                byte[] decryptedChunk = decryptCipher.doFinal(encryptedChunk);
                System.arraycopy(decryptedChunk, 0, decryptedData, decryptedOffset, decryptedChunk.length);
                decryptedOffset += decryptedChunk.length;
            }
            // Ensure the final decryptedData has the correct size
            if (decryptedOffset < decryptedData.length) {
                decryptedData = Arrays.copyOf(decryptedData, decryptedOffset);
            }
            return decryptedData;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while decrypting the input", e);
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
            logger.error("There was an error while decrypting the input: {}", new String(input, StandardCharsets.UTF_8), e);
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
            logger.error("Could not init the cipher", e);
        }
        try {
            uncryptedBytes = signatureCheckCipher.doFinal(toBeChecked);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while decrypting the input: {}", new String(toBeChecked, StandardCharsets.UTF_8), e);
        }
        return Arrays.equals(uncryptedBytes, shouldBe);
    }
}
