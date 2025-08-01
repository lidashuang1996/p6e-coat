package club.p6e.coat.message.center.template;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date Time Template Variable Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class DateTimeTemplateVariableParserService implements TemplateVariableParserService {

    /**
     * ORDER
     */
    private static final int ORDER = 1000;

    /**
     * MARK PREFIX
     * #NOW_yyyy%2DMM%2Ddd%20HH%3Amm%3Ass -> yyyy-MM-dd HH:mm:ss -> 2020-01-01 00:00:00
     */
    private static final String MARK_PREFIX = "#NOW_";

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public String execute(String key, String language) {
        if (key.startsWith(MARK_PREFIX)) {
            try {
                final String nk = URLDecoder.decode(key.substring(MARK_PREFIX.length()), StandardCharsets.UTF_8);
                return DateTimeFormatter.ofPattern(nk).format(LocalDateTime.now());
            } catch (Exception e) {
                // ignore exception
            }
        }
        return null;
    }

}
