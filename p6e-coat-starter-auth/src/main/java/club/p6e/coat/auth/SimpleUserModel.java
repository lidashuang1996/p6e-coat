package club.p6e.coat.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
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

    public SimpleUserModel(Integer id, String account, String phone, String mailbox, String password) {
        this.id = id;
        this.account = account;
        this.phone = phone;
        this.mailbox = mailbox;
        this.password = password;
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
        System.out.println("password    >>>> LLLLL <<<<<<<<<<" + password);
        return this.password;
    }

    @Override
    public User password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String serialize() {
        return "";
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of();
    }

}
