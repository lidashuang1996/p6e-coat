package club.p6e.coat.auth.user;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple User Model
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SimpleUserModel implements User, Serializable {

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
    private String language;
    private String avatar;
    private String description;
    private String password;

    public SimpleUserModel(String content) {
        this(JsonUtil.fromJsonToMap(content, String.class, Object.class));
    }

    public SimpleUserModel(Map<String, Object> content) {
        this.id = TransformationUtil.objectToInteger(content.get("id"));
        this.status = TransformationUtil.objectToInteger(content.get("status"));
        this.enabled = TransformationUtil.objectToInteger(content.get("enabled"));
        this.internal = TransformationUtil.objectToInteger(content.get("internal"));
        this.administrator = TransformationUtil.objectToInteger(content.get("administrator"));
        this.account = TransformationUtil.objectToString(content.get("account"));
        this.phone = TransformationUtil.objectToString(content.get("phone"));
        this.mailbox = TransformationUtil.objectToString(content.get("mailbox"));
        this.name = TransformationUtil.objectToString(content.get("name"));
        this.nickname = TransformationUtil.objectToString(content.get("nickname"));
        this.language = TransformationUtil.objectToString(content.get("language"));
        this.avatar = TransformationUtil.objectToString(content.get("avatar"));
        this.description = TransformationUtil.objectToString(content.get("description"));
    }

    public SimpleUserModel(
            Integer id,
            Integer status,
            Integer enabled,
            Integer internal,
            Integer administrator,
            String account,
            String phone,
            String mailbox,
            String name,
            String nickname,
            String language,
            String avatar,
            String description
    ) {
        this.id = id;
        this.status = status;
        this.enabled = enabled;
        this.internal = internal;
        this.administrator = administrator;
        this.account = account;
        this.phone = phone;
        this.mailbox = mailbox;
        this.name = name;
        this.nickname = nickname;
        this.language = language;
        this.avatar = avatar;
        this.description = description;
    }

    @Override
    public String id() {
        return String.valueOf(this.id);
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public User password(String password) {
        this.password = password;
        return this;
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
            put("language", language);
            put("avatar", avatar);
            put("description", description);
        }};
    }

}
