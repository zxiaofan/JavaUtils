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

/**
 * 日期util
 * 
 * @author zxiaofan
 */
public class DateUtil {
    /**
     * 构造函数.
     * 
     */
    private DateUtil() {
        throw new RuntimeException("this is a util class,can not instance!");
    }

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 保留日期到某一级别（天、时、分、秒...）.
     * 
     * @param date
     *            date
     * @param calendarLevel
     *            保留级别，null保留到day
     * @return date
     */
    public static Date setDate(Date date, Integer calendarLevel) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (null == calendarLevel || Calendar.DATE == calendarLevel) { // 保留到 Day
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else if (Calendar.HOUR == calendarLevel) { // 保留到 Hour
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else if (Calendar.MINUTE == calendarLevel) { // 保留到 MINUTE
            cal.set(Calendar.SECOND, 0);
        } else if (Calendar.SECOND == calendarLevel) { // 保留到 SECOND
            date = parseDate(format.format(date), format);
            return date;
        }
        return cal.getTime();
    }

    /**
     * 比较两个日期的天数间隔.
     * 
     * @param time1
     *            time1
     * @param time2
     *            time2
     * @return int
     */
    public static int getDateInterval(Date time1, Date time2) {
        return getDateInterval(time1, time2, Calendar.DAY_OF_YEAR);
    }

    /**
     * 比较两个日期的间隔.
     * 
     * @param date1
     *            date1
     * @param date2
     *            date2
     * @return int
     */
    public static int getDateInterval(Date date1, Date date2, int calendarLevel) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int beginDay = calendar.get(calendarLevel);
        calendar.setTime(date2);
        int endDay = calendar.get(calendarLevel);
        return beginDay - endDay;
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
    public static Date parseDate(String time, DateFormat dateFormat) {
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
    public static Date parseDate(String time, String dateFormat) {
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
            date = parseDate(time, df);
        }
        return date;
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
        String dateStr = format.format(date);
        dateStr = dateStr.substring(0, 10);
        if (null != time) {
            time = time.trim();
            dateStr += " " + time;
        }
        date = parseDate(dateStr, format);
        return date;
    }
}
