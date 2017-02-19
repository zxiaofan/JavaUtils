# JavaUtils
**Java工具类**

整理个人学习工作中常用的工具类，欢迎有兴趣的朋友共同维护。

By github.com/zxiaofan(https://github.com/zxiaofan/JavaUtils)

=====================================================

# utils
2016-08-29（原JDK-Study项目utils）  
**1、AutoPackageAndImport**  
　　针对个别项目package或import错乱，依据其路径自动导入package，并且修改错误的import。  
**2、CSharpToJavaModelUtils**  
　　C#的实体类转为Java实体类（Bean）。  
**3、JsonToJavaBean**  
　　json转JavaBean，支持CheckStyle和自定义转换规则。  
**4、BeanUtils**  
　　java实体工具类：  
　　4.1、针对源实体或目标实体分 5种copy级别。coverLevel覆盖级别： 1:source_field != null; 2:source_field != null (&&
!"".equals(source_field)); 3:target_field==null; 4:target_field==null(||
"".equals(target_field)); others:fullDeepCopy。  
　　4.2、获取特定属性值getProperty、为指定属性赋值setProperty  
　　4.3、为null属性赋初始值notNull  
**5、SpringContextUtil**  
　　多线程实现注入SpringContextUtil  
**6、GsonUtil**  
　　fromJson(String json, Class raw, Class actual);toJson避免String被转义;支持自定义时间格式  
**7、DateUtil**  
　　parse指定DateFormat格式化日期；dateToHms获取当前日期指定时间；getDateInterval比较两个日期的间隔；setDate保留日期到某一级别;parseAuto自动解析时间格式并parse时间（支持年月日时分秒毫秒格式，前包含）  
**8、StringUtil**  
　　isNullOrEmpty判空；Json、Xml首字母大转小、小转大；getInteger字符串转Integer（异常返回默认值）  
**9、FileUtil**  
　　getSpecificLine超大文本获取指定内容行;readFileToString读取本地文件为字符串;writeStringToFile将字符串数据存于本地  
**10、PrintUtil**  
　　控制台输出工具类，统一控制程序控制台输出  
**11、ZipPwdUtil**  
　　压缩文件密码破解工具类（暂支持7Z密码校验，持续更新ing）  
**12、RedisUtil**  
　　Redis工具类，基于redis2.9（暂支持get/set，待更新ing）  

# passwordUtil
2016-08-29（新增加解密工具类）  
**1、AESUtil**  
　　AES加解密。  
**2、DESUtil**  
　　DES、DES3加解密。  
**3、MD5**  
　　MD5。  
**4、DictionaryUtil**  
　　字典生成器，支持字典生成时实时校验密码的正确性。  

