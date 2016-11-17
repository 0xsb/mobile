package cmcc.mobile.admin.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    /**
     * @return 返回 context。
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * getBean
     * @param beanName beanName
     * @return bean
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHelper.applicationContext = applicationContext;
    }
}
