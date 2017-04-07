package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zxiaofan.util.rpc.RedisUtil;

import junit.framework.TestCase;

/**
 * 
 * @author zxiaofan
 */
public class RedisUtilTest extends TestCase {
    String keyR = "key1"; // 对指定key操作

    int num = 500; // 多线程自增次数

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                incr(keyR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * 并发测试（多线程多同一key自增）.
     * 
     * 证明：Redis是单线程的，对redis的操作都是具有原子性的,是线程安全的操作。
     * 
     * @throws Exception
     */
    public void testConcurrency() throws Exception {
        RedisUtil.set(keyR, "0");
        // System.out.print(RedisUtil.getValue(keyR) + "-->");
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < num; i++) {
            service.execute(runnable);
        }
        service.shutdown();
        while (!service.isTerminated()) {
            Thread.sleep(1000);
        }
        assertEquals("" + num, RedisUtil.get(keyR));
    }

    /**
     * 循环自增（务必加锁）.
     * 
     * @param key
     *            自增key
     * @throws Exception
     *             异常
     */
    private synchronized static void incr(String key) throws Exception {
        RedisUtil.set(key, String.valueOf(Integer.valueOf(RedisUtil.get(key)) + 1));
    }
}
