/*
 * 文件名：PrintService.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： PrintService.java
 * 修改人：zxiaofan
 * 修改时间：2016年11月23日
 * 修改内容：新增
 */
package com.zxiaofan.util.other;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 控制台输出工具类，统一控制程序控制台输出 .
 * 
 * Note：不要implements接口，避免try catch嵌套时无法定位到原始异常位置。
 * 
 * @author github.zxiaofan.com
 */
@Component
public class PrintUtil {

    /**
     * 控制台输出级别（false-0不输出，error-3输出异常【默认】，info-2输出重要信息及异常，debug-1输出所有）.
     */
    private static String console;

    /**
     * 输出级别.
     */
    private static Integer level = -1;

    /**
     * 输出级别-error.
     */
    private static String levelError = "error";

    /**
     * 输出级别-info.
     */
    private static String levelInfo = "info";

    /**
     * 输出级别-debug.
     */
    private static String levelDebug = "debug";

    // /**
    // * 构造函数.
    // *
    // * 使用sping注入需要注释此无参构造函数
    // *
    // */
    // public PrintUtil() {
    // throw new RuntimeException("this is a util class,can not instance!");
    // }

    /**
     * 使用ThreadLocal解决SimpleDateFormat不同步问题.
     */
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 当前输出描述.
     */
    private static ThreadLocal<String> threadLevelDesc = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "default";
        }
    };

    /**
     * 直接输出所有信息.
     * 
     * @param param
     *            param
     */
    public static void print(Object param) {
        dealPrint(param);
    }

    /**
     * 重要信息.
     * 
     * @param param
     *            param
     */
    public static void printInfo(Object param) {
        if (level >= 2) {
            threadLevelDesc.set(levelInfo);
            dealPrint(param);
        }
    }

    /**
     * 异常.
     * 
     * @param param
     *            param
     */
    public static void printError(Object param) {
        if (level >= 3) {
            threadLevelDesc.set(levelError);
            dealPrint(param);
        }
    }

    /**
     * debug.
     * 
     * @param param
     *            param
     */
    public static void printDebug(Object param) {
        if (level >= 1) {
            threadLevelDesc.set(levelDebug);
            dealPrint(param);
        }
    }

    /**
     * 分类处理输出.
     * 
     * @param param
     *            param
     */
    private static void dealPrint(Object param) {
        if (param instanceof String) {
            print((String) param);
        } else if (param instanceof Throwable) {
            print((Throwable) param);
        } // 可增加打印Bean
    }

    /**
     * 输出字符串.
     * 
     * @param s
     *            字符串
     */
    private static void print(String s) {
        System.out.println("[" + threadLevelDesc.get() + "]" + ":[" + threadLocal.get().format(new Date()) + "]" + s);
        threadLocal.remove();
        threadLevelDesc.remove();
    }

    /**
     * 输出异常.
     * 
     * @param e
     *            e
     */
    private static void print(Throwable e) {
        print("---Position---");
        String print = locateException(e, 0) + locateException(e, 1); // 方便单独记录日志
        System.out.println(print);
        e.printStackTrace();
    }

    /**
     * 构造异常信息（文件名、方法名、行号）.
     * 
     * @param e
     *            异常
     * @param i
     *            异常父类级别
     * @return 异常信息
     */
    private static String locateException(Throwable e, int i) {
        if (e != null && null != e.getStackTrace() && i >= 0 && e.getStackTrace().length > i) {
            return ">>>" + e.getStackTrace()[i].getFileName() + "-" + e.getStackTrace()[i].getMethodName() + e.getStackTrace()[i].getLineNumber();
        }
        return "";
    }

    /**
     * 初始化日志级别.
     * 
     */
    private static synchronized void initLevel() {
        if (-1 != level) {
            return;
        }
        if (null == console || "".equals(console) || levelError.equals(console)) {
            level = 3;
        } else if ("false".equals(console)) {
            level = 0;
        } else if (levelInfo.equals(console)) {
            level = 2;
        } else if (levelDebug.equals(console)) {
            level = 1;
        } else {
            level = 3;
        }
    }

    /**
     * 初始化异常级别（未配置console时，console==null）.
     * 
     * @param console
     *            异常级别
     */
    @Value("${console}")
    private void setConsole(String console) {
        PrintUtil.console = console;
        initLevel();
    }
}
