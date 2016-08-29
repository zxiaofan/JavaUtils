
package passwordUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author zxiaofan
 */
public class AESUtil {
    /**
     * 构造函数.
     * 
     */
    private AESUtil() {
        throw new RuntimeException("this is a util class,can not instance");
    }

    public static void main(String[] args) {
        String strKey = "csdn.zxiaofancom";
        String strIn = "github/csdn/zxiaofan/HelloWorld";
        String encrypt = encrypt(strKey, strIn);
        System.out.println("加密:" + encrypt);
        System.out.println("解密:" + decrypt(strKey, encrypt));
    }

    /**
     * 加密
     * 
     * @param strKey
     *            密匙
     * @param strIn
     *            待加密字符串
     * @return
     * @throws Exception
     */
    public static String encrypt(String strKey, String strIn) {
        try {
            if (strKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return "";
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(strKey.getBytes(), "AES"), new IvParameterSpec(new byte[16]));// 初始化16空字节
            byte[] encrypted = cipher.doFinal(strIn.getBytes());
            return new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 解密
     * 
     * @param strKey
     *            密匙
     * @param strIn
     *            待解密字符串
     * @return
     * @throws Exception
     */
    public static String decrypt(String strKey, String strIn) {
        try {
            if (strKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return "";
            }
            byte[] bytes = new BASE64Decoder().decodeBuffer(strIn);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(strKey.getBytes(), "AES"), new IvParameterSpec(new byte[16]));// 初始化16空字节
            byte[] encrypted = cipher.doFinal(bytes);
            return new String(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
