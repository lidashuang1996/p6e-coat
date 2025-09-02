package club.p6e.coat.auth.error;

import club.p6e.coat.common.error.CustomException;

/**
 * Custom Exception
 * Voucher Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class VoucherException extends CustomException {

    /**
     * Default Code
     */
    public static final int DEFAULT_CODE = 13000;

    /**
     * Default Sketch
     */
    private static final String DEFAULT_SKETCH = "AUTH_EXCEPTION";

    /**
     * Constructor Initialization
     *
     * @param sc      Class Object
     * @param error   Exception Data
     * @param content Exception Content
     */
    public VoucherException(Class<?> sc, String error, String content) {
        super(sc, VoucherException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

    /**
     * Constructor Initialization
     *
     * @param sc        Class Object
     * @param throwable Exception Object
     * @param content   Exception Content
     */
    public VoucherException(Class<?> sc, Throwable throwable, String content) {
        super(sc, VoucherException.class, throwable, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

    /**
     * Constructor Initialization
     *
     * @param sc      Class Object
     * @param error   Exception Data
     * @param code    Exception Code
     * @param sketch  Exception Sketch
     * @param content Exception Content
     */
    public VoucherException(Class<?> sc, String error, int code, String sketch, String content) {
        super(sc, VoucherException.class, error, code, sketch, content);
    }

    /**
     * Constructor Initialization
     *
     * @param sc        Class Object
     * @param throwable Exception Object
     * @param code      Exception Code
     * @param sketch    Exception Sketch
     * @param content   Exception Content
     */
    public VoucherException(Class<?> sc, Throwable throwable, int code, String sketch, String content) {
        super(sc, VoucherException.class, throwable, code, sketch, content);
    }

}
