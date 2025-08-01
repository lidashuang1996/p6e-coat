package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * Launcher Config Type Mismatch Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherConfigTypeMismatchException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 202000;

    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_CONFIG_TYPE_MISMATCH_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherConfigTypeMismatchException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
