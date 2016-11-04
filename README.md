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
**4、BeanUtils**  
　　多线程实现注入SpringContextUtil  

# passwordUtil
2016-08-29（新增加解密工具类）  
**1、AESUtil**  
　　AES加解密。  
**2、DESUtil**  
　　DES、DES3加解密。  
**3、MD5**  
　　MD5。  

