/*
 * 文件名：DateUtil.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： DateUtil.java
 * 修改人：zxiaofan
 * 修改时间：2016年11月6日
 * 修改内容：新增
 */
package com.zxiaofan.util.other;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 日期util.
 * 
 * @author zxiaofan
 */
public class DateUtil {
    /**
     * 构造函数.
     * 
     */
    public DateUtil() {
        throw new RuntimeException("this is a util class,can not instance!");
    }

    /**
     * 添加字段注释.
     */
    public static final String ENUM_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 添加字段注释.
     */
    public static final String ENUM_FORMAT_YMD = "yyyy-MM-dd";

    /**
     * 添加字段注释.
     */
    public static final String ENUM_FORMAT_YMDS = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * 添加字段注释.
     */
    public static final String ENUM_FORMAT_SLASH = "yyyy/MM/dd HH:mm:ss";

    /**
     * 添加字段注释.
     */
    public static final String ENUM_FORMAT_YMDS_SLASH = "yyyy/MM/dd HH:mm:ss.S";

    /**
     * 添加字段注释.
     */
    public static final String LEVEL_DAY = "day"; // 粒度级别

    /**
     * 添加字段注释.
     */
    public static final String LEVEL_HOUR = "hour";

    /**
     * 添加字段注释.
     */
    public static final String LEVEL_MINUTE = "minute";

    /**
     * 添加字段注释.
     */
    public static final String LEVEL_SECOND = "second";

    /**
     * 日期特殊字符对应.
     */
    private static Map<String, String> mapSign = new HashMap<>();

    /**
     * 使用ThreadLocal保证SimpleDateFormat线程安全.
     */
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 使用ThreadLocal保证SimpleDateFormat线程安全.
     */
    private static ThreadLocal<DateFormat> threadLocalYMd = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 初始化DateFormat标志位.
     * 
     */
    private static void initMapSign() {
        if (mapSign.isEmpty()) {
            mapSign.put("上午|下午", "a");
            mapSign.put("星期[一二三四五六日天七]", "E");
            mapSign.put("CST", "z");
            mapSign.put("公元[前]?", "G");
        }
    }

    /**
     * 常规日期格式yyyy-MM-dd HH:mm:ss.
     * 
     * @param date
     *            date
     * @return time
     */
    public static String format(Date date) {
        return threadLocal.get().format(date);
    }

    /**
     * 常规日期格式yyyy-MM-dd.
     * 
     * @param date
     *            date
     * @return time
     */
    public static String formatYMd(Date date) {
        return threadLocalYMd.get().format(date);
    }

    /**
     * 格式化时间.
     * 
     * @param date
     *            date
     * @param dateFormat
     *            dateFormat
     * @return time
     */
    public static String format(Date date, String dateFormat) {
        if (null == date) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    /**
     * parse时间(yyyy-MM-dd HH:mm:ss).
     * 
     * @param source
     *            source
     * @return Date
     * @throws ParseException
     *             ParseException
     */
    public static Date parse(String source) throws ParseException {
        return threadLocal.get().parse(source);
    }

    /**
     * parse时间(yyyy-MM-dd).
     * 
     * @param source
     *            source
     * @return Date
     * @throws ParseException
     *             ParseException
     */
    public static Date parseYMd(String source) throws ParseException {
        return threadLocalYMd.get().parse(source);
    }

    /**
     * 格式化时间.
     * 
     * @param time
     *            time
     * @param dateFormat
     *            dateFormat
     * @return Date
     * @throws ParseException
     *             ParseException
     */
    public static Date parse(String time, String dateFormat) throws ParseException {
        if (isNullOrEmpty(time) || isNullOrEmpty(dateFormat)) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date date = df.parse(time);
        return date;
    }

    /**
     * 自动解析时间格式并parse（时间格式为yyyyMMddHHmmssS，默认24小时制、前包含且必须包含yyyyMMdd）.
     * 
     * @param time
     *            time
     * @return Date
     * @throws ParseException
     *             ParseException
     */
    public static Date parseAuto(String time) throws ParseException {
        if (isNullOrEmpty(time) || time.length() < 8) {
            return null;
        }
        initMapSign();
        time = time.trim();
        String formatPattern = "";
        if (time.matches("[\\d]+")) { // 纯数字
            String all = "yyyyMMddHHmmssSSS";
            if (time.length() > all.length()) { // 超长截取
                time = time.substring(0, all.length());
            }
            formatPattern = all.substring(0, time.length());
        } else {
            char next = 'y';
            String idNext = "yMdHmsS";
            StringBuilder buffer = new StringBuilder();
            for (char var : time.toCharArray()) {
                if (String.valueOf(var).matches("[0-9]")) {
                    buffer.append(next);
                } else if ("T".equals(String.valueOf(var))) {
                    buffer.append("'").append(var).append("'");
                } else {
                    buffer.append(var);
                    next = idNext.charAt(Math.min(idNext.indexOf(next) + 1, idNext.length() - 1));
                }
            }
            formatPattern = buffer.toString();
        }
        for (Entry<String, String> entry : mapSign.entrySet()) {
            formatPattern = formatPattern.replaceAll(entry.getKey(), entry.getValue());
        }
        return parse(time, formatPattern);
    }

    /**
     * 是否为空或"".
     * 
     * @param param
     *            param
     * @return boolean
     */
    private static boolean isNullOrEmpty(String param) {
        return null == param || "".equals(param.trim());
    }

    /**
     * 日期增加num天.
     * 
     * @param date
     *            date
     * @param num
     *            加减天数
     * @return Date
     */
    public static Date addDate(Date date, int num) {
        return addDate(date, Calendar.DATE, num);
    }

    /**
     * 时间增加.
     * 
     * @param date
     *            date
     * @param calendar
     *            加减级别Calendar
     * @param num
     *            加减天数
     * @return Date
     */
    public static Date addDate(Date date, int calendar, int num) {
        if (null == date || 0 == num) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendar, num);
        return cal.getTime();
    }

    /**
     * 保留日期到某一级别（天、时、分、秒...）.
     * 
     * @param date
     *            date
     * @param level
     *            保留级别，null保留到day
     * @return date
     */
    public static Date setDate(Date date, String level) {
        if (null == date || null == level) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (level) {
            case LEVEL_DAY: // 保留到 Day
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                break;
            case LEVEL_HOUR: // 保留到 Hour
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                break;
            case LEVEL_MINUTE: // 保留到 MINUTE
                cal.set(Calendar.SECOND, 0);
                break;
            case LEVEL_SECOND: // 保留到 SECOND
                cal.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
        return cal.getTime();
    }

    /**
     * 比较两个日期的间隔（时间差绝对值,向下取整）（day、hour、minute）.
     * 
     * @param date1
     *            date1
     * @param date2
     *            date2
     * @param level
     *            比较级别
     * @return int 无对应时间间隔级别
     */
    public static Integer getDateInterval(Date date1, Date date2, String level) {
        Double num = dateInterval(date1, date2, level);
        if (null == num) {
            return null;
        }
        return (int) Math.floor(num);
    }

    /**
     * 比较两个日期的间隔（时间差绝对值,向上取整）（day、hour、minute）.
     * 
     * @param date1
     *            date1
     * @param date2
     *            date2
     * @param level
     *            比较级别
     * @return int 无对应时间间隔级别
     */
    public static Integer getDateIntervalCeil(Date date1, Date date2, String level) {
        Double num = dateInterval(date1, date2, level);
        if (null == num) {
            return null;
        }
        return (int) Math.ceil(num);
    }

    /**
     * 比较两个日期的间隔（day、hour、minute）.
     * 
     * @param date1
     *            date1
     * @param date2
     *            date2
     * @param level
     *            比较级别
     * @return int 无对应时间间隔级别
     */
    private static Double dateInterval(Date date1, Date date2, String level) {
        Double time = (double) (date1.getTime() - date2.getTime());
        if (time < 0) {
            time = time * -1;
        }
        Double num = null;
        switch (level) {
            case LEVEL_DAY: // 天
                num = (Double) (time / TimeUnit.DAYS.toMillis(1));
                break;
            case LEVEL_HOUR: // 小时
                num = (Double) (time / TimeUnit.HOURS.toMillis(1));
                break;
            case LEVEL_MINUTE: // 分钟
                num = (Double) (time / TimeUnit.MINUTES.toMillis(1));
                break;
            case LEVEL_SECOND: // 秒
                num = (Double) (time / TimeUnit.SECONDS.toMillis(1));
                break;
            default:
                break;
        }
        return num;
    }

    /**
     * 获取当前日期指定时间.
     * 
     * @param date
     *            date
     * 
     * @param time
     *            time
     * @return date
     * @throws ParseException
     *             ParseException
     */
    public static Date dateToHms(Date date, String time) throws ParseException {
        if (null == date || isNullOrEmpty(time)) {
            return date;
        }
        StringBuilder timeBuf = new StringBuilder();
        String dateStr = formatYMd(date);
        timeBuf.append(dateStr).append(" ");
        time = time.trim();
        while (!time.matches(".*\\d")) {
            time = time.substring(0, time.length() - 1);
        }
        timeBuf.append(time);
        if (time.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            timeBuf.append(".0");
        } else if (time.matches("\\d{1,2}:\\d{1,2}")) {
            timeBuf.append(":00.0");
        } else if (time.matches("\\d{1,2}")) {
            timeBuf.append(":00:00.0");
        }
        date = parse(timeBuf.toString(), ENUM_FORMAT_YMDS);
        return date;
    }

    /**
     * 将Date类转换为XMLGregorianCalendar.
     * 
     * @param date
     *            date
     * @return XMLGregorianCalendar
     * @throws DatatypeConfigurationException
     *             DatatypeConfigurationException
     */
    public static XMLGregorianCalendar dateToXmlDate(Date date) throws DatatypeConfigurationException {
        XMLGregorianCalendar dateType = null;
        if (null != date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            DatatypeFactory dtf = DatatypeFactory.newInstance();
            dateType = dtf.newXMLGregorianCalendar();
            if (null != dateType) {
                dateType.setYear(cal.get(Calendar.YEAR));
                // 由于Calendar.MONTH取值范围为0~11,需要加1
                dateType.setMonth(cal.get(Calendar.MONTH) + 1);
                dateType.setDay(cal.get(Calendar.DAY_OF_MONTH));
                dateType.setHour(cal.get(Calendar.HOUR_OF_DAY));
                dateType.setMinute(cal.get(Calendar.MINUTE));
                dateType.setSecond(cal.get(Calendar.SECOND));
            }
        }
        return dateType;
    }

    /**
     * 清理ThreadLocal（每次线程结束都应执行此操作）.
     * 
     */
    public static void clearThreadLocal() {
        threadLocal.remove();
        threadLocalYMd.remove();
    }
}
