package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Blocking Web Exception Config
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
@Component("club.p6e.coat.common.controller.BlockingWebExceptionConfig")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingWebExceptionConfig {

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
                log.error(ce.getMessage(), ce);
            }
            return ResultContext.build(ce.getCode(), ce.getSketch(), ce.getContent());
        } else {
            log.error("[{}] >>> {}", exception.getClass(), exception.getMessage(), exception);
            if (properties.isDebug()) {
                exception.printStackTrace();
                return ResultContext.build(500, "SERVICE_EXCEPTION", exception.getMessage());
            } else {
                return ResultContext.build(500, "SERVICE_EXCEPTION", null);
            }
        }
    }

}
