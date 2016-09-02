package utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * @author xiaofan
 * 
 *         3.09
 *
 */
public class ghca {
    // 通过username和password产生对应的token
    public static String gen(String username, String password) {
        String key = "aI0fC8RslXg6HXaKAUa6kpvcAXszvTcxYP8jmS9sBnVfIqTRdJS1eZNHmBjKN28j";

        int time_stamp = Calendar.getInstance().get(Calendar.SECOND); // 获取当前时间戳，秒
        time_stamp = 0x5748cf1d;// To comment
        String hex_time = Long.toHexString(time_stamp).toUpperCase(); // 将时间戳转换为16进制字符串
        String user_upper = username.toUpperCase();// 将宽带用户名转换为大写

        // 将密码asii值进行求和
        int sum_pass = 0;
        for (int i = 0; i < password.length(); ++i) {
            sum_pass += (int) password.charAt(i);// password.codePointAt(i);
        }

        // 利用时间戳和密码长度进行处理
        int t_pass_len = time_stamp % password.length();
        int time_stamp_flag = time_stamp % password.length();
        if (time_stamp_flag == 0)
            time_stamp_flag = 1;

        // 根据时间戳和密码长度得出需要与密码ascii和相与的数
        int seed = time_stamp_flag - (time_stamp_flag == password.length() ? 1 : 0);
        String passwd_hex = String.format("%04x", sum_pass ^ (seed) & 0xffffffff);

        // 计算需要截取的第一部分密码长度
        int pass_split_len = time_stamp_flag - (time_stamp_flag == password.length() ? 1 : 0) + 1;
        // 截取第一部分密码
        String passwd_one = password.substring(0, pass_split_len);
        // 截取密码的第二部分
        String passwd_two = password.substring(pass_split_len);

        // 截取密钥的第一部分，长度为60-len(宽带密码第一部分)
        String enc_key_one = key.substring(0, 60 - passwd_one.length());
        // 截取密钥的第二部分，长度为64-len(宽带用户名)-len(截取的密码的第二部分)
        int enc_key_two_len = 64 - username.length() - passwd_two.length();
        String enc_key_two = key.substring(0, enc_key_two_len);

        // 组合待md5的字符串, 密钥的第一部分 + 截取的密码第一部分 + 帐号 +密钥的第二部分+截取的密码的另一部分
        String key_str = String.format("%s%s%s%s%s", enc_key_one, passwd_one, username, enc_key_two, passwd_two);

        // 将时间戳高低位互换
        int swap_time_stamp = (time_stamp << 24) & 0xFF000000;
        swap_time_stamp += (time_stamp << 8) & 0x00FF0000;
        swap_time_stamp += (time_stamp >> 8) & 0x0000FF00;
        swap_time_stamp += (time_stamp >> 24) & 0x000000FF;

        // Turn Timestamp To 4 Bytes 在这里char是两个字节的
        char byte_four = (char) ((swap_time_stamp & 0xff000000) >> 24);
        char byte_three = (char) ((swap_time_stamp & 0x00ff0000) >> 16);
        char byte_two = (char) ((swap_time_stamp & 0x0000ff00) >> 8);
        char byte_one = (char) ((swap_time_stamp & 0x000000ff));

        // 组合一波待md5的字符串,组合方式：高低16位互换的时间戳+key_str
        String key_str_final = new String();
        key_str_final = "" + byte_one + byte_two + byte_three + byte_four + key_str;

        // 将key_str_final进行两次md5
        // String sec_md5 = MD5_hexdigest(MD5_digest(key_str_final));
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        try {
            md5.update(key_str_final.getBytes("ISO-8859-1"));// 用ISO-8859-1的字符编码 参考：http://kxjhlele.iteye.com/blog/333211
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        md5.update(md5.digest());
        byte[] tmp = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : tmp) {
            sb.append(String.format("%02x", b & 0xff));// 注意是两位的
        }
        String sec_md5 = sb.toString();

        // 截取第二次md5加密过后的前16位
        String part_four = sec_md5.substring(0, 16);

        // 组合最终的帐号
        String enc_acc = String.format("~ghca%s2023%s%s%s", hex_time, part_four, passwd_hex, user_upper);

        return enc_acc;
    }

    public static void main(String[] args) {
        String username = "18388888888";
        String password = "12345678";
        String token = gen(username, password);
        System.out.println(token);
    }

}
