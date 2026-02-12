package club.p6e.coat.common.controller;

import club.p6e.coat.common.exception.CustomException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;

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
        CustomException.registerTransformer(NoResourceFoundException.class, new ExceptionExtend1());
        CustomException.registerTransformer(MethodNotAllowedException.class, new ExceptionExtend2());
    }

    /**
     * Exception Extend Transformer 1
     */
    public static class ExceptionExtend1 extends CustomException {
        public static final int DEFAULT_CODE = 404;
        private static final String DEFAULT_SKETCH = "NOT_FOUND";

        public ExceptionExtend1() {
            super(NoResourceFoundException.class, ExceptionExtend1.class,
                    "NOT_FOUND", DEFAULT_CODE, DEFAULT_SKETCH, "NOT_FOUND");
        }
    }

    /**
     * Exception Extend Transformer 2
     */
    public static class ExceptionExtend2 extends CustomException {
        public static final int DEFAULT_CODE = 405;
        private static final String DEFAULT_SKETCH = "METHOD_NOT_ALLOWED";

        public ExceptionExtend2() {
            super(NoResourceFoundException.class, ExceptionExtend2.class,
                    "METHOD_NOT_ALLOWED", DEFAULT_CODE, DEFAULT_SKETCH, "METHOD_NOT_ALLOWED");
        }
    }

}
