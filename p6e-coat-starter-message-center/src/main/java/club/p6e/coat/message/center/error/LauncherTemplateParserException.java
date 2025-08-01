package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * Launcher Template Parser Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherTemplateParserException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 209000;

    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_TEMPLATE_PARSER_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherTemplateParserException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
