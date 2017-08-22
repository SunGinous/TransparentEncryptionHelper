package com.example.administrator.myironguard.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Administrator on 2016/12/15.
 */
public class RSACoder {
    public static boolean decrypt(File file1,File file2, PrivateKey key) {
        int len=-1;
        byte[] buff = new byte[50];
        //实例化
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/None/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        //使用密钥初始化，设置为解密模式
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        //执行操作

        try {
            FileInputStream fileInputStream = new FileInputStream(file1);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            while ((len = fileInputStream.read(buff)) != -1)
            {
                byte[] bytes = cipher.doFinal(buff);
                fileOutputStream.write(bytes,0,bytes.length);
            }
            fileOutputStream.close();
            fileInputStream.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static Boolean encrypt(File file1,File file2, PublicKey key) {
        int len=-1;
        byte[] buff = new byte[50];
        try{
            FileInputStream fileInputStream = new FileInputStream(file1);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
        //使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //执行操作
              while ((len = fileInputStream.read(buff)) != -1)
              {
                  byte[] bytes = cipher.doFinal(buff);
                  fileOutputStream.write(bytes,0,bytes.length);
              }
            fileOutputStream.close();
            fileInputStream.close();
        return  true;
      }catch (Exception e)
      {
          e.printStackTrace();
      }
      return  false;
    }
}
