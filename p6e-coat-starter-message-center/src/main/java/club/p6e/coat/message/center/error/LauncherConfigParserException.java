package club.p6e.coat.message.center.error;

import club.p6e.coat.common.exception.CustomException;
import club.p6e.coat.common.exception.ResourceException;

/**
 * Launcher Config Parser Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherConfigParserException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 201000;

    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_CONFIG_PARSER_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherConfigParserException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
