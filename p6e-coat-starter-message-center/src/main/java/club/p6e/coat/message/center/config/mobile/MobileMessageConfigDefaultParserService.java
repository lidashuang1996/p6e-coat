package club.p6e.coat.message.center.config.mobile;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigModel;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mobile Message Config Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MobileMessageConfigDefaultParserService implements MobileMessageConfigParserService {

    /**
     * Parser Name
     */
    private static final String PARSER_NAME = "MOBILE_MESSAGE_CONFIG_DEFAULT_PARSER";

    @Override
    public String name() {
        return PARSER_NAME;
    }

    @Override
    public MobileMessageConfigModel execute(ConfigModel config) {
        final SimpleMobileMessageConfigModel model = new SimpleMobileMessageConfigModel(config);
        if (config.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(config.content(), String.class, Object.class);
            if (data != null) {
                model.setApplicationId(TransformationUtil.objectToString(data.get("applicationId")));
                model.setApplicationKey(TransformationUtil.objectToString(data.get("applicationKey")));
                model.setApplicationName(TransformationUtil.objectToString(data.get("applicationName")));
                model.setApplicationSecret(TransformationUtil.objectToString(data.get("applicationSecret")));
                final Map<String, String> other = new HashMap<>();
                for (final String key : data.keySet()) {
                    other.put(key, TransformationUtil.objectToString(data.get(key)));
                }
                model.setOther(other);
            }
        }
        return model;
    }

    /**
     * Simple Mobile Message Config Model
     */
    public static class SimpleMobileMessageConfigModel implements MobileMessageConfigModel, Serializable {

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Application ID
         */
        private String applicationId;

        /**
         * Application Key
         */
        private String applicationKey;

        /**
         * Application Name
         */
        private String applicationName;

        /**
         * Application Secret
         */
        private String applicationSecret;

        /**
         * Other Data
         */
        private Map<String, String> other = Collections.unmodifiableMap(new HashMap<>());

        /**
         * Construct Initialization
         * Inject Source Config Model Object
         *
         * @param source Source Config Model
         */
        public SimpleMobileMessageConfigModel(ConfigModel source) {
            this.source = source;
        }

        @Override
        public int id() {
            return this.source == null ? 0 : this.source.id();
        }

        @Override
        public String rule() {
            return this.source == null ? null : this.source.rule();
        }

        @Override
        public MessageCenterType type() {
            return this.source == null ? null : this.source.type();
        }

        @Override
        public boolean enable() {
            return this.source != null && this.source.enable();
        }

        @Override
        public String name() {
            return this.source == null ? null : this.source.name();
        }

        @Override
        public String content() {
            return this.source == null ? null : this.source.content();
        }

        @Override
        public String description() {
            return this.source == null ? null : this.source.description();
        }

        @Override
        public String parser() {
            return this.source == null ? null : this.source.parser();
        }

        @Override
        public byte[] parserSource() {
            return this.source == null ? null : this.source.parserSource();
        }

        @Override
        public String getApplicationId() {
            return this.applicationId;
        }

        @Override
        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        @Override
        public String getApplicationKey() {
            return this.applicationKey;
        }

        @Override
        public void setApplicationKey(String applicationKey) {
            this.applicationKey = applicationKey;
        }

        @Override
        public String getApplicationName() {
            return this.applicationName;
        }

        @Override
        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        @Override
        public String getApplicationSecret() {
            return this.applicationSecret;
        }

        @Override
        public void setApplicationSecret(String applicationSecret) {
            this.applicationSecret = applicationSecret;
        }

        @Override
        public Map<String, String> getOther() {
            return other;
        }

        @Override
        public void setOther(Map<String, String> other) {
            if (other != null) {
                this.other = Collections.unmodifiableMap(other);
            }
        }

    }

}
