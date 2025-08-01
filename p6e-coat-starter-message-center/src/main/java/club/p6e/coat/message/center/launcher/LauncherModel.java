package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageCenterType;

import java.io.Serializable;
import java.util.List;

/**
 * Launcher Model
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherModel extends Serializable {

    /**
     * ID
     *
     * @return ID
     */
    Integer id();

    /**
     * Enable
     *
     * @return Enable
     */
    boolean enable();

    /**
     * Type
     *
     * @return Type
     */
    MessageCenterType type();

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Template
     *
     * @return Template
     */
    String template();

    /**
     * Description
     *
     * @return Description
     */
    String description();

    /**
     * Route
     *
     * @return Route
     */
    String route();

    /**
     * Route Source
     *
     * @return Route Source
     */
    byte[] routeSource();

    /**
     * Parser
     *
     * @return Parser
     */
    String parser();

    /**
     * Parser Source
     *
     * @return ParserSource
     */
    byte[] parserSource();

    /**
     * Config List
     *
     * @return Config List
     */
    List<ConfigMapperModel> configs();

    /**
     * Config Mapper Model
     */
    interface ConfigMapperModel {

        /**
         * ID
         *
         * @return ID
         */
        Integer id();

        /**
         * Attribute
         *
         * @return Attribute
         */
        String attribute();

    }

}
