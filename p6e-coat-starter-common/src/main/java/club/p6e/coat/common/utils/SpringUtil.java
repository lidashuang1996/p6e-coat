package club.p6e.coat.common.utils;

import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Util
 *
 * @author lidashuang
 * @version 1.0
 */
public final class SpringUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Init Application Context Object
         *
         * @param application Application Context Object
         */
        void init(ApplicationContext application);

        /**
         * Exist Bean Judgment
         *
         * @param tClass Bean Class
         * @return boolean Exist Bean
         */
        boolean exist(Class<?> tClass);

        /**
         * Get Bean Object
         *
         * @param tClass Bean Class
         * @return Bean Object
         */
        <T> T getBean(Class<T> tClass);

        /**
         * Get Bean Object Collection
         *
         * @param tClass Bean Class
         * @return Bean Object Collection
         */
        <T> Map<String, T> getBeans(Class<T> tClass);

        /**
         * Get Application Context Object
         *
         * @return Application Context Object
         */
        ApplicationContext getApplicationContext();

    }

    /**
     * Implementation
     */
    public static class Implementation implements Definition {

        /**
         * Application Context Object
         */
        private ApplicationContext application = null;

        @Override
        public ApplicationContext getApplicationContext() {
            return application;
        }

        @Override
        public void init(ApplicationContext application) {
            this.application = application;
        }

        @Override
        public boolean exist(Class<?> tClass) {
            try {
                this.application.getBean(tClass);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public <T> T getBean(Class<T> tClass) {
            return application == null ? null : application.getBean(tClass);
        }

        @Override
        public <T> Map<String, T> getBeans(Class<T> tClass) {
            return application == null ? new HashMap<>(0) : application.getBeansOfType(tClass);
        }
    }

    /**
     * Default Definition Implementation Object
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * Set Definition Implementation Object
     *
     * @param implementation Definition Implementation Object
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * Init Application Context Object
     *
     * @param application Application Context Object
     */
    public static void init(ApplicationContext application) {
        DEFINITION.init(application);
    }

    /**
     * Exist Bean Judgment
     *
     * @param tClass Bean Class
     * @return boolean Exist Bean
     */
    public static boolean exist(Class<?> tClass) {
        return DEFINITION.exist(tClass);
    }

    /**
     * Get Bean Object
     *
     * @param tClass Bean Class
     * @return Bean Object
     */
    public static <T> T getBean(Class<T> tClass) {
        return DEFINITION.getBean(tClass);
    }

    /**
     * Get Bean Object Collection
     *
     * @param tClass Bean Class
     * @return Bean Object Collection
     */
    public static <T> Map<String, T> getBeans(Class<T> tClass) {
        return DEFINITION.getBeans(tClass);
    }

    /**
     * Get Application Context Object
     *
     * @return Application Context Object
     */
    @SuppressWarnings("ALL")
    public static ApplicationContext getApplicationContext() {
        return DEFINITION.getApplicationContext();
    }

}
