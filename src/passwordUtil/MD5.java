package passwordUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zxiaofan
 */
public class MD5 {
    private MD5() {
        throw new RuntimeException("this is a util class,can not instance");
    }

    /**
     * MD5.
     * 
     * @param plainText
     *            种子
     * @return String MD5字符串
     */
    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            // System.out.println("result: " + buf.toString());
            // // 32位的加密
            // System.out.println("result: " + buf.toString().substring(8, 24));
            // 16位的加密
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 测试.
     * 
     * @param args
     *            args
     */
    public static void main(String[] args) {
        System.out.println(md5("github.zxiaofan.com"));
    }
}
