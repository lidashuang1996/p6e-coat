package club.p6e.coat.auth.model;

import club.p6e.DatabaseConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
@Table(UserModel.TABLE)
public class UserModel implements Serializable {

    public static final String TABLE = DatabaseConfig.TABLE_PREFIX + "user";

    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String ENABLED = "enabled";
    public static final String INTERNAL = "internal";
    public static final String ADMINISTRATOR = "administrator";
    public static final String ACCOUNT = "account";
    public static final String PHONE = "phone";
    public static final String MAILBOX = "mailbox";
    public static final String NAME = "name";
    public static final String NICKNAME = "nickname";
    public static final String AVATAR = "avatar";
    public static final String DESCRIPTION = "description";
    public static final String LANGUAGE = "language";
    public static final String CREATOR = "creator";
    public static final String MODIFIER = "modifier";
    public static final String CREATION_DATE_TIME = "creationDateTime";
    public static final String MODIFICATION_DATE_TIME = "modificationDateTime";
    public static final String VERSION = "version";
    public static final String IS_DELETED = "isDeleted";

    @Id
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
    private String creator;
    private String modifier;
    private LocalDateTime creationDateTime;
    private LocalDateTime modificationDateTime;
    private Integer version;
    private Integer isDeleted;

}
