package club.p6e.coat.auth.web.reactive;

import club.p6e.coat.auth.PasswordEncryptorImpl;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.reactive.cache.memory.*;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import club.p6e.coat.auth.web.reactive.cache.redis.*;
import club.p6e.coat.auth.web.reactive.controller.*;
import club.p6e.coat.auth.web.reactive.repository.UserAuthRepositoryImpl;
import club.p6e.coat.auth.web.reactive.repository.UserRepositoryImpl;
import club.p6e.coat.auth.web.reactive.service.*;
import club.p6e.coat.auth.web.reactive.token.*;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @author lidashuang
 * @version 1.0
 */
public class ServerConfig {

    private static final Properties PROPERTIES = Properties.getInstance();

    public Properties getProperties() {
        return PROPERTIES;
    }

    public static void init() {
        if (PROPERTIES.isEnable()) {
            register(UserRepositoryImpl.class);
            register(UserAuthRepositoryImpl.class);

            if (PROPERTIES.getCache().getType() == Properties.Cache.Type.MEMORY) {
                register(ReactiveMemoryTemplate.class);
            }

            if (PROPERTIES.getLogin() != null) {
                initLogin();
            }

            if (PROPERTIES.getRegister() != null) {
                initRegister();
            }

            if (PROPERTIES.getForgotPassword() != null) {
                initForgotPassword();
            }

            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(VoucherRedisCache.class);
                case MEMORY -> register(VoucherMemoryCache.class);
            }
//            register(GlobalAspect.class);
//            register(LoginResultAspect.class);
//            register(ServerHttpRequestAspect.class);

            final DefaultListableBeanFactory factory = (DefaultListableBeanFactory)
                    SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
            factory.getBeanNamesIterator().forEachRemaining(name -> {
                if (name.startsWith("club.p6e.coat.auth")) {
                    System.out.println(name);
                }
            });
        }
    }

    public static void init(String salt) {
        if (PROPERTIES.isEnable()) {
            final DefaultListableBeanFactory factory = (DefaultListableBeanFactory)
                    SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
            factory.registerSingleton(PasswordEncryptorImpl.class.getName(), new PasswordEncryptorImpl(salt));
            init();
        }
    }

    public static void initLogin() {
        if (PROPERTIES.getLogin().isEnable()) {
            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(UserTokenRedisCache.class);
                case MEMORY -> register(UserTokenMemoryCache.class);
            }
            register(LocalStorageCacheTokenGenerator.class);
            register(LocalStorageCacheTokenValidator.class);

            register(CookieCacheTokenGenerator.class);
            register(CookieCacheTokenValidator.class);

            register(CookieJsonWebTokenGenerator.class);
            register(CookieJsonWebTokenValidator.class);

            register(AuthenticationLoginServiceImpl.class);
            register(AuthenticationLoginController.class);
            initAccountPasswordLogin();
            initQuickResponseCodeLogin();
            initVerificationCodeLogin();
        }
    }

    private static void initAccountPasswordLogin() {
        final Properties.Login.AccountPassword config = PROPERTIES.getLogin().getAccountPassword();
        if (config.isEnable()) {
            register(AccountPasswordLoginServiceImpl.class);
//            register(AccountPasswordLoginController.class);


            if (config.isEnableTransmissionEncryption()) {
                switch (PROPERTIES.getCache().getType()) {
                    case REDIS -> register(PasswordSignatureRedisCache.class);
                    case MEMORY -> register(PasswordSignatureMemoryCache.class);
                }
                register(PasswordSignatureServiceImpl.class);
                register(PasswordSignatureController.class);
            }
        }
    }

    private static void initQuickResponseCodeLogin() {
        final Properties.Login.QuickResponseCode config = PROPERTIES.getLogin().getQuickResponseCode();
        if (config.isEnable()) {
            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(QuickResponseCodeLoginRedisCache.class);
                case MEMORY -> register(QuickResponseCodeLoginMemoryCache.class);
            }
            register(QuickResponseCodeLoginAcquisitionServiceImpl.class);
            register(QuickResponseCodeLoginCallbackServiceImpl.class);
            register(QuickResponseCodeLoginServiceImpl.class);
            register(QuickResponseCodeLoginController.class);
        }
    }

    private static void initVerificationCodeLogin() {
        final Properties.Login.VerificationCode config = PROPERTIES.getLogin().getVerificationCode();
        if (config.isEnable()) {
            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(VerificationCodeLoginRedisCache.class);
                case MEMORY -> register(VerificationCodeLoginMemoryCache.class);
            }
            register(VerificationCodeLoginServiceImpl.class);
            register(VerificationCodeLoginAcquisitionServiceImpl.class);
            register(VerificationCodeLoginController.class);
        }
    }

    private static void initRegister() {
        final Properties.Register config = PROPERTIES.getRegister();
        if (config.isEnable()) {
            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(UserTokenRedisCache.class);
                case MEMORY -> register(UserTokenMemoryCache.class);
            }
            register(RegisterService.class);
            register(RegisterController.class);
        }
    }

    private static void initForgotPassword() {
        final Properties.ForgotPassword config = PROPERTIES.getForgotPassword();
        if (config.isEnable()) {
            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(VerificationCodeForgotPasswordRedisCache.class);
                case MEMORY -> register(VerificationCodeForgotPasswordMemoryCache.class);
            }
            register(ForgotPasswordServiceImpl.class);
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
        final DefaultListableBeanFactory factory = (DefaultListableBeanFactory)
                SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();
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
        factory.registerBeanDefinition(clazz.getName(), beanDefinition);
    }

}
