package club.p6e.coat.permission.model;

import club.p6e.DatabaseConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

/**
 * Permission Url Group Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
@Table(PermissionUrlGroupModel.TABLE)
public class PermissionUrlGroupModel implements Serializable {

    public static final String TABLE = DatabaseConfig.TABLE_PREFIX + "permission_url_group";

    public static final String ID = "id";
    public static final String OID = "oid";
    public static final String PID = "pid";
    public static final String NAME = "name";
    public static final String MARK = "mark";
    public static final String WEIGHT = "weight";
    public static final String DESCRIPTION = "description";

    private Integer id;
    private Integer oid;
    private Integer pid;
    private String name;
    private String mark;
    private Integer weight;
    private String description;

}
