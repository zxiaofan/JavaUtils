/*
 * 文件名：DictionaryUtilTest.java
 * 版权：Copyright 2007-2017 517na Tech. Co. Ltd. All Rights Reserved. 
 * 描述： DictionaryUtilTest.java
 * 修改人：xiaofan
 * 修改时间：2017年2月18日
 * 修改内容：新增
 */
package passwordUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.zxiaofan.util.other.ZipPwdUtil;
import com.zxiaofan.util.password.Check;
import com.zxiaofan.util.password.DictionaryUtil;

/**
 * 
 * @author xiaofan
 */
public class DictionaryUtilTest {
    static String path7ZFile = "E:\\123.7z";

    static int lengthMin = 5;

    static int lengthMax = 8;

    List<String> params = new ArrayList<>(Arrays.asList("z", "x", "f", "c"));

    /**
     * 校验器myCheck实时校验.
     */
    @Test
    public void testValidate() {
        for (int i = 0; i < 10; i++) {
            params.add(i + "");
        }
        DictionaryUtil.validate(params, lengthMin, lengthMax, myCheck);
    }

    /**
     * 生成全量字典.
     * 
     */
    @Test
    public void testBuildAll() {
        List<String> result = DictionaryUtil.buildAll(params, 3, 3);
        System.out.println(result.size() + ":" + result.toString());
    }

    /**
     * 重写校验器，校验7z压缩包密码.
     */
    static Check myCheck = new Check(lengthMin, lengthMax) {
        /**
         * {@inheritDoc}.
         */
        @Override
        protected boolean execute(String param) {
            if (ZipPwdUtil.validatePwd(path7ZFile, param)) {
                return true;
            }
            return false;
        }
    };
}
