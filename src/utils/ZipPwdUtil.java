/*
 * 文件名：ZipPwdUtil.java
 * 版权：Copyright 2007-2017 517na Tech. Co. Ltd. All Rights Reserved. 
 * 描述： ZipPwdUtil.java
 * 修改人：xiaofan
 * 修改时间：2017年2月5日
 * 修改内容：新增
 */
package utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

/**
 * 压缩文件密码破解工具类.
 * 
 * 7Z：jar（commons-compress-1.9.jar、xz-1.6.jar）
 * 
 * @author xiaofan
 */
public class ZipPwdUtil {
    public static void main(String[] args) throws IOException {
        // Scanner scanner = new Scanner(System.in);
        // String zipPath = scanner.nextLine();
        String zipPath = "D:\\7z.7z";
        String pwd = "zxiaofan.com";
        boolean bool = validatePwd(zipPath, pwd);
        System.out.println(bool);
    }

    /**
     * 校验xx.7Z压缩包的密码是否正确.
     * 
     * @param path7ZFile
     *            7Z压缩包文件路径
     * @param pwd
     *            密码
     * @return 密码是否正确
     */
    public static boolean validatePwd(String path7ZFile, String pwd) {
        boolean bool = true;
        File file = new File(path7ZFile);
        if (!file.exists()) {
            System.out.println("no file[" + path7ZFile + "]");
            return false;
        }
        if (null == pwd) {
            pwd = "";
        }
        try {
            @SuppressWarnings("resource")
            SevenZFile sevenZFile = new SevenZFile(file, pwd.getBytes(Charset.forName("UTF-16LE")));
            while (null != sevenZFile.getNextEntry()) {
                byte[] content = new byte[2]; // 流输出
                try {
                    sevenZFile.read(content, 0, 2);
                } catch (IOException e) { // 读取数据异常，说明密码错误
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return bool;
    }

    /**
     * 解压7Z文件到当前位置.
     * 
     * @param path7ZFile
     *            7Z压缩包文件路径
     * @param pwd
     *            密码（无密码传null）
     * @return 解压成功
     */
    public static boolean un7z(String path7ZFile, String pwd) {
        SevenZFile sevenZFile = null;
        try {
            File file = new File(path7ZFile);
            if (!file.exists()) {
                System.out.println("no file[" + path7ZFile + "]");
                return false;
            }
            if (null == pwd) {
                pwd = "";
            }
            sevenZFile = new SevenZFile(file, pwd.getBytes(Charset.forName("UTF-16LE")));
            SevenZArchiveEntry archiveEntry = null;
            while ((archiveEntry = sevenZFile.getNextEntry()) != null) {
                String entryFileName = archiveEntry.getName(); // 文件名
                byte[] content = new byte[(int) archiveEntry.getSize()]; // 流输出
                sevenZFile.read(content, 0, content.length);
                String entryFilePath = file.getParent() + entryFileName; // 解压文件存放路径
                OutputStream os = null;
                try {
                    File entryFile = new File(entryFilePath);
                    os = new BufferedOutputStream(new FileOutputStream(entryFile)); // 输出解压文件
                    os.write(content);
                } finally {
                    if (os != null) {
                        os.flush();
                        os.close();
                    }
                }
            }
            sevenZFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (sevenZFile != null) {
                    sevenZFile.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
