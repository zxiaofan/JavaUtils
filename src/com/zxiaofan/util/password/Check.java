/*
 * 文件名：Check.java
 * 版权：Copyright 2007-2017 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： Check.java
 * 修改人：zxiaofan
 * 修改时间：2017年4月7日
 * 修改内容：新增
 */
package com.zxiaofan.util.password;

/**
 * 校验器，支持生成字典数据时立即校验.
 * 
 * @author xiaofan
 *
 */
public class Check {
    int minLen = 1; // 执行execute参数param最小位数

    int maxLen = Integer.MAX_VALUE;

    /**
     * 构造函数.
     * 
     * @param minLen
     *            最少位数
     * @param maxLen
     *            最多位数
     */
    public Check(int minLen, int maxLen) {
        super();
        this.minLen = minLen;
        this.maxLen = maxLen;
    }

    /**
     * 校验.
     * 
     * @param param
     *            参数
     * @return 校验结果
     */
    protected boolean execute(String param) {
        return false;
    };
}
