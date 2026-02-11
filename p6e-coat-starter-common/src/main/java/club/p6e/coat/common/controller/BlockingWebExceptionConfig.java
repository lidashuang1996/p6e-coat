package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Blocking Web Exception Config
 *
 * @author lidashuang
 * @version 1.0
 */
@ControllerAdvice
@Component("club.p6e.coat.common.controller.BlockingWebExceptionConfig")
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class BlockingWebExceptionConfig {

    /**
     * Inject Log Object
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BlockingWebExceptionConfig.class);

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public BlockingWebExceptionConfig(Properties properties) {
        this.properties = properties;
    }

    @SuppressWarnings("ALL")
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object def(Exception exception) {
        if (CustomException.transformation(exception) instanceof final CustomException ce) {
            if (properties.isDebug()) {
                LOGGER.info(ce.getMessage());
            }
            return ResultContext.build(ce.getCode(), ce.getSketch(), ce.getContent());
        } else {
            LOGGER.error("[{}] >>> {}", exception.getClass(), exception.getMessage());
            if (properties.isDebug()) {
                exception.printStackTrace();
                return ResultContext.build(500, "SERVICE_EXCEPTION", exception.getMessage());
            } else {
                return ResultContext.build(500, "SERVICE_EXCEPTION", null);
            }
        }
    }

}
