package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * Launcher No Exist Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherNoExistException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 205000;

    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_NO_EXIST_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherNoExistException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
