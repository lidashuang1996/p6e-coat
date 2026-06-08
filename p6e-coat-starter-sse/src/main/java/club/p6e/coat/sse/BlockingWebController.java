package club.p6e.coat.sse;

import club.p6e.coat.common.context.ResultContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web Controller (Blocking / Spring MVC)
 *
 * [P2] 重构: 继承 BaseWebController 消除代码重复
 * [P1] 安全加固: 异常信息不再泄漏类名和方法签名
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class BlockingWebController extends BaseWebController {

    public BlockingWebController(Application application) {
        super(application);
    }

    @PostMapping("/push")
    public ResultContext push(@RequestBody MessageContext.Request request) {
        return pushText(request);
    }

    @PostMapping("/push/text")
    public ResultContext pushText(@RequestBody MessageContext.Request request) {
        validateRequest(request);
        pushTextMessage(resolveName(request), request);
        return buildResult();
    }

    @PostMapping("/push/hex")
    public ResultContext pushHex(@RequestBody MessageContext.Request request) {
        validateRequest(request);
        pushHexMessage(resolveName(request), request);
        return buildResult();
    }

}