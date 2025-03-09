package club.p6e.coat.auth.launcher;

import club.p6e.coat.common.utils.GeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 短信消息推送器的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class SmsMessageLauncherImpl implements SmsMessageLauncher {

    /**
     * 注入日志对象
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SmsMessageLauncherImpl.class);

    @SuppressWarnings("ALL")
    @Override
    public Mono<List<String>> execute(List<String> recipients, String template, Map<String, String> content, String language) {
        final List<String> result = new ArrayList<>();
        for (final String recipient : recipients) {
            result.add(GeneratorUtil.uuid());
            LOGGER.info("\r\n" +
                    "\n" +
                    "---------------------------------------------------------------------\n" +
                    "|--------------------------   S   M   S   --------------------------|\n" +
                    "---------------------------------------------------------------------\n" +
                    "|   account    =>   " + recipient + "   |\n" +
                    "|   template   =>   " + template + "    |\n" +
                    "|   content    =>   " + content + "     |\n" +
                    "|   language   =>   " + language + "    |\n" +
                    "--------------------------------------------------------------------\n" +
                    "\n");
        }
        return Mono.just(result);
    }
}
