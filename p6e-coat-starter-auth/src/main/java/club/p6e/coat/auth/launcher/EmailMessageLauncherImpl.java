package club.p6e.coat.auth.launcher;

import club.p6e.coat.common.utils.GeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 邮件消息推送器的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class EmailMessageLauncherImpl implements EmailMessageLauncher {

    /**
     * 注入日志对象
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailMessageLauncherImpl.class);

    @SuppressWarnings("ALL")
    @Override
    public Mono<List<String>> execute(List<String> recipients, String template, Map<String, String> content, String language) {
        final List<String> result = new ArrayList<>();
        for (final String recipient : recipients) {
            result.add(GeneratorUtil.uuid());
            LOGGER.info("\r\n" +
                    "\n" +
                    "---------------------------------------------------------------------\n" +
                    "|------------------------   E  M  A  I  L   ------------------------|\n" +
                    "---------------------------------------------------------------------\n" +
                    "|   recipient   =>   " + recipient + "   |\n" +
                    "|   template    =>   " + template + "    |\n" +
                    "|   content     =>   " + content + "     |\n" +
                    "|   language    =>   " + language + "    |\n" +
                    "--------------------------------------------------------------------\n" +
                    "\n");
        }
        return Mono.just(result);
    }

}
