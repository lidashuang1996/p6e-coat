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
import reactor.core.publisher.Mono;

/**
 * Blocking Web Exception Config
 *
 * @author lidashuang
 * @version 1.0
 */
@ControllerAdvice
@Component("club.p6e.coat.common.controller.ReactiveWebExceptionConfig")
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class ReactiveWebExceptionConfig {

    /**
     * Inject Log Object
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ReactiveWebExceptionConfig.class);

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public ReactiveWebExceptionConfig(Properties properties) {
        this.properties = properties;
    }

    @SuppressWarnings("ALL")
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Mono<Object> def(Exception exception) {
        if (CustomException.transformation(exception) instanceof final CustomException ce) {
            if (properties.isDebug()) {
                LOGGER.info(ce.getMessage());
            }
            return Mono.just(ResultContext.build(ce.getCode(), ce.getSketch(), ce.getContent()));
        } else {
            LOGGER.error("[{}] >>> {}", exception.getClass(), exception.getMessage());
            if (properties.isDebug()) {
                exception.printStackTrace();
                return Mono.just(ResultContext.build(500, "SERVICE_EXCEPTION", exception.getMessage()));
            } else {
                return Mono.just(ResultContext.build(500, "SERVICE_EXCEPTION", null));
            }
        }
    }

}
