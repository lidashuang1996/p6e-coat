package club.p6e.coat.auth.model;

import club.p6e.DatabaseConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Auth Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
@Table(UserAuthModel.TABLE)
public class UserAuthModel implements Serializable {

    public static final String TABLE = DatabaseConfig.TABLE_PREFIX + "user_auth";

    public static final String ID = "id";
    public static final String ACCOUNT = "account";
    public static final String PHONE = "phone";
    public static final String MAILBOX = "mailbox";
    public static final String PASSWORD = "password";
    public static final String QQ = "qq";
    public static final String CREATOR = "creator";
    public static final String MODIFIER = "modifier";
    public static final String CREATION_DATE_TIME = "creationDateTime";
    public static final String MODIFICATION_DATE_TIME = "modificationDateTime";
    public static final String VERSION = "version";

    @Id
    private Integer id;
    private String account;
    private String phone;
    private String mailbox;
    private String password;
    private String qq;
    private String creator;
    private String modifier;
    private LocalDateTime creationDateTime;
    private LocalDateTime modificationDateTime;
    private Integer version;

}
