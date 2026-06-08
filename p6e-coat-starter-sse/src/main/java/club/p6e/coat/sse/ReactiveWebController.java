package club.p6e.coat.sse;

import club.p6e.coat.common.context.ResultContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Web Flux Controller (Reactive / Spring WebFlux)
 *
 * [P2] 重构: 继承 BaseWebController 消除代码重复
 * [P1] 安全加固: 异常信息不再泄漏类名和方法签名
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class ReactiveWebController extends BaseWebController {

    public ReactiveWebController(Application application) {
        super(application);
    }

    @PostMapping("/push")
    public Mono<ResultContext> push(@RequestBody MessageContext.Request request) {
        return pushText(request);
    }

    @PostMapping("/push/text")
    public Mono<ResultContext> pushText(@RequestBody MessageContext.Request request) {
        validateRequest(request);
        pushTextMessage(resolveName(request), request);
        return Mono.just(buildResult());
    }

    @PostMapping("/push/hex")
    public Mono<ResultContext> pushHex(@RequestBody MessageContext.Request request) {
        validateRequest(request);
        pushHexMessage(resolveName(request), request);
        return Mono.just(buildResult());
    }

}