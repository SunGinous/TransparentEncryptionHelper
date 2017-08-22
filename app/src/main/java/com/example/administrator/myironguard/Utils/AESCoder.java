package com.example.administrator.myironguard.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2016/11/27.
 */
public class AESCoder {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static Key toKey(byte[] key){
        //生成密钥
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }



    public static OutputStream encrypt(File file, byte[] key) throws Exception{
        return encrypt(file, key, DEFAULT_CIPHER_ALGORITHM);
    }
    private static OutputStream encrypt(File file, byte[] key, String cipherAlgorithm) throws Exception {
        //还原密钥
        Key k = toKey(key);
        return encrypt(file, k, cipherAlgorithm);
    }
    private static OutputStream encrypt(File file, Key key, String cipherAlgorithm) throws Exception{
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //执行操作
        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(file), cipher);
        return cos;
    }



    public static InputStream decrypt(File file, byte[] key) throws Exception{
        return decrypt(file, key,DEFAULT_CIPHER_ALGORITHM);
    }
    private static InputStream decrypt(File file, byte[] key,String cipherAlgorithm) throws Exception{
        //还原密钥
        Key k = toKey(key);
        return decrypt(file, k, cipherAlgorithm);
    }
    private static InputStream decrypt(File file, Key key, String cipherAlgorithm) throws Exception{
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        //执行操作
        CipherInputStream cis = new CipherInputStream(new FileInputStream(file), cipher);
        return cis;
    }
}
