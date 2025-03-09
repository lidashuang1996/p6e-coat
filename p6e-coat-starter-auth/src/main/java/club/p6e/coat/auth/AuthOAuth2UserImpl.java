package club.p6e.coat.auth;

import club.p6e.coat.common.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;

/**
 * AUTH OAuth2 用户的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthOAuth2UserImpl implements AuthOAuth2Client<AuthOAuth2UserImpl.Model> {

    @Override
    public Model create(String content) {
        final AuthOAuth2UserImpl.Model model = JsonUtil.fromJson(content, AuthOAuth2UserImpl.Model.class);
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
        private Integer status;
        private Integer enabled;
        private Integer internal;
        private Integer administrator;
        private String account;
        private String phone;
        private String mailbox;
        private String name;
        private String nickname;
        private String avatar;
        private String description;
        private String language;

        @Override
        public String id() {
            return String.valueOf(id);
        }

        @Override
        public String password() {
            return null;
        }

        @Override
        public String serialize() {
            return JsonUtil.toJson(new HashMap<>() {{
                put("id", id);
                put("status", status);
                put("enabled", enabled);
                put("internal", internal);
                put("administrator", administrator);
                put("phone", phone);
                put("account", account);
                put("mailbox", mailbox);
                put("name", name);
                put("avatar", avatar);
                put("nickname", nickname);
                put("description", description);
                put("language", language);
            }});
        }

    }
}
