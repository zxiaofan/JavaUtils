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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 修改所有文件（加注释），便于提测时提全量。
 * 
 * github.zxiaofan.com
 * 
 * @author zxiaofan
 */
public class ModifyAllFile {
    /**
     * 支持多条规则（以;分隔，每条规则以|分隔后缀名和增加内容）（a|b|c表示以a或b后后缀的文件将在文件头增加c）.
     */
    static String rule = ".java||////AutoModify -By zxiaofan////;.jsp|<!-- AutoModify -->";

    public static void main(String[] args) {
        System.out.println("请输入待修改的文件（夹）的绝对路径：");
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        modify(path, rule);
    }

    /**
     * 文件首行增加内容.
     * 
     * @param path
     *            待修改文件路径
     * @param rules
     *            修改规则
     */
    public static void modify(String path, String rules) {
        if (isNullOrEmpty(rules)) {
            System.out.println("规则为空...");
            return;
        }
        Map<String, String> mapRule = buildRuleMap(rules);
        List<String> files = null;
        if (null != mapRule && !mapRule.isEmpty()) {
            files = getAllFilePath(path, mapRule.keySet());
        }
        try {
            modifyFile(files, mapRule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("修改完成");
    }

    /**
     * 构建规则Map.
     * 
     * @param rules
     *            规则字符串
     * @return Map
     */
    private static Map<String, String> buildRuleMap(String rules) {
        Map<String, String> mapRule = new HashMap<>();
        String[] arr = rules.split(";");
        for (String keyVal : arr) {
            if (!isNullOrEmpty(keyVal)) {
                String[] spl = keyVal.split("\\|");
                for (int i = 0; i < spl.length - 1; i++) {
                    mapRule.put(spl[i], spl[spl.length - 1]);
                }
            }
        }
        mapRule.remove("");
        return mapRule;
    }

    /**
     * 按规则修改特定文件.
     * 
     * @throws Exception
     * 
     */
    private static void modifyFile(List<String> filePaths, Map<String, String> mapRule) throws Exception {
        if (isNullOrEmpty(mapRule) || isNullOrEmpty(filePaths)) {
            return;
        }
        String addNote = "";
        for (String path : filePaths) {
            for (String suffix : mapRule.keySet()) {
                if (path.endsWith(suffix)) {
                    addNote = mapRule.get(suffix);
                    break;
                }
            }
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
     * @return 所有带转换文件路径
     */
    private static List<String> getAllFilePath(String path, Set<String> suffixSet) {
        /**
         * 所有待转换文件绝对路径.
         */
        List<String> filePaths = new ArrayList<>();
        File root = new File(path);
        File[] files = root.listFiles();
        if (files == null) {
            filePaths.add(path);
            return filePaths;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归调用
                List<String> list = getAllFilePath(file.getAbsolutePath(), suffixSet);
                if (!isNullOrEmpty(list)) {
                    filePaths.addAll(list);
                }
                // System.out.println(filePath + "目录下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                // System.out.println(filePath + "目录下所有文件" + file.getAbsolutePath());
                for (String suf : suffixSet) {
                    if (file.getAbsolutePath().endsWith(suf)) {
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return filePaths;
    }

    /**
     * 是否为null或空字符串或空集合.
     * 
     * @param param
     *            参数
     * @return bool
     */
    @SuppressWarnings("rawtypes")
    private static boolean isNullOrEmpty(Object param) {
        if (param instanceof String) {
            if (null == param || "".equals(((String) param).trim())) {
                return true;
            }
        } else if (param instanceof List) {
            if (null == param || ((List) param).isEmpty()) {
                return true;
            }
        } else if (param instanceof Map) {
            if (null == param || ((Map) param).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static String encode = "utf-8";

}
