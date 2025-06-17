package club.p6e.coat.websocket.controller;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.NumberUtil;
import club.p6e.coat.websocket.Application;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

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
     * WebSocket Main 对象
     */
    private final Application webSocketMain;

    /**
     * 构造方法初始化
     *
     * @param webSocketMain WebSocket Main 对象
     */
    public WebController(Application webSocketMain) {
        this.webSocketMain = webSocketMain;
    }

    @PostMapping("/push")
    public ResultContext push(@RequestBody PushParam param) {
        return pushText(param);
    }

    @PostMapping("/push/text")
    public ResultContext pushText(@RequestBody PushParam param) {
        if (param == null
                || param.getType() == null
                || param.getContent() == null
                || param.getUsers() == null
                || param.getUsers().isEmpty()) {
            throw new ParameterException(
                    this.getClass(),
                    "fun pushText(PushParam param).",
                    "request parameter exception, please check your network request."
            );
        }
        final String id = DATE_TIME_FORMATTER.format(LocalDateTime.now()) + GeneratorUtil.uuid();
        final String name = param.getName() == null ? "DEFAULT" : param.getName();
        final String type = param.getType();
        final String content = param.getContent();
        final List<String> users = param.getUsers();
        webSocketMain.push(user -> users.contains(user.id()), name, id, type, content);
        return ResultContext.build(id);
    }

    @PostMapping("/push/hex")
    public ResultContext pushHex(@RequestBody PushParam param) {
        if (param == null
                || param.getContent() == null
                || param.getUsers() == null
                || param.getUsers().isEmpty()) {
            throw new ParameterException(
                    this.getClass(),
                    "fun pushHex(PushParam param).",
                    "request parameter exception, please check your network request."
            );
        }
        final String id = DATE_TIME_FORMATTER.format(LocalDateTime.now()) + GeneratorUtil.uuid();
        final String name = param.getName() == null ? "DEFAULT" : param.getName();
        final String content = param.getContent();
        final List<String> users = param.getUsers();
        webSocketMain.push(user -> users.contains(user.id()), name, NumberUtil.hexToBytes(content));
        return ResultContext.build(id);
    }

}
