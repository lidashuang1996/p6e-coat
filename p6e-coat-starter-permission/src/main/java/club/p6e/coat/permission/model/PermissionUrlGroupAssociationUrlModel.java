package club.p6e.coat.permission.model;

import club.p6e.DatabaseConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Permission Url Group Association Url Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
@Table(PermissionUrlGroupAssociationUrlModel.TABLE)
public class PermissionUrlGroupAssociationUrlModel {

    public static final String TABLE = DatabaseConfig.TABLE_PREFIX + "permission_url_group_association_url";

    public static final String GID = "gid";
    public static final String UID = "uid";
    public static final String OID = "oid";
    public static final String PID = "pid";
    public static final String CONFIG = "config";
    public static final String ATTRIBUTE = "attribute";

    private Integer gid;
    private Integer uid;
    private Integer oid;
    private Integer pid;
    private String config;
    private String attribute;

}
