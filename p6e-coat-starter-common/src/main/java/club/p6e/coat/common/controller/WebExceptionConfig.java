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
 * Web Exception Config
 *
 * @author lidashuang
 * @version 1.0
 */
@ControllerAdvice
@Component("club.p6e.coat.common.controller.WebExceptionConfig")
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class WebExceptionConfig {

    /**
     * Inject Log Object
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(WebExceptionConfig.class);

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public WebExceptionConfig(Properties properties) {
        this.properties = properties;
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object def(Exception exception) {
        LOGGER.error(exception.getMessage());
        if (exception instanceof final CustomException ce) {
            return ResultContext.build(ce.getCode(), ce.getSketch(), ce.getContent());
        }
        return ResultContext.build(500, "SERVICE_EXCEPTION", properties.isDebug() ? exception.getMessage() : null);
    }

}
