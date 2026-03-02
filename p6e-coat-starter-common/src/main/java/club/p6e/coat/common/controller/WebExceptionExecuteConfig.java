package club.p6e.coat.common.controller;

import club.p6e.coat.common.exception.CustomException;

/**
 * Web Exception Execute Config
 *
 * @author lidashuang
 * @version 1.0
 */
public final class WebExceptionExecuteConfig {

    /**
     * Init
     */
    public static void init() {
        boolean run = false;
        try {
            Class.forName("org.springframework.web.servlet.package-info");
            run = true;
            CustomException.registerTransformer(org.springframework.web.servlet.resource.NoResourceFoundException.class, new ExceptionExtend11());
            CustomException.registerTransformer(org.springframework.web.server.MethodNotAllowedException.class, new ExceptionExtend21());
        } catch (Exception e) {
            // ignore exception
        }
        if (!run) {
            try {
                Class.forName("org.springframework.web.reactive.package-info");
                CustomException.registerTransformer(org.springframework.web.reactive.resource.NoResourceFoundException.class, new ExceptionExtend12());
            } catch (Exception e) {
                // ignore exception
            }
        }
    }

    /**
     * Exception Extend Transformer 11
     */
    public static class ExceptionExtend11 extends CustomException {
        public static final int DEFAULT_CODE = 404;
        private static final String DEFAULT_SKETCH = "NOT_FOUND";

        public ExceptionExtend11() {
            super(org.springframework.web.servlet.resource.NoResourceFoundException.class,
                    ExceptionExtend11.class, "NOT_FOUND", DEFAULT_CODE, DEFAULT_SKETCH, "NOT_FOUND");
        }
    }

    /**
     * Exception Extend Transformer 12
     */
    public static class ExceptionExtend12 extends CustomException {
        public static final int DEFAULT_CODE = 404;
        private static final String DEFAULT_SKETCH = "NOT_FOUND";

        public ExceptionExtend12() {
            super(org.springframework.web.reactive.resource.NoResourceFoundException.class,
                    ExceptionExtend12.class, "NOT_FOUND", DEFAULT_CODE, DEFAULT_SKETCH, "NOT_FOUND");
        }
    }

    /**
     * Exception Extend Transformer 2
     */
    public static class ExceptionExtend21 extends CustomException {
        public static final int DEFAULT_CODE = 405;
        private static final String DEFAULT_SKETCH = "METHOD_NOT_ALLOWED";

        public ExceptionExtend21() {
            super(org.springframework.web.server.MethodNotAllowedException.class,
                    ExceptionExtend21.class, "METHOD_NOT_ALLOWED", DEFAULT_CODE, DEFAULT_SKETCH, "METHOD_NOT_ALLOWED");
        }
    }

}
