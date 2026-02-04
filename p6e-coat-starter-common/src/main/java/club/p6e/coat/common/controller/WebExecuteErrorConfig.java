package club.p6e.coat.common.controller;

import club.p6e.coat.common.error.CustomException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * 异常捕获处理
 *
 * @author lidashuang
 * @version 1.0
 */
public final class WebExecuteErrorConfig {

    /**
     * 初始化
     */
    public static void init() {
        boolean bool = true;
        try {
            Class.forName("javax.servlet.ServletRequest");
        } catch (ClassNotFoundException e) {
            bool = false;
        }
        if (bool) {
            CustomException.registerTransformer(HttpRequestMethodNotSupportedException.class, new ExtendException1());
        }
    }

    /**
     * 扩展异常类
     */
    public static class ExtendException1 extends CustomException {

        /**
         * 默认的代码
         */
        public static final int DEFAULT_CODE = 405;

        /**
         * 默认的简述
         */
        private static final String DEFAULT_SKETCH = "HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION";

        public ExtendException1() {
            super(HttpRequestMethodNotSupportedException.class, ExtendException1.class,
                    "HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION", DEFAULT_CODE, DEFAULT_SKETCH, "不支持的请求方法");
        }

    }

}
