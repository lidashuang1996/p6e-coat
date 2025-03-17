package club.p6e.coat.auth;

import club.p6e.coat.auth.web.reactive.controller.*;
import club.p6e.coat.auth.web.reactive.token.LocalStorageCacheTokenGenerator;
import club.p6e.coat.auth.web.reactive.token.LocalStorageCacheTokenValidator;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class P6eAuth {

    private static final Properties PROPERTIES = Properties.getInstance();

    public Properties getProperties() {
        return PROPERTIES;
    }


    public void init() {

    }

    public void initWeb() {

    }

    public void initWebReactive() {

    }


    public void initLogin() {
        if (PROPERTIES.getLogin() != null && PROPERTIES.getLogin().isEnable()) {
            register(AuthenticationLoginController.class);
            register(LocalStorageCacheTokenGenerator.class);
            register(LocalStorageCacheTokenValidator.class);
            initAccountPasswordLogin();
        }
    }

    public static void initAccountPasswordLogin() {
        final Properties.Login.AccountPassword config = PROPERTIES.getLogin().getAccountPassword();
        if (config.isEnable()) {
            register(AccountPasswordLoginController.class);

            if (config.isEnableTransmissionEncryption()) {
                register(PasswordSignatureController.class);
            }
        }
    }

    public static void initRegister() {
        final Properties.Register config = PROPERTIES.getRegister();
        if (config.isEnable()) {
            register(RegisterController.class);
        }
    }

    public static void initForgotPassword() {
        final Properties.ForgotPassword config = PROPERTIES.getForgotPassword();
        if (config.isEnable()) {
            register(ForgotPasswordController.class);
        }
    }

    /**
     * Register Bean
     *
     * @param clazz Class Object
     */
    private static synchronized void register(Class<?> clazz) {
        register(clazz, true, true);
    }

    /**
     * Register Bean
     *
     * @param clazz            Class Object
     * @param isScanSelf       Class Object Scan Self Bean
     * @param isScanInterfaces Class Object Scan Interfaces Bean
     */
    private static synchronized void register(Class<?> clazz, boolean isScanSelf, boolean isScanInterfaces) {
        final DefaultListableBeanFactory factory = SpringUtil.getBean(DefaultListableBeanFactory.class);
        if (factory != null) {
            boolean bool = false;
            try {
                if (factory.containsBean(clazz.getName())) {
                    factory.getBean(clazz);
                    bool = true;
                }
            } catch (Exception e) {
                // ignore exception
            }
            if (isScanSelf && bool) {
                return;
            }
            if (isScanInterfaces) {
                final Class<?>[] interfaces = clazz.getInterfaces();
                for (final Class<?> item : interfaces) {
                    try {
                        if (factory.containsBean(item.getName())) {
                            factory.getBean(item);
                            return;
                        }
                    } catch (Exception e) {
                        // ignore exception
                    }
                }
            }
            final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(clazz);
            SpringUtil.getBean(DefaultListableBeanFactory.class).registerBeanDefinition(clazz.getName(), beanDefinition);
        }
    }

}
