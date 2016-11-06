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
    private StringUtil() {
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
                    if (Character.isUpperCase(var)) {
                        var = Character.toLowerCase(var);
                    }
                    check = 0;
                }
            }
            buffer.append(var);
        }
        return buffer.toString();
    }

    /**
     * json属性名首字母小写转大写.
     * 
     * @param json
     *            json
     * @return String
     */
    public static String lowerToUpperForJson(String json) {
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
                    if (Character.isLowerCase(var)) {
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
     * @return Integer
     */
    public static Integer getInteger(Object param) {
        Integer result = null;
        if (param != null && param instanceof String && !"".equals(param)) {
            String p = (String) param;
            try {
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
        char[] chars = xmlStr.toCharArray();
        StringBuffer resultString = new StringBuffer();
        boolean isLabel = false;
        for (char var : chars) {
            if (var == '<') {
                isLabel = true;
            } else {

                if (var == '/') {
                    isLabel = true;
                } else if (isLabel && Character.isLowerCase(var)) {
                    var = Character.toUpperCase(var);
                    isLabel = false;
                }
            }
            resultString.append(var);
        }
        return resultString.toString();
    }

    /**
     * Xml属性首字母，大写转大写.
     * 
     * @param xmlStr
     *            xmlStr
     * @return String
     */
    public static String upperToLowerForXml(String xmlStr) {
        char[] chars = xmlStr.toCharArray();
        StringBuffer resultString = new StringBuffer();
        boolean isLabel = false;
        for (char var : chars) {
            if (var == '<') {
                isLabel = true;
            } else {
                if (var == '/') {
                    isLabel = true;
                } else if (isLabel && Character.isUpperCase(var)) {
                    var = Character.toLowerCase(var);
                    isLabel = false;
                }
            }
            resultString.append(var);
        }
        return resultString.toString();
    }

}
