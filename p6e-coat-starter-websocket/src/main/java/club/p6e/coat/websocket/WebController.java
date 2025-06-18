package club.p6e.coat.websocket;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * Web Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class WebController extends Controller {

    /**
     * Web Socket Application Object
     */
    private final Application application;

    /**
     * Constructor Initialization
     *
     * @param application Web Socket Application Object
     */
    public WebController(Application application) {
        this.application = application;
    }

    @PostMapping("/push")
    public ResultContext push(@RequestBody PushParam param) {
        return pushText(param);
    }

    @PostMapping("/push/text")
    public ResultContext pushText(@RequestBody PushParam param) {
        if (param == null
                || param.getUsers() == null
                || param.getContent() == null
                || param.getUsers().isEmpty()) {
            throw new ParameterException(
                    this.getClass(),
                    "fun pushText(PushParam param).",
                    "request parameter exception."
            );
        }
        final String id = DATE_TIME_FORMATTER.format(LocalDateTime.now()) + GeneratorUtil.uuid();
        final String name = param.getName() == null ? "DEFAULT" : param.getName();
        pushTextMessage(param, name, id);
        return ResultContext.build(id);
    }

    @PostMapping("/push/hex")
    public ResultContext pushHex(@RequestBody PushParam param) {
        if (param == null
                || param.getUsers() == null
                || param.getContent() == null
                || param.getUsers().isEmpty()) {
            throw new ParameterException(
                    this.getClass(),
                    "fun pushHex(PushParam param).",
                    "request parameter exception."
            );
        }
        final String id = DATE_TIME_FORMATTER.format(LocalDateTime.now()) + GeneratorUtil.uuid();
        final String name = param.getName() == null ? "DEFAULT" : param.getName();
        pushHexMessage(param, name, id);
        return ResultContext.build(id);
    }

    @SuppressWarnings("ALL")
    private void pushHexMessage(PushParam param, String name, String id) {
        final String content = param.getContent();
        application.push(user -> true, name, HexFormat.of().parseHex(content));
    }

    @SuppressWarnings("ALL")
    private void pushTextMessage(PushParam param, String name, String id) {
        final String content = param.getContent();
        application.push(user -> true, name, content);
    }

}
