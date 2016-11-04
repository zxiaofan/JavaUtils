package utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zxiaofan
 *
 *         多线程实现注入
 * 
 *         private static IPrintService printService=(IPrintService) SpringContextUtil.getBean("printServiceImpl");
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * Spring应用上下文环境.
     */
    private static ApplicationContext applicationContext;

    /**
     * {@inheritDoc}.实现ApplicationContextAware接口的回调方法，设置上下文环境.
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * .
     * 
     * @return applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据bean名字获取bean实例,重写了bean方法，起主要作用.
     * 
     * @param beanName
     *            beanName默认为class名字（首字母小写），亦可@Service(value="printService")
     * @return Bean
     * @throws BeansException
     *             BeansException
     */
    public static Object getBean(String beanName) throws BeansException {
        return applicationContext.getBean(beanName);
    }
}
