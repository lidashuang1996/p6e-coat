package club.p6e.coat.auth;

import club.p6e.coat.auth.model.UserAuthModel;
import club.p6e.coat.auth.model.UserModel;
import club.p6e.coat.common.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证用户
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthUserImpl implements AuthUser<AuthUserImpl.Model> {

    @Override
    public Mono<AuthUserImpl.Model> create(String content) {
        final AuthUserImpl.Model model = JsonUtil.fromJson(content, AuthUserImpl.Model.class);
        if (model == null) {
            return Mono.error(new RuntimeException(
                    "[ " + this.getClass() + " ] " +
                            "fun create(String content) ==> deserialization failure !!"));
        } else {
            return Mono.just(model);
        }
    }

    @Override
    public Mono<AuthUserImpl.Model> create(UserModel um, UserAuthModel uam) {
        final Model model = new Model()
                .setId(um.getId())
                .setStatus(um.getStatus())
                .setEnabled(um.getEnabled())
                .setInternal(um.getInternal())
                .setAdministrator(um.getAdministrator())
                .setAccount(um.getAccount())
                .setPhone(um.getPhone())
                .setMailbox(um.getMailbox())
                .setName(um.getName())
                .setNickname(um.getNickname())
                .setAvatar(um.getAvatar())
                .setDescription(um.getDescription())
                .setLanguage(um.getLanguage());
        if (uam != null) {
            model.setPassword(uam.getPassword());
        }
        return Mono.just(model);
    }

    /**
     * 实现的用户认证模型
     */
    @Data
    @Accessors(chain = true)
    public static class Model implements AuthUser.Model, Serializable {
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

        /**
         * [ PASSWORD ] sensitive data should not be returned to the front-end
         */
        private String password;

        @Override
        public String id() {
            return String.valueOf(id);
        }

        @Override
        public String password() {
            return password;
        }

        @Override
        public String serialize() {
            return JsonUtil.toJson(toMap());
        }

        @Override
        public Map<String, Object> toMap() {
            return new HashMap<>() {{
                put("id", id);
                put("status", status);
                put("enabled", enabled);
                put("internal", internal);
                put("administrator", administrator);
                put("account", account);
                put("phone", phone);
                put("mailbox", mailbox);
                put("name", name);
                put("nickname", nickname);
                put("avatar", avatar);
                put("description", description);
                put("language", language);
            }};
        }
    }
}
