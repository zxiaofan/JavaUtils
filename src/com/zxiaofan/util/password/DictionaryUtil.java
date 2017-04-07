/*
 * 文件名：DictionaryUtil.java
 * 版权：Copyright 2007-2017 517na Tech. Co. Ltd. All Rights Reserved. 
 * 描述： DictionaryUtil.java
 * 修改人：xiaofan
 * 修改时间：2017年2月12日
 * 修改内容：新增
 */
package com.zxiaofan.util.password;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典生成工具类
 * 
 * @author xiaofan
 */
public class DictionaryUtil {

    /**
     * 字典校验接口.
     * 
     * @param elements
     *            元素（可为字符串）
     * @param min
     *            最小位数
     * @param max
     *            最大位数
     * @return 符合校验的字符串
     */
    public static void validate(List<String> elements, int min, int max, Check check) {
        if (min > max) { // 保证min<=max
            min = min * max;
            max = min / max;
            min = min / max;
        }
        if (null == elements || min < 1) {
            return;
        }
        for (int i = min; i <= max; i++) {
            permutation(elements, i, check, i);
        }
    }

    /**
     * 可重复有序全排列.
     * 
     * @param elements
     *            元素（可为字符串）
     * @param min
     *            最小位数
     * @param max
     *            最大位数
     * @return 排列集合
     */
    public static List<String> buildAll(List<String> elements, int min, int max) {
        int size = 0; // 最终集合大小
        int eleNum = 0; // elements的元素个数
        List<String> results = null;
        if (min > max) { // 保证min<=max
            min = min * max;
            max = min / max;
            min = min / max;
        }
        if (null == elements || min < 1) {
            return results;
        } else {
            for (int i = min; i <= max; i++) {
                size += Math.pow(eleNum, min);
            }
        }
        results = new ArrayList<>(size);
        for (int i = min; i <= max; i++) {
            results.addAll(permutation(elements, i, null, null));
        }
        return results;
    }

    /**
     * 递归全排列，元素集合中筛选num个数全排列.
     * 
     * @param elements
     *            元素集合
     * @param num
     *            选几个元素组合
     * @return 排列结果
     */
    private static List<String> permutation(List<String> elements, int num, Check check, Integer numOut) {
        List<String> results = null;
        if (null == elements) {
            return results;
        }
        int eleNum = elements.size(); // elements的元素个数
        int size = (int) Math.pow(eleNum, eleNum);
        size = Math.min(size, 10000); // 避免结果集太大
        if (null != check) {
            size = Math.min(size, eleNum);
        }
        results = new ArrayList<>(size);
        if (num == 1) {
            for (String str : elements) {
                results.add(str);
            }
        } else {
            List<String> temp = permutation(elements, num - 1, check, numOut);
            if (null != temp) {
                for (String para : elements) {
                    for (String str : temp) {
                        String param = para + str;
                        if (null != check) { // 实时校验
                            results.add(param);
                            if (null != param && param.length() >= check.minLen && param.length() < check.maxLen) {
                                // System.out.println("param:" + param);
                                if (check.execute(param)) {
                                    System.out.println("success:" + param);
                                    System.exit(0); // 终止程序
                                }
                            }
                        } else {
                            results.add(param);
                        }
                    }
                }
            }
        }
        return results;
    }

}
