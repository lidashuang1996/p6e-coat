package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * Message Center File Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherParamFileException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 212000;

    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_PARAM_FILE_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherParamFileException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
