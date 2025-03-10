
package club.p6e.coat.common.controller.config;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 基础的异常全局处理
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ControllerAdvice
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class WebExceptionConfig {

    /**
     * 注入日志对象
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(WebExceptionConfig.class);

    @SuppressWarnings("ALL")
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(Exception exception) {
        LOGGER.error(exception.getMessage());
        if (exception instanceof final CustomException ce) {
            return ResultContext.build(ce.getCode(), ce.getSketch(), ce.getContent());
        }
        exception.printStackTrace();
        return  ResultContext.build(500, "SERVICE_EXCEPTION", exception.getMessage());
    }

}
