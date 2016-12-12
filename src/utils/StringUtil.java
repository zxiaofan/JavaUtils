/*
 * 文件名：StringUtil.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： StringUtil.java
 * 修改人：zxiaofan
 * 修改时间：2016年11月6日
 * 修改内容：新增
 */
package utils;

/**
 * 
 * @author zxiaofan
 */
public class StringUtil {
    /**
     * 构造函数.
     * 
     */
    public StringUtil() {
        throw new RuntimeException("this is a util class,can not instance!");
    }

    /**
     * 字符串判空.
     * 
     * @param src
     * @return
     */
    public static boolean isNullOrEmpty(String src) {
        if (src == null)
            return true;
        return "".equals(src.trim());
    }

    /**
     * json属性名首字母大写转小写.
     * 
     * @param json
     *            json
     * @return String
     */
    public static String upperToLowerForJson(String json) {
        return jsonCaseChange(json, 1);
    }

    /**
     * json属性名首字母小写转大写.
     * 
     * @param json
     *            json
     * @return String
     */
    public static String lowerToUpperForJson(String json) {
        return jsonCaseChange(json, 2);
    }

    /**
     * json属性首字母大小写转换..
     * 
     * @param json
     *            json
     * @param jsonCase
     *            1大转小，2小转大
     * @return json
     */
    private static String jsonCaseChange(String json, int jsonCase) {
        char[] chars = json.toCharArray();
        StringBuffer buffer = new StringBuffer();
        int check = 0;
        for (char var : chars) {
            if (var == '{' || var == ',') {
                check = 1;
            } else {
                if (check == 1) {
                    if (var == '"') {
                        check = 2;
                    } else {
                        check = 0;
                    }
                } else if (check == 2) {
                    if (1 == jsonCase) {
                        var = Character.toLowerCase(var);
                    } else if (2 == jsonCase) {
                        var = Character.toUpperCase(var);
                    }
                    check = 0;
                }
            }
            buffer.append(var);
        }
        return buffer.toString();
    }

    /**
     * String 转Integer.
     * 
     * @param param
     *            param
     * @param value
     *            转换异常则返回此值
     * @return Integer
     */
    public static Integer getInteger(Object param, Integer value) {
        Integer result = getInteger(param);
        if (null == result) {
            return value;
        } else {
            return result;
        }
    }

    /**
     * String 转Integer.
     * 
     * @param param
     *            param
     * @return Integer
     */
    public static Integer getInteger(Object param) {
        Integer result = null;
        if (param != null && !"".equals(param)) {
            try {
                String p = String.valueOf(param);
                result = Integer.valueOf(p);
            } catch (NumberFormatException e) {
                // e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Xml属性首字母，小写转大写.
     * 
     * @param xmlStr
     *            xmlStr
     * @return String
     */
    public static String lowerToUpperForXml(String xmlStr) {
        return xmlCaseChange(xmlStr, 2);
    }

    /**
     * Xml属性首字母，大写转大写.
     * 
     * @param xmlStr
     *            xmlStr
     * @return String
     */
    public static String upperToLowerForXml(String xmlStr) {
        return xmlCaseChange(xmlStr, 1);
    }

    /**
     * xml属性首字母大小写转换.
     * 
     * @param xmlStr
     *            xmlStr
     * @param xmlCase
     *            1大转小，2小转大
     * @return xmlStr
     */
    private static String xmlCaseChange(String xmlStr, int xmlCase) {
        char[] chars = xmlStr.toCharArray();
        StringBuffer resultString = new StringBuffer();
        boolean isLabel = false; // <Header>
        boolean isLabelLeft = true; // <Header Name="">
        char last = 0; // 上一个字符
        char var = 0;
        String ingore = "";
        for (int i = 0; i < chars.length; i++) {
            var = chars[i];
            if (var == '<') {
                isLabel = true;
                isLabelLeft = true;
            } else if (var == '?' && last == '<' && subStringFromIToChar(xmlStr, i, " ").equals("?xml")) { // <?xml version="1.0">
                isLabel = false;
                isLabelLeft = false;
            } else if (var == 'x' && last == ' ' && !"".equals(ingore = subStringFromIToChar(xmlStr, i, "=")) && ("xmlns".equals(ingore) || ingore.startsWith("xmlns:") || ingore.startsWith("xsi:"))) {
                isLabelLeft = false; // xml特殊标签只允许小写
            } else if (var == '/' && (last == '<')) {
                isLabel = true;
            } else if (var == '"' && last == '=') {
                isLabelLeft = false;
            } else if (isLabel && (var == ' ' && last == '"')) {
                isLabelLeft = true;
            } else if (var == '>') {
                isLabel = false;
                isLabelLeft = false;
            } else if (isLabel || (isLabelLeft && last == ' ')) {
                if (1 == xmlCase) {
                    var = Character.toLowerCase(var);
                } else if (2 == xmlCase) {
                    var = Character.toUpperCase(var);
                }
                if (!isLabel) {
                    isLabelLeft = false;
                }
                isLabel = false;
            }
            resultString.append(var);
            last = var;
        }
        return resultString.toString();
    }

    /**
     * TODO 添加方法注释.
     * 
     * @param index
     * @param var
     * @return
     */
    private static String subStringFromIToChar(String str, int index, String var) {
        if (null == str || index < 0 || index > str.length() || null == var) {
            return "";
        }
        int end = str.indexOf(var, index);
        if (end < 0 || end - index > 25) {
            return "";
        }
        return str.substring(index, end);
    }
}
