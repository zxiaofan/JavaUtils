package utils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * 灵活获取Spring注入的bean.
 * 
 * 使用方法：①注册本util<bean id="springContextUtil" class="utils.SpringContextUtil"/>
 * 
 * ②获取bean：private static IPrintService printService=(IPrintService) SpringContextUtil.getBean("printServiceImpl");
 * 
 * @author zxiaofan
 *
 */
public class SpringContextUtil implements ApplicationContextAware {

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

    /**
     * 根据类类型获取bean实例.
     * 
     * @param classType
     *            待查找的类类型
     * @return Bean（可能需要强转）
     * @throws BeansException
     *             BeansException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getBean(Class classType) throws BeansException {
        return context.getBean(classType);
    }

    /**
     * 根据类类型获取所有bean实例（包括子类）.
     * 
     * PrintServiceImpl printServiceImpl=(PrintServiceImpl) map.get("printService");
     * 
     * @param classType
     *            待查找的类类型
     * @return bean集合Map<String, Object>:<key>the name of bean,<value>the bean instance
     * @throws BeansException
     *             BeansException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map<String, Object> getBeansOfType(Class classType) throws BeansException {
        return context.getBeansOfType(classType);
    }
}
