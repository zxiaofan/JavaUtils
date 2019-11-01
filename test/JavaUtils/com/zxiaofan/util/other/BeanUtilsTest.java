package com.zxiaofan.util.other;


import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Project: JavaUtils
 * Author: zxiaofan
 * Date: 2019/11/1
 * Time: 16:56
 * Desc: create.
 */
public class BeanUtilsTest {
    /**
     * 相同类覆盖准确性测试
     */
    @Test
    public void copyAccuracyTest() {
        Student source = new Student("@zxiaofan", 1, new Date(), new BigDecimal("1.2"), true, false, new Timestamp(System.currentTimeMillis()), 2.0D);
        Student dest = new Student();
        BeanUtils.copy(source, dest);
        System.out.println("copyAccuracyTest:" + dest.toString());
        Assert.assertEquals(source.toString(), dest.toString());
    }

    /**
     * 直接覆盖测试
     */
    @Test
    public void copyCoverTest() {
        Student source = new Student(null, 1, new Date(), new BigDecimal("1.2"), true, false, new Timestamp(System.currentTimeMillis()), 2.0D);
        Student dest = new Student();
        dest.setName("destName");
        BeanUtils.copy(source, dest);
        Assert.assertNull(dest.getName());
    }

    @Test
    public void copyTimeOfSampleType() {
        Student source = new Student("@zxiaofan", 1, new Date(), new BigDecimal("1.2"), true, false, new Timestamp(System.currentTimeMillis()), 2.0D);
        int times = 100000;
        Long timeStart = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Student dest = new Student();
            BeanUtils.copy(source, dest);
        }
        Long timeEnd = System.currentTimeMillis();
        System.out.println("copyTimeOfSampleType:" + times + "次耗时" + (timeEnd - timeStart) + "毫秒"); // 10W次1~2秒以下
    }

    @Test
    public void copyTimeOfDestLittle() {
        Student source = new Student("@zxiaofan", 1, new Date(), new BigDecimal("1.2"), true, false, new Timestamp(System.currentTimeMillis()), 2.0D);
        Person dest1 = new Person();
        BeanUtils.copy(source, dest1);
        System.out.println("copyTimeOfDestLittle:" + dest1.toString()); // 10W次1.5秒以下

        int times = 100000;
        Long timeStart = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Person dest = new Person();
            BeanUtils.copy(source, dest);
        }
        Long timeEnd = System.currentTimeMillis();
        System.out.println("copyTimeOfDestLittle:" + times + "次耗时" + (timeEnd - timeStart) + "毫秒"); // 10W次1秒以下
    }

    @Test
    public void copyTimeOfSourceLittle() {
        Person source = new Person("@zxiaofan", 1, 2);
        Student dest1 = new Student();
        BeanUtils.copy(source, dest1);
        System.out.println("copyTimeOfSourceLittle:" + dest1.toString()); // 10W次1.2秒以下

        int times = 100000;
        Long timeStart = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Student dest = new Student();
            BeanUtils.copy(source, dest);
        }
        Long timeEnd = System.currentTimeMillis();
        System.out.println("copyTimeOfSourceLittle:" + times + "次耗时" + (timeEnd - timeStart) + "毫秒"); // 10W次1~2秒
    }

    @Test
    public void copyTimeOfLevel() {
        Student source = new Student("@zxiaofan", 1, new Date(), new BigDecimal("1.2"), true, false, new Timestamp(System.currentTimeMillis()), 2.0D);
        int times = 100000;
        copyLevel(source, times, 4);
        copyLevel(source, times, 3);
        copyLevel(source, times, 2);
        copyLevel(source, times, 1);
        copyLevel(source, times, 0);
    }

    private void copyLevel(Student source, int times, int coverLevel) {
        Long timeStart = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Person dest = new Person();
            BeanUtils.copy(source, dest, coverLevel);
        }
        Long timeEnd = System.currentTimeMillis();
        System.out.println(coverLevel + "-copyLevel:" + times + "次耗时" + (timeEnd - timeStart) + "毫秒"); // 10W次1秒以下
    }
}

class Student implements Serializable {

    private String name;
    private int age;
    private Date date;
    private BigDecimal decimal;
    private Boolean isBool;
    private Boolean boolGet;
    private Timestamp timestamp;
    private Double grade;

    public Student() {
    }

    public Student(String name, int age, Date date, BigDecimal decimal, Boolean isBool, Boolean boolGet, Timestamp timestamp, Double grade) {
        this.name = name;
        this.age = age;
        this.date = date;
        this.decimal = decimal;
        this.isBool = isBool;
        this.boolGet = boolGet;
        this.timestamp = timestamp;
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", date=" + date +
                ", decimal=" + decimal +
                ", boolGet=" + boolGet +
                ", timestamp=" + timestamp +
                ", grade=" + grade +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
    }

    public Boolean isIsBool() {
        return isBool;
    }

    public void setBool(Boolean bool) {
        isBool = bool;
    }

    public Boolean getBoolGet() {
        return boolGet;
    }

    public void setBoolGet(Boolean boolGet) {
        this.boolGet = boolGet;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}

class Person implements Serializable {

    private String name;
    private int year;
    private Integer date;

    public Person() {
    }

    public Person(String name, int year, int date) {
        this.name = name;
        this.year = year;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", date=" + date +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }
}