package club.p6e.coat.auth.launcher;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.SpringUtil;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 消息推送器
 *
 * @author lidashuang
 * @version 1.0
 */
public final class Launcher {

    /**
     * 推送消息
     *
     * @param type       类型
     * @param recipients 收件人
     * @param template   模版
     * @param content    内容
     * @param language   语言
     * @return 推送的结果回执
     */
    public static Mono<List<String>> push(
            LauncherType type,
            List<String> recipients,
            String template,
            Map<String, String> content,
            String language
    ) {
        if (LauncherType.SMS == type) {
            return SpringUtil.getBean(SmsMessageLauncher.class)
                    .execute(recipients, template, content, language);
        }
        if (LauncherType.EMAIL == type) {
            return SpringUtil.getBean(EmailMessageLauncher.class)
                    .execute(recipients, template, content, language);
        }
        return Mono.error(GlobalExceptionContext.exceptionLauncherTypeException(
                Launcher.class,
                "fun push(LauncherType type, List<String> recipients, " +
                        "String template, Map<String, String> content, String language).",
                "[ " + type + " ] launcher type mismatch."
        ));
    }

}
