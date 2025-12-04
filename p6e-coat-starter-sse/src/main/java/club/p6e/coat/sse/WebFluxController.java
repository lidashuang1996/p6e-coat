package club.p6e.coat.sse;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HexFormat;

/**
 * Web Flux Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class WebFluxController extends Controller {

    /**
     * Web Socket Application Object
     */
    private final Application application;

    /**
     * Constructor Initialization
     *
     * @param application Web Socket Application Object
     */
    public WebFluxController(Application application) {
        this.application = application;
    }

    @PostMapping("/push")
    public Mono<ResultContext> push(@RequestBody PushParam param) {
        return pushText(param);
    }

    @PostMapping("/push/text")
    public Mono<ResultContext> pushText(@RequestBody PushParam param) {
        if (param == null || param.getContent() == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun push(PushParam param)",
                    "request parameter exception"
            );
        }
        pushTextMessage(param.getName() == null ? "DEFAULT" : param.getName(), param);
        return Mono.just(ResultContext.build("SUCCESS"));
    }

    @PostMapping("/push/hex")
    public Mono<ResultContext> pushHex(@RequestBody PushParam param) {
        if (param == null || param.getContent() == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun push(PushParam param)",
                    "request parameter exception"
            );
        }
        pushHexMessage(param.getName() == null ? "DEFAULT" : param.getName(), param);
        return Mono.just(ResultContext.build("SUCCESS"));
    }

    /**
     * Controller Push Hex Message
     *
     * @param name  Channel Name
     * @param param Push Param Object
     */
    protected void pushHexMessage(String name, PushParam param) {
        this.application.push(user -> true, name, HexFormat.of().parseHex(param.getContent()));
    }

    /**
     * Controller Push Text Message
     *
     * @param name  Channel Name
     * @param param Push Param Object
     */
    protected void pushTextMessage(String name, PushParam param) {
        this.application.push(user -> true, name, param.getContent());
    }

}
