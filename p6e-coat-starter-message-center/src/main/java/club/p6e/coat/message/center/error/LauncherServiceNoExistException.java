package club.p6e.coat.message.center.error;

import club.p6e.coat.common.exception.CustomException;
import club.p6e.coat.common.exception.ResourceException;

/**
 * Launcher Service No Exist Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherServiceNoExistException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 207000;

    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_SERVICE_NO_EXIST_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherServiceNoExistException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
