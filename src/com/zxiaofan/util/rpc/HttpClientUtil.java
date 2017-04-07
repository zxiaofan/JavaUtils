/*
 * 文件名：FileUtil.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： FileUtil.java
 * 修改人：zxiaofan
 * 修改时间：2017年04月07日
 * 修改内容：新增
 */
package com.zxiaofan.util.rpc;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient的Http工具类.
 * 
 * @author yunhai
 */
public class HttpClientUtil {
    /**
     * 默认超时时间.
     */
    public static int timeoutDefault = 60000;

    /**
     * 请求参数配置.
     */
    private static RequestConfig requestConfigDefault = null;

    /**
     * HttpClient-post（默认超时时间timeoutDefault）.
     * 
     * @param httpClient
     *            httpClient
     * @param httpPost
     *            httpPost
     * @return result
     * @throws Exception
     *             Exception
     */
    public static String execute(HttpClient httpClient, HttpPost httpPost) throws Exception {
        httpPost.setConfig(getDefaultRequestConfig());
        HttpResponse execute = httpClient.execute(httpPost);
        String result = EntityUtils.toString(execute.getEntity(), "UTF-8");
        return result;
    }

    /**
     * HttpClient-post.
     * 
     * @param httpClient
     *            httpClient
     * @param httpPost
     *            httpPost
     * @param requestConfig
     *            requestConfig
     * @return result
     * @throws Exception
     *             Exception
     */
    public static String execute(HttpClient httpClient, HttpPost httpPost, RequestConfig requestConfig) throws Exception {
        httpPost.setConfig(requestConfig);
        HttpResponse execute = httpClient.execute(httpPost);
        String result = EntityUtils.toString(execute.getEntity(), "UTF-8");
        return result;
    }

    /**
     * HttpClient-Get（默认超时时间timeoutDefault）.
     * 
     * @param httpClient
     *            httpClient
     * @param httpGet
     *            httpGet
     * @return String
     * @throws Exception
     *             Exception
     */
    public static String execute(HttpClient httpClient, HttpGet httpGet) throws Exception {
        httpGet.setConfig(getDefaultRequestConfig());
        HttpResponse execute = httpClient.execute(httpGet);
        String result = EntityUtils.toString(execute.getEntity(), "UTF-8");
        return result;
    }

    /**
     * HttpClient-Get.
     * 
     * @param httpClient
     *            httpClient
     * @param httpGet
     *            httpGet
     * @param requestConfig
     *            requestConfig
     * @return String
     * @throws Exception
     *             Exception
     */
    public static String execute(HttpClient httpClient, HttpGet httpGet, RequestConfig requestConfig) throws Exception {
        httpGet.setConfig(requestConfig);
        HttpResponse execute = httpClient.execute(httpGet);
        String result = EntityUtils.toString(execute.getEntity(), "UTF-8");
        return result;
    }

    /**
     * 获取RequestConfig.
     * 
     * @param timeout
     *            超时时间
     * @return RequestConfig
     */
    public static RequestConfig getRequestConfig(int timeout) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        return requestConfig;
    }

    /**
     * 获取RequestConfig(默认超时timeoutDefault).
     * 
     * @return RequestConfig
     */
    public static RequestConfig getDefaultRequestConfig() {
        if (null == requestConfigDefault) {
            synchronized (HttpClientUtil.class) {
                if (null == requestConfigDefault) {
                    requestConfigDefault = RequestConfig.custom().setConnectionRequestTimeout(timeoutDefault).setSocketTimeout(timeoutDefault).setConnectTimeout(timeoutDefault).build();
                }
            }
        }
        return requestConfigDefault;
    }

}
