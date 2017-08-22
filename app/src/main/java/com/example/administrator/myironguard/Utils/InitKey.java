package com.example.administrator.myironguard.Utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by Administrator on 2016/11/27.
 */
public class InitKey {
    public static byte[] initSecretKey_Aes() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小
        //AES 要求密钥长度为 128
        kg.init(128);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] initSecretKey_Des() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小
        //DES 要求密钥长度为 64
        kg.init(64);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static  KeyPair initSecretKeyPair_Rsa()
    {
        try {
            SecureRandom sr = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024,sr);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            System.out.println("keypair generate success");
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static  PrivateKey initPrivateKey_Rsa(KeyPair keyPair)
    {

        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            System.out.println("privatekey success");
            return  privateKey;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        System.out.println("private fail");
        return  null;
    }
    public static PublicKey initPublicKey_Rsa(KeyPair keyPair)
    {

        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
        try {
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            PublicKey publicKey= keyFactory.generatePublic(x509EncodedKeySpec);
            return  publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
   return  null;
    }

}
