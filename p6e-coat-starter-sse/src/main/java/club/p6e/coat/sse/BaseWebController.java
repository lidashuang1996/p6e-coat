package club.p6e.coat.sse;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.exception.ParameterException;
import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * [P2] 重构: 抽取共享推送逻辑，消除 BlockingWebController 与 ReactiveWebController 的代码重复
 *
 * @author lidashuang
 * @version 1.0
 */
public abstract class BaseWebController {

    protected final Application application;

    protected BaseWebController(Application application) {
        this.application = application;
    }

    /**
     * [P1] 安全加固: 移除异常信息中的具体类名与方法签名泄漏，
     *     仅保留通用描述信息
     */
    protected void validateRequest(MessageContext.Request request) {
        if (request == null || request.getContent() == null) {
            throw new ParameterException(
                    Object.class,
                    "push",
                    "request parameter exception: content is required"
            );
        }
    }

    protected String resolveName(MessageContext.Request request) {
        return request.getName() == null ? "DEFAULT" : request.getName();
    }

    protected ResultContext buildResult() {
        return ResultContext.build(LocalDateTime.now());
    }

    protected void pushHexMessage(String name, MessageContext.Request request) {
        this.application.push(_ -> true, name, HexFormat.of().parseHex(request.getContent()));
    }

    protected void pushTextMessage(String name, MessageContext.Request request) {
        this.application.push(_ -> true, name, request.getContent());
    }

}