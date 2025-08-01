package club.p6e.coat.message.center.template;

import org.springframework.core.Ordered;

/**
 * Template Variable Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateVariableParserService extends Ordered {

    /**
     * Execute Template Variable Parser
     *
     * @param key      Key
     * @param language Language
     * @return Result Value
     */
    String execute(String key, String language);

}
