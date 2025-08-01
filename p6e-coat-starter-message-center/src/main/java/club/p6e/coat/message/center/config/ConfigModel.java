package club.p6e.coat.message.center.config;

import club.p6e.coat.message.center.MessageCenterType;

import java.io.Serializable;

/**
 * Config Model
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigModel extends Serializable {

    /**
     * ID
     *
     * @return ID
     */
    int id();

    /**
     * Rule
     *
     * @return 限流规则
     */
    String rule();

    /**
     * Type
     *
     * @return Type
     */
    MessageCenterType type();

    /**
     * Enable
     *
     * @return Enable
     */
    boolean enable();

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Content
     *
     * @return Content
     */
    String content();

    /**
     * Description
     *
     * @return Description
     */
    String description();

    /**
     * Parser
     *
     * @return Parser
     */
    String parser();

    /**
     * Parser Source
     *
     * @return Parser Source
     */
    byte[] parserSource();

}
