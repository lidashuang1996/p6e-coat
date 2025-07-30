package club.p6e.coat.message.center.config.wechat;

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
 * We Chat Message Config Default Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WeChatMessageConfigDefaultParserService implements WeChatMessageConfigParserService {

    /**
     * Parser Name
     */
    private static final String PARSER_NAME = "WECHAT_MESSAGE_CONFIG_DEFAULT_PARSER";

    @Override
    public String name() {
        return PARSER_NAME;
    }

    @Override
    public WeChatMessageConfigModel execute(ConfigModel cm) {
        final SimpleWeChatMessageConfigModel model = new SimpleWeChatMessageConfigModel(cm);
        if (cm.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(cm.content(), String.class, Object.class);
            if (data != null) {
                model.setApplicationSecret(TransformationUtil.objectToString(data.get("applicationSecret")));
                model.setAccessTokenUrl(TransformationUtil.objectToString(data.get("accessTokenUrl")));
                model.setApplicationId(TransformationUtil.objectToString(data.get("applicationId")));
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
     * Simple We Chat Message Config Model
     */
    public static class SimpleWeChatMessageConfigModel implements WeChatMessageConfigModel, Serializable {

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Other Data
         */
        public Map<String, String> other = Collections.unmodifiableMap(new HashMap<>());

        /**
         * Application ID
         */
        private String applicationId;

        /**
         * Access Token Url
         */
        private String accessTokenUrl;

        /**
         * Application Secret
         */
        private String applicationSecret;

        /**
         * Construct Initialization
         * Inject Source Config Model Object
         *
         * @param source Source Config Model
         */
        public SimpleWeChatMessageConfigModel(ConfigModel source) {
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
        public String getApplicationSecret() {
            return this.applicationSecret;
        }

        @Override
        public void setApplicationSecret(String applicationSecret) {
            this.applicationSecret = applicationSecret;
        }

        @Override
        public String getAccessTokenUrl() {
            return this.accessTokenUrl;
        }

        @Override
        public void setAccessTokenUrl(String accessTokenUrl) {
            this.accessTokenUrl = accessTokenUrl;
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
