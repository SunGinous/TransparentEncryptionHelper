package com.example.administrator.myironguard.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sunginous on 2017/3/11.
 */
/*
 *MD5算法返回128位二进制数，但128位太长了，所以返回的结果一般是以32位16进制数显示的，其它版本的MD5算法还可以返回
 * 24位16进制数或者16位16进制数
 * MD5算法仅用来加密登陆密码
 */
public class MD5Utils {
    public static String MD5(String str){
        MessageDigest md5=null;
        try{
            md5=MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray=str.toCharArray();
        byte[] byteArray=new byte[charArray.length];
        for(int i=0;i<charArray.length;i++){
            byteArray[i]=(byte)charArray[i];
        }
        byte[] md5Bytes=md5.digest(byteArray);
        StringBuffer hexValue=new StringBuffer();
        for(int i=0;i<md5Bytes.length;i++){
            int val=((int) md5Bytes[i])&0xff;
            if (val<16){
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
