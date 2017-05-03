
/*
 * 文件名：JsonToJavaBean.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： JsonToJavaBean.java
 * 修改人：zxiaofan
 * 修改时间：2016年7月20日
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 最近需将大量json转换为对应的JavaBean，网上有诸多在线转换工具，但是由于[1:无法直接生成JavaBean;2转换不够准确;3:公司对代码要求CheckStyle]，故自己写个转换工具.
 * 
 * 功能：读取Json文件并在outputPath目录生成相应的JavaBean文件，直接Copy使用，注释可选(直过CheckStyle)。
 * 
 * 持续更新地址：https://github.com/zxiaofan/JavaUtils
 * 
 * Note:暂不支持多线程并发.
 * 
 * @author zxiaofan
 */
@SuppressWarnings("unchecked")
public class JsonToJavaBean {
    // ====== 修改参数 ====== //
    /**
     * 待转换Json路径.
     */
    private static String path = "D:\\json.txt";

    /**
     * packageName包名.
     */
    private static String packageName = "com.zxiaofan.service.model.vo";

    /**
     * 根Bean名字.
     */
    private static String beanRootName = "Root";

    /**
     * 是否为字段添加@SerializedName("Upper")注解，字段大写（默认false）.
     */
    private static boolean serializedNameUpper = false;

    /**
     * 是否替换get、Set方法的字段中的特殊字符（默认开启）.
     */
    private static boolean replaceSpecialCharacter = true;

    /**
     * get、Set方法的字段中要过滤字符(ReplaceAll)(默认只保留字母数字[^0-9a-zA-Z])(index[1]即替换后的字符).
     */
    private static List<String> filterCharacter = Arrays.asList("[^0-9a-zA-Z]", "");

    /**
     * [double]特殊匹配规则(前缀|后缀|包含|类型),默认不区分大小写.
     */
    private static String matchRuleDouble = "|Date_Time_Hour||long,|!id_type|money_price|BigDecimal,|Cost|!time|BigDecimal";

    /**
     * [String]特殊匹配规则(前缀|后缀|包含|类型),默认不区分大小写.
     */
    private static String matchRuleString = "|Date_Time_Hour||Date," + matchRuleDouble; // 自动覆盖double规则第一条

    /**
     * 是否开启特殊匹配规则.
     */
    private static boolean ismachRule = true;

    /**
     * 特殊匹配规则默认不区分大小写.
     */
    private static boolean machRuleIgnoreCase = true;

    // ====== 修改参数 ====== //

    /**
     * 是否添加注释.
     */
    private static boolean addNote = true;

    /**
     * 默认使用Integer代替int.
     */
    private static boolean defaultInteger = true;

    /**
     * package子路径.
     */
    private static String packagePath = "";

    /**
     * 输出路径.
     */
    private static String outputPath = "d:\\JsonToJavaBean\\";

    /**
     * main.
     * 
     * @param args
     *            args
     */
    public static void main(String[] args) {
        start();
    }

    /**
     * 开始转换.
     * 
     */
    public static void start() {
        // System.out.println("请输入待转换的json文件的绝对路径：");
        // Scanner scanner = new Scanner(System.in);
        // String path = scanner.nextLine();

        String json = readTextFile(path, encode);
        outputPath = outputPath + format.format(new Date()) + "\\" + packageName + "\\" + packagePath;
        outputPath = outputPath.replaceAll("\\.", "\\\\");
        toJavaBean(json);
        System.out.println("转换完成，请到【" + outputPath + "】查看转换结果！");
        createFile(outputPath);
        try {
            String[] cmd = new String[5];
            cmd[0] = "cmd";
            cmd[1] = "/c";
            cmd[2] = "start";
            cmd[3] = " ";
            cmd[4] = outputPath;
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 转bean.
     * 
     * @param json
     *            json
     */
    private static void toJavaBean(String json) {
        if (!(json.startsWith("{") || json.endsWith("}"))) {
            throw new RuntimeException("不是标准的json文件:{...}"); // 暂不做过多校验
        }
        json = formatStr(json);
        buildRule(matchRuleDouble, listRuleDouble);
        buildRule(matchRuleString, listRuleString);
        List<Bean> beans = new ArrayList<>();
        buildOrignBean(json, beans, beanRootName);
        formatBean();
        createJavaModel();
        // System.out.println(gson.toJson(fields));
    }

    /**
     * 生成Java model.
     * 
     */
    private static void createJavaModel() {
        createFile(outputPath);
        for (Entry<String, List<Bean>> entry : fields.entrySet()) {
            String[] listJar = new String[8]; // 顺序:BigDecimal、Date、List、Map、Set
            List<Bean> fieldVos = new ArrayList<>(entry.getValue());
            if (fieldVos.isEmpty()) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            if (!"".equals(packageName) && packageName != null && !packageName.endsWith(".") && !"".equals(packagePath.trim())) {
                packageName += ".";
            }
            builder.append("package " + packageName + packagePath + ";" + rn + rn);
            builder.append(importJar);
            builder.append(author);
            builder.append("public class " + fieldVos.get(0).getFieldNameUpper() + " {" + rn);
            Set<Bean> setBean = new HashSet<Bean>();
            // 字段定义
            boolean hasImport = false;
            for (int i = 1; i < fieldVos.size(); i++) {
                Bean vo = fieldVos.get(i);
                if (setBean.contains(vo)) {
                    continue; // 字段去重
                }
                setBean.add(vo);
                if (addNote) {
                    builder.append(desc1);
                    builder.append(vo.getFieldDesc());
                    builder.append(desc2);
                }
                if (serializedNameUpper) {
                    builder.append("@SerializedName(\"").append(vo.getFieldNameUpper()).append("\")").append(rn);
                    builder.append(tab);
                }
                builder.append("private ").append(vo.getFieldType()).append(" ").append(vo.getFieldNameLower()).append(";").append(rn);
                if (vo.getFieldType().startsWith(ObjType.BigDecimal)) {
                    listJar[0] = importBigDecimal;
                    hasImport = true;
                } else if (vo.getFieldType().startsWith(ObjType.Date)) {
                    listJar[1] = importDate;
                    hasImport = true;
                } else if (vo.getFieldType().startsWith(ObjType.List)) {
                    listJar[2] = importList;
                    hasImport = true;
                } else if (vo.getFieldType().startsWith(ObjType.Map)) {
                    listJar[3] = importMap;
                    hasImport = true;
                } else if (vo.getFieldType().startsWith(ObjType.Set)) {
                    listJar[4] = importSet;
                    hasImport = true;
                }
            }
            if (serializedNameUpper) {
                if (hasImport) {
                    listJar[5] = rn;
                }
                listJar[6] = importSerializedNameUpper;
            }
            // 字段get、set（已过滤第一个Bean）
            List<Bean> beans = new ArrayList<>(setBean);
            for (int i = 0; i < beans.size(); i++) {
                Bean vo = beans.get(i);
                // get
                if (addNote) {
                    builder.append(desc1);
                    builder.append("获取" + vo.getFieldDesc() + "." + rn + tab + " *" + rn);
                    builder.append("     * @return 返回" + vo.getFieldDesc());
                    builder.append(desc2);
                }
                builder.append("public ").append(vo.getFieldType()).append(" get");
                if (replaceSpecialCharacter) {
                    builder.append(vo.getFieldNameUpper().replaceAll(filterCharacter.get(0), filterCharacter.get(1)));
                } else {
                    builder.append(vo.getFieldNameUpper());
                }
                builder.append("() {").append(rn);
                builder.append(tab).append(tab).append("return ").append(vo.getFieldNameLower()).append(";").append(rn).append(tab).append("}").append(rn);
                String fieldNameUpperTemp = vo.getFieldNameUpper();
                String fieldNameLowerTemp = vo.getFieldNameLower();
                if (replaceSpecialCharacter) {
                    fieldNameUpperTemp = fieldNameUpperTemp.replaceAll(filterCharacter.get(0), filterCharacter.get(1));
                    fieldNameLowerTemp = fieldNameLowerTemp.replaceAll(filterCharacter.get(0), filterCharacter.get(1));
                }
                // set
                if (addNote) {
                    builder.append(desc1);
                    builder.append("设置" + vo.getFieldDesc() + "." + rn + tab + " *" + rn);
                    builder.append("     * @param " + fieldNameLowerTemp + rn + tab);
                    builder.append(" *       要设置的" + vo.getFieldNameUpper());
                    builder.append(desc2);
                }
                builder.append("public void set");

                builder.append(fieldNameUpperTemp);
                builder.append("(").append(vo.getFieldType()).append(" ").append(fieldNameLowerTemp).append(") {").append(rn);
                builder.append(tab).append(tab).append("this.").append(vo.getFieldNameLower()).append(" = ").append(fieldNameLowerTemp).append(";").append(rn).append(tab).append("}").append(rn);
            }
            builder.append("}" + rn);
            String beanText = builder.toString();
            StringBuilder jarText = new StringBuilder();
            for (String jar : listJar) {
                if (jar != null) {
                    jarText.append(jar);
                }
            }
            beanText = beanText.replace(importJar, jarText.toString());
            String finalPath = outputPath + "\\";
            finalPath = finalPath.replaceAll("\\.", "\\\\");
            createFile(finalPath);
            finalPath = finalPath + fieldVos.get(0).getFieldNameUpper() + ".java";
            try (FileWriter fw = new FileWriter(finalPath);) {
                fw.write(beanText);
                // OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encode);
                // out.write(buffer.toString());
                // out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化Bean中字段值.
     * 
     * @param fieldVos
     *            fieldVos
     */
    private static void formatBean() {
        for (List<Bean> fieldVos : fields.values()) {
            for (Bean bean : fieldVos) {
                bean.setFieldNameLower(caseConversion(bean.getFieldName(), true));
                bean.setFieldNameUpper(caseConversion(bean.getFieldName(), false));
            }
        }
    }

    /**
     * 构建原始实体数据.
     * 
     * @param json
     *            json
     * @param beans
     *            beans
     * @param className
     *            className
     */
    private static void buildOrignBean(String json, List<Bean> beans, String className) {
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) fromJson(json, Map.class);
        } catch (JsonSyntaxException e) {
            System.out.println(json);
            e.printStackTrace();
            System.exit(0);
        }
        Bean beanParent = new Bean();
        beanParent.setFieldName(className);
        beans.add(beanParent);
        Iterator<Entry<String, Object>> itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Bean bean = new Bean();
            Entry<String, Object> entry = itr.next();
            String k = entry.getKey(); // FieldName
            bean.setFieldName(k);
            Object v = entry.getValue();
            if (v == null || "[]".equals(v.toString())) {
                itr.remove();
                continue;
            }
            if (v instanceof Integer) {
                bean.setFieldType(ObjType.Int);
                if (defaultInteger) {
                    bean.setFieldType(ObjType.Integer);
                }
            } else if (v instanceof Double) {
                // bean.setFieldType(ObjType.Double);
                String type = matchRule(k, ObjType.Double, listRuleDouble);
                if (ObjType.Double.equals(type) && String.valueOf(v).endsWith(".0")) { // Gson转Map时会将int转为double，此处还原为int
                    type = ObjType.Integer;
                }
                bean.setFieldType(type);
            } else if (v instanceof Boolean) {
                bean.setFieldType(ObjType.Boolean);
            } else if (v instanceof String) {
                bean.setFieldType(ObjType.String);
                bean.setFieldType(matchRule(k, ObjType.String, listRuleString));
            } else {
                String childJson = v.toString();
                if (childJson.startsWith("{")) {
                    bean.setFieldType(caseConversion(k, false));
                    List<Bean> newBeans = new ArrayList<>();
                    buildOrignBean(childJson, newBeans, k);
                    fields.put(k, newBeans);
                } else if (childJson.startsWith("[")) {
                    bean.setFieldName(k);
                    bean.setFieldType("List<" + caseConversion(k, false) + ">");
                    // System.out.println(childJson);
                    List<Object> childList = (List<Object>) fromJson(childJson, List.class);
                    List<Bean> newBeans = new ArrayList<>();
                    if (!childList.toString().startsWith("[{")) { // 匹配特殊格式["ANY"],Expected BEGIN_OBJECT but was STRING
                        bean.setFieldType(ObjType.ListString);
                        beans.add(bean);
                    } else {
                        buildOrignBean(gson.toJson(childList.get(0)), newBeans, k); // 数据可能重复["ANY"]
                    }
                } else {
                    bean.setFieldType(ObjType.String);
                }
            }
            beans.add(bean);
        }
        fields.put(className, beans);

    }

    /**
     * json序列化.
     * 
     * @param json
     *            josn
     * @return Object
     */
    private static Object fromJson(String json, Class type) {
        Object obj = null;
        try {
            obj = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println(json);
            System.exit(0);
        }
        return obj;
    }

    /**
     * 构建特殊匹配规则.
     * 
     */
    private static String matchRule(String fieldName, String fieldType, List<String[]> listRule) {
        if (!ismachRule) {
            return fieldType;
        }
        // |Date_Time||long,|!id_type|money_price|BigDecimal,|Cost|!time|BigDecimal
        if (machRuleIgnoreCase) {
            fieldName = fieldName.toLowerCase();
        }
        for (String[] oneRule : listRule) {
            boolean special = true; // 满足特殊匹配规则
            boolean oneRuleDotFail = false; // 不匹配规则中任意一项
            for (int i = 0; i < oneRule.length - 1; i++) {
                if (oneRuleDotFail) {
                    break;
                }
                String[] tempArr = null;
                String tempStr = oneRule[i];
                if (tempStr != null && !"".equals(tempStr)) {
                    boolean fei = false;
                    if (tempStr.startsWith("!")) {
                        fei = true;
                        tempStr = tempStr.replace("!", "");
                    }
                    tempArr = tempStr.split("_"); // 前/后缀规则
                    boolean bool1 = false; // 前缀中若含有两个匹配，满足一个则即为true
                    if (tempArr != null) {
                        for (String str : tempArr) {
                            if (bool1) {
                                break;
                            }
                            if (fei && oneRuleDotFail) {
                                break;
                            }
                            if (machRuleIgnoreCase) {
                                str = str.toLowerCase();
                            }
                            switch (i) {
                                case 0: // 前缀
                                    if (fei ^ fieldName.startsWith(str)) {
                                        bool1 = true;
                                        special = true;
                                    } else {
                                        special = false;
                                        oneRuleDotFail = true; // 不匹配规则中任意一项，直接跳过
                                    }
                                    break;
                                case 1: // 后缀
                                    if (fei ^ fieldName.endsWith(str)) {
                                        bool1 = true;
                                        special = true;
                                    } else {
                                        special = false;
                                        oneRuleDotFail = true;
                                    }
                                    break;
                                case 2: // 包含
                                    if (fei ^ fieldName.contains(str)) {
                                        bool1 = true;
                                        special = true;
                                    } else {
                                        special = false;
                                        oneRuleDotFail = true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

            }
            if (special) {
                return oneRule[3];
            }
        }
        return fieldType;

    }

    /**
     * 生成自定义规则列表.
     * 
     * @param matchRule
     *            自定义规则
     * @param listRule
     */
    public static void buildRule(String matchRule, List<String[]> listRule) {
        if (matchRule.length() != 0 && listRule.isEmpty()) {
            String[] rules = matchRule.split(",");
            for (String rule : rules) {
                listRule.add(rule.split("\\|")); // 自定义规则列表
            }
        }
    }

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
     * 去空格去换行，去掉特殊字符.
     *
     * @param str
     *            str
     * @return str
     */
    private static String formatStr(String str) {
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            str = m.replaceAll("");
            // json = json.replace(":\".*?\"(?=[,}])", ":\"" + typeString + "\"");
            p = Pattern.compile(":\".*?\"(?=[,}])");
            m = p.matcher(str);
            str = m.replaceAll(":\"" + typeString + "\"");
            str = str.replaceAll("://", ""); // 避免json中含有url引发json转换异常
            str = str.replaceAll("/", "").replaceAll("“|”", "\"").replaceAll("（|【", "[").replaceAll("）|】", "]").replaceAll("，", ",");
            p = Pattern.compile("(?<re1>:null)(,|})");
            m = p.matcher(str);
            if (m.find()) { // 处理某些不标准json{"PaySuccess":null,"Msg":null}
                str = str.replaceAll(m.group("re1"), ":\"" + typeString + "\"");
            }
        }
        return str;
    }

    /**
     * 大小写转换.
     * 
     * @param fieldName
     *            字段名
     * @param toLower
     *            true:大转小
     * @return 转换后的字段名
     */
    private static String caseConversion(String fieldName, boolean toLower) {
        if (null == fieldName || "".equals(fieldName)) {
            return "";
        }
        String result = "";
        if (toLower) {
            result = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toLowerCase());
        } else {
            result = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
        }
        return result;
    }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    private static String typeString = "typeString";

    private static String encode = "utf-8";

    private static Gson gson = new Gson();

    private static Map<String, List<Bean>> fields = new HashMap<>();

    private static List<String[]> listRuleDouble = new ArrayList<>(); // 匹配规则

    private static List<String[]> listRuleString = new ArrayList<>(); // 匹配规则

    private static String importJar = "github.zxiaofan.com";

    private static String rn = "\r\n"; // 换行.

    private static String tab = "    ";

    private static String desc1 = tab + "/**" + rn + tab + " * "; // 字段描述-开始部分

    private static String desc2 = "." + rn + tab + " */" + rn + tab;

    private static String author = rn + "/**" + rn + " * @author yunhai(default)" + rn + " */" + rn;

    private static String importList = "import java.util.List;\r\n";

    private static String importBigDecimal = "import java.math.BigDecimal;\r\n";

    private static String importMap = "import java.util.Map;\r\n";

    private static String importDate = "import java.util.Date;\r\n";

    private static String importSet = "import java.util.Set;\r\n";

    private static String importSerializedNameUpper = "import com.google.gson.annotations.SerializedName;\r\n";
}

class Bean {
    private String fieldName;

    private String fieldNameLower; // 小写开头

    private String fieldNameUpper; // 大写开头

    private String fieldType; // 类型

    private String fieldDesc; // 描述

    /**
     * 设置fieldName.
     * 
     * @return 返回fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获取fieldName.
     * 
     * @param fieldName
     *            要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
     * 设置fieldType.
     * 
     * @return 返回fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * 获取fieldType.
     * 
     * @param fieldType
     *            要设置的fieldType
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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

/**
 * 字段类类型.
 * 
 * @author yunhai
 *
 */
class ObjType {
    static final String String = "String";

    static final String Int = "int";

    static final String Integer = "Integer";

    static final String Boolean = "boolean";

    static final String Float = "float";

    static final String Date = "Date";

    static final String Long = "long";

    static final String Double = "double";

    static final String BigDecimal = "BigDecimal";

    static final String List = "List";

    static final String ListString = "List<String>"; // 特殊list专用

    static final String Map = "Map";

    static final String Set = "Set";

    static final String Defined = "Defined";
}
