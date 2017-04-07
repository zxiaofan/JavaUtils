package com.zxiaofan.util.password;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zxiaofan
 */
public class DESUtil {
    /**
     * 构造函数.
     * 
     */
    private DESUtil() {
        throw new RuntimeException("this is a util class,can not instance");
    }

    /**
     * .
     * 
     * @param args
     *            args
     * @throws Exception
     *             E
     */
    public static void main(String[] args) throws Exception {
        String PWD = "%github.zxiaofan.com@24w";
        String data = "Hello,I'm zxiaofan";
        String des3en = des3EncodeECB(PWD, data);
        System.out.println("des3加密:" + des3en);
        System.out.println(des3DecodeECB(PWD, des3en));

        String des = new String(encrypt(data, PWD));
        System.out.println("des加密:" + des);
        System.out.println(decrypt(des, PWD));
    }

    /**
     * byteArr2String.
     * 
     * @param bytes
     *            bytes
     * @return string
     */
    private static String byteArr2String(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(b + "");
            sb.append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * string2ByteArr.
     * 
     * @param str
     *            str
     * @return byte[]
     */
    private static byte[] string2ByteArr(String str) {
        if (null == str || str.isEmpty()) {
            return new byte[0];
        }
        String[] split = str.split(",");
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            bytes[i] = Byte.parseByte(split[i]);
        }
        return bytes;
    }

    /**
     * 加密.
     * 
     * @param content
     *            需要加密的内容
     * @param password
     *            加密密码
     * @return byte
     * @exception Exception
     *                e
     */
    public static String encrypt(String content, String password) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
        byte[] byteContent = content.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, key); // 初始化
        byte[] result = cipher.doFinal(byteContent);
        return byteArr2String(result); // 加密
    }

    /**
     * 解密.
     * 
     * @param content
     *            待解密内容
     * @param password
     *            解密密钥
     * @return byte
     * @exception Exception
     *                e
     */
    public static String decrypt(String content, String password) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key); // 初始化
        byte[] result = cipher.doFinal(string2ByteArr(content));
        return new String(result, "utf-8"); // 加密
    }

    /**
     * des3加密.
     * 
     * @param key
     *            key
     * @param data
     *            数据
     * @return 加密后字符串
     */
    public static String des3EncodeECB(String key, String data) {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(getKey(key));
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] bOut = cipher.doFinal(data.getBytes("UTF-8"));
            return byte2hex(bOut);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * des3解密.
     * 
     * @param key
     *            秘钥
     * @param data
     *            数据
     * @return 机密后字符串
     */
    public static String des3DecodeECB(String key, String data) {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(getKey(key));
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            byte[] bOut = cipher.doFinal(hex2byte(data));
            return new String(bOut, "UTF-8");
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * 二进制转16进制.
     * 
     * @param bytes
     *            二进制
     * @return 十六进制字符串
     */
    private static String byte2hex(byte[] bytes) {
        StringBuffer retString = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1).toUpperCase());
        }
        return retString.toString();
    }

    /**
     * 十六进制转二进制.
     * 
     * @param hex
     *            十六进制
     * @return 二进制
     */
    private static byte[] hex2byte(String hex) {
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }

    /**
     * 转UTF-8.
     * 
     * @param keyString
     *            字符串
     * @return 二进制
     * @throws Exception
     *             e
     */
    private static byte[] getKey(String keyString) throws Exception {
        return keyString.substring(0, 24).getBytes("UTF-8");
    }
}
