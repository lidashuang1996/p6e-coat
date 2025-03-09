package club.p6e.coat.auth;

import club.p6e.coat.common.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;

/**
 * AUTH OAuth2 客户端的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthOAuth2ClientImpl implements AuthOAuth2Client<AuthOAuth2ClientImpl.Model> {

    @Override
    public Model create(String content) {
        final AuthOAuth2ClientImpl.Model model = JsonUtil.fromJson(content, AuthOAuth2ClientImpl.Model.class);
        if (model == null) {
            throw new RuntimeException("[ " + this.getClass() + " ] " +
                    "fun create(String content) ==> deserialization failure !!");
        } else {
            return model;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Model implements AuthOAuth2Client.Model, Serializable {
        private Integer id;
        private Integer enabled;
        private String name;
        private String avatar;
        private String description;
        private String secret;

        @Override
        public String id() {
            return String.valueOf(id);
        }

        @Override
        public String password() {
            return secret;
        }

        @Override
        public String serialize() {
            return JsonUtil.toJson(new HashMap<>() {{
                put("id", id);
                put("enabled", enabled);
                put("name", name);
                put("avatar", avatar);
                put("description", description);
            }});
        }

    }
}
