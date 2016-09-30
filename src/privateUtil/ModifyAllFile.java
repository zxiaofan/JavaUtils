/*
 * 文件名：ModifyAllFile.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： ModifyAllFile.java
 * 修改人：zxiaofan
 * 修改时间：2016年9月30日
 * 修改内容：新增
 */
package privateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 修改所有文件（加注释），便于提测时提全量。
 * 
 * @author zxiaofan
 */
public class ModifyAllFile {
    static List<String> suffix = Arrays.asList(".java"); // 处理以此后缀结尾的文件

    static String addNote = "////AutoModify -By github.zxiaofan.com////";

    public static void main(String[] args) {
        modify();
    }

    /**
     * 执行方法.
     * 
     * @param filePath
     */
    public static void modify() {
        System.out.println("请输入待修改的文件（夹）的绝对路径：");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        getAllFilePath(path);
        try {
            modifyFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("修改完成");
    }

    /**
     * TODO 添加方法注释.
     * 
     * @throws Exception
     * 
     */
    private static void modifyFile() throws Exception {
        if (null == addNote || "".equals(addNote)) {
            addNote = "////AutoAddNote////";
        }
        for (String path : filePaths) {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(path), encode));
            StringBuffer content = new StringBuffer();
            content.append(addNote);
            String s = reader.readLine();
            while (s != null && !"null".equals(s)) {
                content.append("\r\n");
                content.append(s);
                s = reader.readLine();
            }
            content.append("\r\n");
            reader.close();
            try {
                FileWriter fw = new FileWriter(path);
                fw.write(content.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 所有待转换文件绝对路径.
     * 
     * @param path
     * @return
     */
    private static void getAllFilePath(String path) {
        File root = new File(path);
        File[] files = root.listFiles();
        if (files == null) {
            filePaths.add(path);
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归调用
                getAllFilePath(file.getAbsolutePath());
                // System.out.println(filePath + "目录下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                // System.out.println(filePath + "目录下所有文件" + file.getAbsolutePath());
                for (String suf : suffix) {
                    if (file.getAbsolutePath().endsWith(suf)) {
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }

    }

    /**
     * 所有待转换文件绝对路径.
     */
    static List<String> filePaths = new ArrayList<>();

    private static String encode = "utf-8";

}
