/*
 * 文件名：FileUtil.java
 * 版权：Copyright 2007-2016 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： FileUtil.java
 * 修改人：zxiaofan
 * 修改时间：2016年11月17日
 * 修改内容：新增
 */
package utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * github.zxiaofan.com.
 * 
 * @author zxiaofan
 */
public class FileUtil {
    static String max = "max";

    static Map<String, Long> mapIndex = new ConcurrentHashMap<>(); // <行key,index>

    /**
     * 超大文本获取指定内容行.
     * 
     * 文件行索引完成后，获取内容耗时在毫秒级别。
     * 
     * @param file
     *            file
     * @param content
     *            查找指定内容
     * @return String
     * @throws Exception
     *             Exception
     */
    @SuppressWarnings("resource")
    public static String getSpecificLine(File file, String content) throws Exception {
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file, "r");
        long fileLength = randomAccessFile.length(); // 获取当前文件的长度
        long indexStart = 0;
        if (!mapIndex.containsKey(max)) {
            mapIndex.put(max, 0L);
        }
        Future<String> future = null;
        if (mapIndex.containsKey(content)) {
            indexStart = mapIndex.get(content);
        } else {
            indexStart = mapIndex.get(max);
            // 大文件缓存全部key索引耗时较多，建议按行匹配并发,取优先完成的数据
            ExecutorService service = Executors.newFixedThreadPool(1);
            // future = service.submit(new QueryCallable(file, content));
        }
        if (indexStart > fileLength || indexStart < 0) {
            System.out.println("index illegal");
            return null;
        }
        long index = indexStart;
        randomAccessFile.seek(index);
        StringBuffer buffer = new StringBuffer();
        String line = null;
        boolean getData = false;
        while (index <= fileLength && null != (line = randomAccessFile.readLine())) {
            line = new String(line.getBytes("ISO-8859-1"), "utf-8"); // 处理乱码
            String key = line.substring(0, Math.min(10, line.length())); // 假设key为我们要匹配的content
            if (key.equals(content)) {
                buffer.append(line);
                getData = true;
            } else if (getData && !key.equals(content)) {
                break;
            }
            if (!mapIndex.containsKey(key)) {
                mapIndex.put(key, index);
            }
            if (index > mapIndex.get(max)) {
                mapIndex.put(max, index);
            }
            index = randomAccessFile.getFilePointer(); // 获取下次循环的开始指针
            if (null != future) {
                try {
                    String res = future.get(1, TimeUnit.MILLISECONDS);
                    if (null != res) {
                        buffer.setLength(0);
                        buffer.append(res);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println();
                }
            }
        }
        return buffer.toString();
    }

}
