package club.p6e.coat.sse;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.exception.ParameterException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HexFormat;

/**
 * Web Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class BlockingWebController {

    /**
     * Web Socket Application Object
     */
    private final Application application;

    /**
     * Constructor Initialization
     *
     * @param application Web Socket Application Object
     */
    public BlockingWebController(Application application) {
        this.application = application;
    }

    @PostMapping("/push")
    public ResultContext push(@RequestBody MessageContext.Request request) {
        return pushText(request);
    }

    @PostMapping("/push/text")
    public ResultContext pushText(@RequestBody MessageContext.Request request) {
        if (request == null || request.getContent() == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun ResultContext pushText(MessageContext.Request request)",
                    "request parameter exception"
            );
        }
        pushTextMessage(request.getName() == null ? "DEFAULT" : request.getName(), request);
        return ResultContext.build("SUCCESS");
    }

    @PostMapping("/push/hex")
    public ResultContext pushHex(@RequestBody MessageContext.Request request) {
        if (request == null || request.getContent() == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun ResultContext pushHex(MessageContext.Request request)",
                    "request parameter exception"
            );
        }
        pushHexMessage(request.getName() == null ? "DEFAULT" : request.getName(), request);
        return ResultContext.build("SUCCESS");
    }

    /**
     * Controller Push Hex Message
     *
     * @param name    Channel Name
     * @param request Message Context Request Object
     */
    protected void pushHexMessage(String name, MessageContext.Request request) {
        this.application.push(_ -> true, name, HexFormat.of().parseHex(request.getContent()));
    }

    /**
     * Controller Push Text Message
     *
     * @param name    Channel Name
     * @param request Message Context Request Object
     */
    protected void pushTextMessage(String name, MessageContext.Request request) {
        this.application.push(_ -> true, name, request.getContent());
    }

}
