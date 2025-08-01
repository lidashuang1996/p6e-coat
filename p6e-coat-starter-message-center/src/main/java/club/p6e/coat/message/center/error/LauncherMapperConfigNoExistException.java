package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * Launcher Mapper Config No Exist Exception
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherMapperConfigNoExistException extends CustomException {

    /**
     * DEFAULT CODE
     */
    public static final int DEFAULT_CODE = 203000;
    
    /**
     * DEFAULT SKETCH
     */
    private static final String DEFAULT_SKETCH = "LAUNCHER_MAPPER_CONFIG_NO_EXIST_EXCEPTION";

    /**
     * Construct Initialization
     */
    public LauncherMapperConfigNoExistException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
