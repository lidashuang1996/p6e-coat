package club.p6e.coat.auth.launcher;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 消息推送器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MessageLauncher {

    /**
     * 推送器类型
     *
     * @return 类型
     */
    String toType();

    /**
     * 执行推送
     *
     * @param recipients 收件人
     * @param template   模版
     * @param content    内容
     * @param language   语言
     * @return 推送消息的回执
     */
    Mono<List<String>> execute(List<String> recipients, String template, Map<String, String> content, String language);

}
