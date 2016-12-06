/*
 * 文件名：DateUtil.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： DateUtil.java
 * 修改人：zxiaofan
 * 修改时间：2016年11月6日
 * 修改内容：新增
 */
package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
    private static DateFormat formatDefault = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 添加字段注释.
     */
    public static String format = "yyyy-MM-dd HH:mm:ss";

    /**
     * 添加字段注释.
     */
    public static String formatyMd = "yyyy-MM-dd";

    /**
     * 添加字段注释.
     */
    public static String formatyMdS = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * 添加字段注释.
     */
    public static String formatslash = "yyyy/MM/dd HH:mm:ss";

    /**
     * 添加字段注释.
     */
    public static String formatyMdSslash = "yyyy/MM/dd HH:mm:ss.S";

    /**
     * 添加字段注释.
     */
    public static final String DAY = "day"; // 粒度级别

    /**
     * 添加字段注释.
     */
    public static final String HOUR = "hour";

    /**
     * 添加字段注释.
     */
    public static final String MINUTE = "minute";

    /**
     * 添加字段注释.
     */
    public static final String SECOND = "second";

    /**
     * 日期特殊字符对应.
     */
    private static Map<String, String> mapSign = new HashMap<>();

    /**
     * 使用ThreadLocal解决SimpleDateFormat不同步问题.
     */
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    static {
        mapSign.put("上午|下午", "a");
        mapSign.put("星期[一二三四五六日天七]", "E");
        mapSign.put("CST", "z");
        mapSign.put("公元[前]?", "G");
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
     * 指定格式格式化日期.
     * 
     * @param date
     *            date
     * @param dateFormat
     *            dateFormat
     * @return time
     */
    public static String format(Date date, DateFormat dateFormat) {
        if (null == date) {
            return null;
        }
        if (null == dateFormat) { // 尽管dateFormat为null将编译报错，因为format有两个方法（2 param），依旧判断处理
            return threadLocal.get().format(date);
        }
        return dateFormat.format(date);
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
        if (null == dateFormat) {
            return threadLocal.get().format(date);
        }
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    /**
     * parse时间yyyy-MM-dd HH:mm:ss.
     * 
     * @param source
     *            source
     * @return Date
     */
    public static Date parse(String source) {
        try {
            return threadLocal.get().parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化时间.
     * 
     * @param dateFormat
     *            DateFormat
     * @param time
     *            time
     * @return Date
     */
    public static Date parse(String time, DateFormat dateFormat) {
        if (null == time) {
            return null;
        }
        try {
            Date date = dateFormat.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化时间.
     * 
     * @param time
     *            time
     * @param dateFormat
     *            dateFormat
     * @return Date
     */
    public static Date parse(String time, String dateFormat) {
        if (null == time) {
            return null;
        }
        DateFormat df = null;
        Date date = null;
        try {
            df = new SimpleDateFormat(dateFormat);
            date = df.parse(time);
        } catch (ParseException e) {
            time = time.replaceAll("/", "-");
            if ((!"".equals(time)) && (time.length() < dateFormat.length())) { // format字符串过长
                time += dateFormat.substring(time.length()).replaceAll("[YyMmDdHhSs]", "0");
            }
            date = parse(time, df);
        }
        return date;
    }

    /**
     * 自动解析时间格式并parse时间（支持年月日时分秒毫秒格式，前包含）.
     * 
     * @param time
     *            time
     * @return Date
     * @throws ParseException
     *             ParseException
     */
    public static Date parseAuto(String time) throws ParseException {
        if (null == time) {
            return null;
        }
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
            StringBuffer buffer = new StringBuffer();
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
        DateFormat df = new SimpleDateFormat(formatPattern);
        Date date = df.parse(time);
        return date;
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
        if (null == date) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (level) {
            case DAY: // 保留到 Day
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                break;
            case HOUR: // 保留到 Hour
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                break;
            case MINUTE: // 保留到 MINUTE
                cal.set(Calendar.SECOND, 0);
                break;
            case SECOND: // 保留到 SECOND
                threadLocal.set(formatDefault);
                date = parse(threadLocal.get().format(date), threadLocal.get());
                return date;
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
            case DAY: // 天
                num = (Double) (time / TimeUnit.DAYS.toMillis(1));
                break;
            case HOUR: // 小时
                num = (Double) (time / TimeUnit.HOURS.toMillis(1));
                break;
            case MINUTE: // 分钟
                num = (Double) (time / TimeUnit.MINUTES.toMillis(1));
                break;
            case SECOND: // 秒
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
     */
    public static Date dateToHms(Date date, String time) {
        if (null == date) {
            return null;
        }
        threadLocal.set(formatDefault);
        String dateStr = threadLocal.get().format(date);
        dateStr = dateStr.substring(0, 10);
        if (null != time) {
            time = time.trim();
            dateStr += " " + time;
        }
        date = parse(dateStr, threadLocal.get());
        return date;
    }
}
