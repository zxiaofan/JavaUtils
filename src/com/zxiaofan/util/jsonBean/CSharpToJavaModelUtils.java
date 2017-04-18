/*
 * 文件名：CSharpToJavaModelUtils.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： CSharpToJavaModelUtils.java
 * 修改人：zxiaofan
 * 修改时间：2016年3月28日
 * 修改内容：新增
 */
package com.zxiaofan.util.jsonBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本工具源于最近项目需要将C#的model转为Java，且公司有CheckStyle.
 * 
 * 考虑到copy、paste效率太低，且对技术没有任何提升；
 * 
 * 以后接手的C#项目会越来越多，写个工具实现一键转换的必要性也就相当高了。
 * 
 * 支持标准格式的C# model
 * 
 * 持续更新地址：https://github.com/zxiaofan/JavaUtils
 * 
 * @author zxiaofan
 */
public class CSharpToJavaModelUtils {
    /**
     * 执行此main函数即可.
     * 
     * @param args
     *            args
     */
    public static void main(String[] args) {
        CSharpToJavaModelUtils.start();
    }

    /**
     * package默认加入该路径.
     */
    private static String packagePath = "model.vo";

    /**
     * 输出路径.
     */
    private static String outputPath = "d:\\model\\";

    /**
     * 开始转换.
     * 
     */
    public static void start() {
        System.out.println("请输入待转换的C#文件（夹）的绝对路径：");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        System.out.println("请输入生成Java文件的包名：");
        String packageName = scanner.nextLine();
        transform(path, packageName);
        System.out.println("转换完成，请到" + outputPath + "查看转换结果！");
        try {
            String[] cmd = new String[5];
            cmd[0] = "cmd";
            cmd[1] = "/c";
            cmd[2] = "start";
            cmd[3] = " ";
            cmd[4] = outputPath;
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    private static String encode = "utf-8";

    private static String rn = "\r\n"; // 换行.

    private static String tab = "    ";

    private static String desc1 = tab + "/**" + rn + tab + " * "; // 字段描述-开始部分

    private static String desc2 = "." + rn + tab + " */" + rn + tab;

    private static String author = "/**" + rn + " * @author yunhai(default)" + rn + " */" + rn;

    private static String importList = "import java.util.List;\r\n";

    private static String importBigDecimal = "import java.math.BigDecimal;\r\n";

    private static String importMap = "import java.util.Map;\r\n";

    private static String importDate = "import java.util.Date;\r\n";

    /**
     * 执行转换.
     * 
     * @param path
     *            绝对路径.
     * @param packageName
     *            包名.
     */
    public static void transform(String path, String packageName) {
        String originPath = handlePath(path); // 待转换的目录
        if (originPath.endsWith("\\")) {
            originPath = originPath.substring(0, originPath.length() - 1);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        outputPath = outputPath + format.format(new Date()) + "\\" + packageName.replaceAll("\\.", "\\\\") + "\\model\\vo";
        List<String> filePaths = getAllFilePath(path);
        Map<String, List<FieldVo>> mapData = buildOriginData(filePaths, originPath); // Map<相对路径，字段信息集合>
        createJavaModel(mapData, packageName);
    }

    /**
     * 生成Java model.
     * 
     * @param map
     *            map
     * @param packageName
     *            packageName
     */
    private static void createJavaModel(Map<String, List<FieldVo>> map, String packageName) {
        createFile(outputPath);
        for (Entry<String, List<FieldVo>> entry : map.entrySet()) {
            String relativePath = entry.getKey();
            if (!"".equals(relativePath.trim())) {
                relativePath = relativePath.substring(0, relativePath.lastIndexOf("\\"));
            }
            List<FieldVo> fieldVos = entry.getValue();
            if (fieldVos.isEmpty()) {
                continue;
            }
            StringBuffer buffer = new StringBuffer();
            if (!"".equals(packageName) && packageName != null && !packageName.endsWith(".")) {
                packageName += ".";
            }
            String relativePathPackage = relativePath.replaceAll("\\\\", ".");
            // if (relativePathPackage.contains(".")) {
            // relativePathPackage = relativePathPackage.substring(relativePathPackage.lastIndexOf("."));
            // }
            buffer.append("package " + packageName + packagePath + relativePathPackage + ";" + rn + rn);
            buffer.append(importBigDecimal);
            buffer.append(importDate);
            buffer.append(importList);
            buffer.append(importMap);
            buffer.append(author);
            buffer.append("public class " + fieldVos.get(0).getClassName() + " {" + rn);
            // 字段定义
            for (FieldVo vo : fieldVos) {
                buffer.append(desc1);
                buffer.append(vo.getFieldDesc());
                buffer.append(desc2);
                buffer.append("private " + vo.getType() + " " + vo.getFieldNameLower() + ";" + rn);
            }
            // 字段get、set
            for (FieldVo vo : fieldVos) {
                // get
                buffer.append(desc1);
                buffer.append("获取" + vo.getFieldDesc() + "." + rn + tab + " *" + rn);
                buffer.append("     * @return 返回" + vo.getFieldDesc());
                buffer.append(desc2);
                buffer.append("public " + vo.getType() + " get" + vo.getFieldNameUpper() + "() {" + rn);
                buffer.append(tab + tab + "return " + vo.getFieldNameLower() + ";" + rn + tab + "}" + rn);
                // set
                buffer.append(desc1);
                buffer.append("设置" + vo.getFieldDesc() + "." + rn + tab + " *" + rn);
                buffer.append("     * @param " + vo.getFieldNameLower() + rn + tab);
                buffer.append(" *       要设置的" + vo.getFieldNameLower());
                buffer.append(desc2);
                buffer.append("public void set" + vo.getFieldNameUpper() + "(" + vo.getType() + " " + vo.getFieldNameLower() + ") {" + rn);
                buffer.append(tab + tab + "this." + vo.getFieldNameLower() + " = " + vo.getFieldNameLower() + ";" + rn + tab + "}" + rn);
            }
            buffer.append("}" + rn);
            relativePath = relativePath.replaceAll("\\.", "\\\\");
            String finalPath = outputPath + "\\" + relativePath; // + fieldVos.get(0).getClassName() + ".java";
            if (!"".equals(relativePath)) {
                createFile(finalPath);
            }
            String claseName = fieldVos.get(0).getClassName();
            if (claseName.contains("<")) {
                claseName = claseName.substring(0, claseName.indexOf("<"));
            }
            finalPath = outputPath + "\\" + relativePath + "\\" + claseName + ".java";
            try {
                FileWriter fw = new FileWriter(finalPath);
                fw.write(buffer.toString());
                fw.close();
                // OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encode);
                // out.write(buffer.toString());
                // out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新建文件（夹）.
     * 
     * @param path
     *            路径
     */
    private static void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (path.contains(".")) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 递归创建文件夹，保证该路径所有文件夹都被创建
                createFile(path.substring(0, path.lastIndexOf("\\")));
                file.mkdir();
            }
        }
    }

    /**
     * 构造Java model原始信息.
     * 
     * @param paths
     *            所有待转换文件绝对路径.
     * @param originPath
     *            待转换的目录.
     * @return map
     */
    private static Map<String, List<FieldVo>> buildOriginData(List<String> paths, String originPath) {
        String fileTxt = ""; // 文本内容
        String fileClassname = ""; // 类名
        String fieldDesc = ""; // 字段描述
        String fieldNameUpper = ""; // 字段名-大写开头
        String type = ""; // 类型
        String relativePath = ""; // 相对路径
        Map<String, List<FieldVo>> mapData = new HashMap<>();
        for (String absolutePath : paths) {
            relativePath = absolutePath.replace(originPath, "");
            List<FieldVo> fieldVos = new ArrayList<>(); // 字段信息集合
            fileClassname = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.lastIndexOf("."));
            fileTxt = readTextFile(absolutePath, encode);
            if (!fileTxt.contains("namespace")) {
                System.err.println("该CS文件不是model:" + absolutePath);
                continue;
            }
            fileTxt = formatStr(fileTxt);
            fileTxt = fileTxt.substring(fileTxt.indexOf("namespace"));
            fileTxt = fileTxt.replaceAll("#region public members", "").replaceAll(", ", ",").replaceAll("  ", " ");
            // Start 多匹配模式
            StringBuffer pattern = new StringBuffer();
            pattern.append("<summary>.{0,30}?(?<desc>.{0,30}?)?</summary>");
            pattern.append(".{0,80}?public\\s(?<type>.*?)?\\s");
            pattern.append("(?<fieldname>.*?)?\\{");
            Matcher matcher = Pattern.compile(pattern.toString()).matcher(fileTxt);
            while (matcher.find()) {
                FieldVo vo = new FieldVo();
                fieldDesc = matcher.group("desc").replaceAll("/", "").trim();
                type = matcher.group("type").trim();
                fieldNameUpper = matcher.group("fieldname").trim();
                if ("class".equals(type) && fieldNameUpper.matches(fileClassname + "<[A-Za-z]+>")) {
                    fileClassname = fieldNameUpper;
                } else if (!"class".equals(type) && !fieldNameUpper.endsWith(")") && !"".equals(fieldNameUpper)) {
                    vo.setClassName(fileClassname);
                    vo.setFieldNameUpper(fieldNameUpper);
                    type = typeTrans(type);
                    vo.setType(type);
                    // vo.setRelativePath(relativePath);
                    vo.setFieldNameLower(upperConvertToLower(fieldNameUpper));
                    vo.setFieldDesc(fieldDesc);
                    fieldVos.add(vo);
                }
            }
            // End 多匹配模式
            mapData.put(relativePath, fieldVos);
        }
        return mapData;
    }

    /**
     * C#的数据类型转为Java.
     * 
     * replace()方法防止List<string>形式
     * 
     * @param type
     *            字段类型
     * @return Java类型
     */
    private static String typeTrans(String type) {
        if (type.contains("?")) {
            type = type.replaceAll("\\?", "");
        }
        if (type.contains("string")) {
            type = type.replaceAll("string", "String");
        }
        if (type.contains("DateTime")) {
            type = type.replaceAll("DateTime", "Date");
        }
        if (type.contains("bool")) {
            type = type.replaceAll("bool", "boolean");
        }
        if (type.contains("decimal")) {
            type = type.replaceAll("decimal", "BigDecimal");
        }
        if (type.contains("object")) {
            type = type.replaceAll("object", "Object");
        }
        if ("enum".equals(type) || "int".equals(type)) {
            type = "Integer";
        }
        if (type.contains("<int,")) { // Dictionary<int, string>
            type = type.replaceAll("<int,", "<Integer,");
        }
        if (type.contains("Dictionary<")) {
            type = type.replaceAll("Dictionary<", "Map<");
        }
        if (type.contains(",")) {
            type = type.replaceAll(",", ", ");
        }
        return type;
    }

    /**
     * C#字段大写开头转为小写开头.
     * 
     * @param fieldNameUpper
     *            字段名
     * @return 小写开头字段
     */
    private static String upperConvertToLower(String fieldNameUpper) {
        if ("".equals(fieldNameUpper)) {
            return "";
        }
        String result = fieldNameUpper.replaceFirst(fieldNameUpper.substring(0, 1), fieldNameUpper.substring(0, 1).toLowerCase());
        return result;
    }

    /**
     * 所有待转换文件绝对路径.
     * 
     * @param path
     *            path
     * @return paths
     */
    private static List<String> getAllFilePath(String path) {
        File root = new File(path);
        File[] files = root.listFiles();
        List<String> paths = new ArrayList<>();
        if (files == null) {
            paths.add(path);
            return paths;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归调用
                List<String> pathsTemp = getAllFilePath(file.getAbsolutePath());
                paths.addAll(pathsTemp);
                // System.out.println(filePath + "目录下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                // System.out.println(filePath + "目录下所有文件" + file.getAbsolutePath());
                if (file.getAbsolutePath().endsWith(".cs")) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
        return paths;
    }

    /**
     * 文件读取.
     * 
     * @param sFileName
     *            文件名
     * @param sEncode
     *            文件编码格式
     * @return 文本字符串
     */
    private static String readTextFile(String sFileName, String sEncode) {
        StringBuffer sbStr = new StringBuffer();
        try {
            File ff = new File(sFileName);
            InputStreamReader read = new InputStreamReader(new FileInputStream(ff), sEncode);
            BufferedReader ins = new BufferedReader(read);
            String dataLine = "";
            while (null != (dataLine = ins.readLine())) {
                sbStr.append(dataLine);
                // sbStr.append("/r/n");
            }
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbStr.toString();
    }

    /**
     * 去空格去换行.
     *
     * @param str
     *            str
     * @return str
     */
    private static String formatStr(String str) {
        if (str != null) {
            Pattern p = Pattern.compile("\r|\n");
            Matcher m = p.matcher(str);
            str = m.replaceAll("");
        }
        return str;
    }

    /**
     * 处理路径.
     * 
     * @param absolutelyPath
     *            绝对路径文件名
     * @return 路径
     */
    private static String handlePath(String absolutelyPath) {
        absolutelyPath = absolutelyPath.replaceAll("/+", "\\");
        absolutelyPath = absolutelyPath.replaceAll("\\+", "\\"); // Windows习惯
        return absolutelyPath;
    }
}

/**
 * 字段信息.
 * 
 * @author yunhai
 */
class FieldVo {
    /**
     * 类名.
     */
    public String className;

    /**
     * 字段类型.
     */
    public String type;

    /**
     * 字段名-小写.
     */
    public String fieldNameLower;

    /**
     * 字段名-大写.
     */
    public String fieldNameUpper;

    /**
     * get.
     */
    public String getters;

    /**
     * set.
     */
    public String setters;

    /**
     * 类的相对路径.
     */
    public String relativePath;

    /**
     * 字段描述.
     */
    public String fieldDesc;

    /**
     * 设置className.
     * 
     * @return 返回className
     */
    public String getClassName() {
        return className;
    }

    /**
     * 获取className.
     * 
     * @param className
     *            要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 设置type.
     * 
     * @return 返回type
     */
    public String getType() {
        return type;
    }

    /**
     * 获取type.
     * 
     * @param type
     *            要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 设置fieldNameLower.
     * 
     * @return 返回fieldNameLower
     */
    public String getFieldNameLower() {
        return fieldNameLower;
    }

    /**
     * 获取fieldNameLower.
     * 
     * @param fieldNameLower
     *            要设置的fieldNameLower
     */
    public void setFieldNameLower(String fieldNameLower) {
        this.fieldNameLower = fieldNameLower;
    }

    /**
     * 设置fieldNameUpper.
     * 
     * @return 返回fieldNameUpper
     */
    public String getFieldNameUpper() {
        return fieldNameUpper;
    }

    /**
     * 获取fieldNameUpper.
     * 
     * @param fieldNameUpper
     *            要设置的fieldNameUpper
     */
    public void setFieldNameUpper(String fieldNameUpper) {
        this.fieldNameUpper = fieldNameUpper;
    }

    /**
     * 设置getters.
     * 
     * @return 返回getters
     */
    public String getGetters() {
        return getters;
    }

    /**
     * 获取getters.
     * 
     * @param getters
     *            要设置的getters
     */
    public void setGetters(String getters) {
        this.getters = getters;
    }

    /**
     * 设置setters.
     * 
     * @return 返回setters
     */
    public String getSetters() {
        return setters;
    }

    /**
     * 获取setters.
     * 
     * @param setters
     *            要设置的setters
     */
    public void setSetters(String setters) {
        this.setters = setters;
    }

    /**
     * 设置relativePath.
     * 
     * @return 返回relativePath
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * 获取relativePath.
     * 
     * @param relativePath
     *            要设置的relativePath
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * 设置fieldDesc.
     * 
     * @return 返回fieldDesc
     */
    public String getFieldDesc() {
        return fieldDesc;
    }

    /**
     * 获取fieldDesc.
     * 
     * @param fieldDesc
     *            要设置的fieldDesc
     */
    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

}
