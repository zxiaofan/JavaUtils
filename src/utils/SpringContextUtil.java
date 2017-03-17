package utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author zxiaofan
 *
 *         多线程实现注入
 * 
 *         private static IPrintService printService=(IPrintService) SpringContextUtil.getBean("printServiceImpl");
 */
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * 构造函数.
     * 
     */
    public SpringContextUtil() {
        throw new RuntimeException("this is a util class,can not instance!");
    }

    /**
     * Spring应用上下文环境.
     */
    private static ApplicationContext context;

    /**
     * {@inheritDoc}.实现ApplicationContextAware接口的回调方法，设置上下文环境.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 获得ApplicationContext.
     * 
     * @return applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 根据bean名字获取bean实例.
     * 
     * @param beanName
     *            beanName默认为class名字（首字母小写），亦可@Service(value="printService")
     * @return Bean（可能需要强转）
     * @throws BeansException
     *             BeansException
     */
    public static Object getBean(String beanName) throws BeansException {
        return context.getBean(beanName);
    }
}
