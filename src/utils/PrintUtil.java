/*
 * 文件名：PrintService.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： PrintService.java
 * 修改人：zxiaofan
 * 修改时间：2016年11月23日
 * 修改内容：新增
 */
package utils;

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
     * 控制台输出级别（false-0不输出，error-3输出异常，info-2输出重要信息【默认】，debug-1输出所有）.
     */
    private static String console;

    /**
     * 输出级别.
     */
    private static Integer level = -1;

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
     * 重要信息【默认】.
     * 
     * @param param
     *            param
     */
    public static void print(Object param) {
        if (level >= 2) {
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
        System.out.println("[" + threadLocal.get().format(new Date()) + "]" + s);
        threadLocal.remove();
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
        if (e != null && e.getStackTrace().length > i) {
            return ">>>" + e.getStackTrace()[i].getFileName() + "-" + e.getStackTrace()[i].getMethodName() + e.getStackTrace()[i].getLineNumber();
        }
        return null;
    }

    /**
     * 初始化日志级别.
     * 
     */
    private static void initLevel() {
        if (-1 != level) {
            return;
        }
        if (null == console || "".equals(console) || "error".equals(console)) {
            level = 3;
        } else if ("false".equals(console)) {
            level = 0;
        } else if ("info".equals(console)) {
            level = 2;
        } else if ("debug".equals(console)) {
            level = 1;
        } else {
            level = 2;
        }
    }

    /**
     * 初始化异常级别.
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
