package club.p6e.coat.auth.web.reactive;

import club.p6e.coat.auth.PasswordEncryptorImpl;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.reactive.aspect.ServerHttpRequestAspect;
import club.p6e.coat.auth.web.reactive.cache.memory.*;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import club.p6e.coat.auth.web.reactive.cache.redis.*;
import club.p6e.coat.auth.web.reactive.handler.*;
import club.p6e.coat.auth.web.reactive.repository.UserAuthRepositoryImpl;
import club.p6e.coat.auth.web.reactive.repository.UserRepositoryImpl;
import club.p6e.coat.auth.web.reactive.service.*;
import club.p6e.coat.auth.web.reactive.token.LocalStorageCacheTokenGenerator;
import club.p6e.coat.auth.web.reactive.token.LocalStorageCacheTokenValidator;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.cache.CacheType;

/**
 * @author lidashuang
 * @version 1.0
 */
public class ServerConfig {

    private static final Properties PROPERTIES = Properties.getInstance();

    public Properties getProperties() {
        return PROPERTIES;
    }

    public void init() {
        if (PROPERTIES.isEnable()) {
            register(PasswordEncryptorImpl.class);
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

            register(ServerHttpRequestAspect.class);
        }
    }

    public void initLogin() {
        if (PROPERTIES.getLogin().isEnable()) {
            switch (PROPERTIES.getCache().getType()) {
                case REDIS -> register(UserTokenRedisCache.class);
                case MEMORY -> register(UserTokenMemoryCache.class);
            }
            register(LocalStorageCacheTokenGenerator.class);
            register(LocalStorageCacheTokenValidator.class);
            register(AuthenticationLoginServiceImpl.class);
            register(AuthenticationLoginHandler.class);
            initAccountPasswordLogin();
            initQuickResponseCodeLogin();
            initVerificationCodeLogin();
        }
    }

    private static void initAccountPasswordLogin() {
        final Properties.Login.AccountPassword config = PROPERTIES.getLogin().getAccountPassword();
        if (config.isEnable()) {
            register(AccountPasswordLoginServiceImpl.class);
            register(AccountPasswordLoginHandler.class);
            if (config.isEnableTransmissionEncryption()) {
                switch (PROPERTIES.getCache().getType()) {
                    case REDIS -> register(PasswordSignatureRedisCache.class);
                    case MEMORY -> register(PasswordSignatureMemoryCache.class);
                }
                register(PasswordSignatureServiceImpl.class);
                register(AccountPasswordSignatureHandler.class);
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
            register(QuickResponseCodeLoginServiceImpl.class);
            register(QuickResponseCodeLoginHandler.class);
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
            register(VerificationCodeLoginHandler.class);
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
            register(RegisterHandler.class);
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
            register(ForgotPasswordHandler.class);
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
