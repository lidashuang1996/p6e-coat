package club.p6e.coat.common.controller;

import club.p6e.coat.common.exception.CustomException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

/**
 * Web Execute Exception Config
 *
 * @author lidashuang
 * @version 1.0
 */
public final class WebExecuteExceptionConfig {

    /**
     * Init
     */
    public static void init() {
        CustomException.registerTransformer(NoResourceFoundException.class, new ExtendException1());
    }

    /**
     * Extend Exception Transformer
     */
    public static class ExtendException1 extends CustomException {
        public static final int DEFAULT_CODE = 404;
        private static final String DEFAULT_SKETCH = "NOT_FOUND";

        public ExtendException1() {
            super(NoResourceFoundException.class, ExtendException1.class,
                    "NOT_FOUND", DEFAULT_CODE, DEFAULT_SKETCH, "NOT_FOUND");
        }
    }

}
