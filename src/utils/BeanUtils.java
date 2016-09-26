/*
 * 文件名：BeanUtils.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： BeanUtils.java
 * 修改人：zxiaofan
 * 修改时间：2016年9月26日
 * 修改内容：新增
 */
package utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * github.zxiaofan.com
 * 
 * @author zxiaofan
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class BeanUtils {
    /**
     * 构造函数.
     * 
     */
    private BeanUtils() {
        throw new RuntimeException("this is a util class,can not instance");
    }

    /**
     * fullDeepCopy：source to target(target完全完全深复制为source)
     * 
     * @param source
     *            源实体类
     * @param target
     *            目标实体类
     */
    public static void copy(Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (int i = 0, j = fields.length; i < j; i++) {
            String propertyName = fields[i].getName();
            Object propertyValue = getProperty(source, propertyName);
            setProperty(target, propertyName, propertyValue);
        }
    }

    /**
     * 按需复制source里的属性到target
     * 
     * @param source
     *            源实体类
     * @param target
     *            目标实体类
     * @param coverLevel
     *            覆盖级别： 1:source_field != null; 2:source_field != null (&& !"".equals(source_field)); 3:target_field==null; 4:target_field==null(|| "".equals(target_field)); others:fullDeepCopy。
     */
    public static void copyLevel(Object source, Object target, int coverLevel) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (int i = 0, j = fields.length; i < j; i++) {
            String propertyName = fields[i].getName();
            Object propertyValue = getProperty(source, propertyName);
            switch (coverLevel) {
                case 1:
                    if (getProperty(source, propertyName) != null) {
                        setProperty(target, propertyName, propertyValue);
                    }
                    break;
                case 2:
                    if (!isNullOrEmpty(getProperty(source, propertyName))) {
                        setProperty(target, propertyName, propertyValue);
                    }
                    break;
                case 3:
                    if (null == getProperty(target, propertyName)) {
                        setProperty(target, propertyName, propertyValue);
                    }
                    break;
                case 4:
                    if (isNullOrEmpty(getProperty(target, propertyName))) {
                        setProperty(target, propertyName, propertyValue);
                    }
                    break;
                default:
                    setProperty(target, propertyName, propertyValue);
                    break;
            }
        }
    }

    /**
     * 根据属性名取值
     * 
     * @param bean
     *            实体
     * @param propertyName
     *            属性名
     * @return value
     */
    private static Object getProperty(Object bean, String propertyName) {
        Class clazz = bean.getClass();
        Field field = null;
        try {
            field = clazz.getDeclaredField(propertyName);
            Method method = clazz.getDeclaredMethod(getGetterName(field.getName()), new Class[]{});
            return method.invoke(bean, new Object[]{});
        } catch (Exception e) {
            if ("boolean".equals(field.getType()) || "java.lang.Boolean".equals(field.getType().getName())) { // field.getType()即class java.lang.Boolean判断equals失败
                try {
                    Method method = clazz.getDeclaredMethod(getIsName(field.getName()), new Class[]{});
                    return method.invoke(bean, new Object[]{});
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 给指定属性赋值
     * 
     * @param bean
     *            实体
     * @param propertyName
     *            属性名
     * @param value
     *            值
     */
    private static void setProperty(Object bean, String propertyName, Object value) {
        Class clazz = bean.getClass();
        Field field = null;
        try {
            field = clazz.getDeclaredField(propertyName);
            Method method = clazz.getDeclaredMethod(getSetterName(field.getName()), new Class[]{field.getType()});
            method.invoke(bean, new Object[]{value});
        } catch (Exception e) {
            if ("boolean".equals(field.getType()) || "java.lang.Boolean".equals(field.getType().getName())) {
                try {
                    Method method = clazz.getDeclaredMethod(getIsName(field.getName()), new Class[]{field.getType()});
                    method.invoke(bean, new Object[]{value});
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据属性名得到get方法
     * 
     * @param propertyName
     *            属性名
     * @return getName
     */
    private static String getGetterName(String propertyName) {
        String method = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        return method;
    }

    /**
     * 根据属性名得到is方法(boolean|Bollean型)
     * 
     * @param propertyName
     *            属性名
     * @return getName
     */
    private static String getIsName(String propertyName) {
        String method = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        return method;
    }

    /**
     * 根据属性名得到set方法
     * 
     * @param propertyName
     *            属性名
     * @return setName
     */
    private static String getSetterName(String propertyName) {
        String method = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        return method;
    }

    /**
     * 判断src是否为null(若src为String,判断是否为"").
     * 
     * @param src
     *            src
     * @return boolean
     */
    private static boolean isNullOrEmpty(Object src) {
        if (null == src)
            return true;
        if (src instanceof String) {
            return "".equals(((String) src).trim());
        }
        return false;
    }

}
