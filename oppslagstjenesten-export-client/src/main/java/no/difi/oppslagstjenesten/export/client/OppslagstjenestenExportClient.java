package no.difi.oppslagstjenesten.export.client;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

/**
 * Utiltity class to verify and decrypt a files from Oppslagstjenesten Eksport.
 */
public class OppslagstjenestenExportClient {

    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {

        // 0. get args
        if (args.length != 9) {
           printUsageAndExit();
        }
        final File keystoreFile = new File(args[0]);
        final String keystorePassword = args[1];
        final String serviceOwnerPrivateKeyAlias = args[2];
        final String serviceOwnerPrivateKeyPassword = args[3];
        final String difiPublicKeyAlias = args[4];
        final File inputEncryptedFile = new File(args[5]);
        final File signatureFile = new File(args[6]);
        final File keyFile = new File(args[7]);
        final String outputDecryptedFile = args[8];


        // 1. load keystore
        final KeyStore keyStore = loadKeystore(keystoreFile, keystorePassword);
        // 2. load Difis public key
        final PublicKey difiPublicKey = loadDifiPublicKey(keyStore, difiPublicKeyAlias);
        // 2. verify signature for signed input file using Difis public key
        verifySignature(signatureFile, inputEncryptedFile, difiPublicKey);
        // 3. Read Service owner's private key
        final PrivateKey soPrivateKey = readSOPrivateKey(keyStore, serviceOwnerPrivateKeyAlias, serviceOwnerPrivateKeyPassword);
        // 4. Decrypt key file
        final SecretKey secretKey = decryptKeyFile(keyFile, soPrivateKey);
        // 4. Decrypt data file with symmetric key
        decryptDataFile(inputEncryptedFile, outputDecryptedFile, secretKey);
    }

    private static KeyStore loadKeystore(File keystoreFile, String keystorePassword) throws Exception{
        System.out.println("Loading keystore [" + keystoreFile + "] ...");
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        final InputStream keystoreFileInputStream = new FileInputStream(keystoreFile);
        keyStore.load(keystoreFileInputStream, keystorePassword.toCharArray());
        keystoreFileInputStream.close();
        System.out.println("OK");
        return keyStore;
    }

    private static PublicKey loadDifiPublicKey(KeyStore keyStore, String difiPublicKeyAlias) throws KeyStoreException {
        System.out.println("Loading Difis public key with alias [" + difiPublicKeyAlias + "]...");
        final PublicKey difiPublicKey = keyStore.getCertificate(difiPublicKeyAlias).getPublicKey();
        System.out.println("OK");
        return difiPublicKey;
    }

    private static void verifySignature(File signatureFile, File inputEncryptedFile, PublicKey difiPublicKey) throws Exception {
        System.out.println("Verifying signature stored in file [" + signatureFile + "] for signed file [" + inputEncryptedFile + "] ...");
        final Signature signature = Signature.getInstance("SHA512withRSA/PSS");
        final MGF1ParameterSpec mgf1 = new MGF1ParameterSpec("SHA-512");
        final PSSParameterSpec spec1 = new PSSParameterSpec("SHA-512", "MGF1", mgf1, 512 / 8, 1);
        signature.setParameter(spec1);
        signature.initVerify(difiPublicKey);
        final FileInputStream signedFileInputStream = new FileInputStream(inputEncryptedFile);
        byte[] signedDataBytes = new byte[1024 * 16];
        while (signedFileInputStream.read(signedDataBytes) != -1) {
            signature.update(signedDataBytes);
        }
        signedFileInputStream.close();
        byte[] signatureBytes = new byte[(int) signatureFile.length()];
        FileInputStream fileInputStream = new FileInputStream(signatureFile);
        fileInputStream.read(signatureBytes);
        if (signature.verify(signatureBytes)) {
            System.out.println("OK");
        } else {
            System.out.println("Signature NOT OK");
            System.exit (1);
        }
    }

    public static PrivateKey readSOPrivateKey(KeyStore keyStore, String serviceOwnerPrivateKeyAlias, String serviceOwnerPrivateKeyPassword) throws Exception {
        System.out.println("Loading Service owner's private key with alias [" + serviceOwnerPrivateKeyAlias + "].");
        final PrivateKey privateKey =
                ((KeyStore.PrivateKeyEntry) keyStore.getEntry(serviceOwnerPrivateKeyAlias,
                new KeyStore.PasswordProtection(serviceOwnerPrivateKeyPassword.toCharArray()))).getPrivateKey();
        System.out.println("OK");
        return privateKey;
    }

    private static SecretKey decryptKeyFile(File keyFile, PrivateKey privateKey) throws Exception {
        System.out.println("Decrypting key file [" + keyFile + "]...");
        final Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA512AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        final CipherInputStream encryptedKeyInputStream = new CipherInputStream(new FileInputStream(keyFile), cipher);
        final byte[] encryptedKeyBytes = new byte[256 / 8];
        encryptedKeyInputStream.read(encryptedKeyBytes);
        final SecretKey secretKey = new SecretKeySpec(encryptedKeyBytes, "AES");
        encryptedKeyInputStream.close();
        System.out.println("OK");
        return secretKey;
    }

    private static void decryptDataFile(File inputEncryptedFile, String outputDecryptedFile, SecretKey secretKey) throws Exception {
        System.out.println("Decrypting file [" + inputEncryptedFile + "] using symmetric key, decrypted file is [" + outputDecryptedFile + "] ...");
        final Cipher decryptFileCipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec spec = new IvParameterSpec(new byte[decryptFileCipher.getBlockSize()]);
        decryptFileCipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        InputStream inputStream = new CipherInputStream(new FileInputStream(inputEncryptedFile), decryptFileCipher);
        FileOutputStream outputStream = new FileOutputStream(outputDecryptedFile);
        byte[] dataBytes = new byte[1024 * 16];
        int i;
        while ((i = inputStream.read(dataBytes)) != -1) {
            outputStream.write(dataBytes, 0, i);
        }
        inputStream.close();
        outputStream.close();
        System.out.println("OK");
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: " + OppslagstjenestenExportClient.class.getName() + " <keystore-file> <keystore-password> <service-owner-key-alias> <service-owner-key-password> <difi-key-alias> <input-encrypted-file> <signature-file> <encrypted-key-file> <output-decrypted-file>");
        System.exit(-1);
    }

}
