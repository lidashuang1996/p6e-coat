package club.p6e.coat.permission.model;

import club.p6e.DatabaseConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

/**
 * Permission Url Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
@Table(PermissionUrlModel.TABLE)
public class PermissionUrlModel implements Serializable {

    public static final String TABLE = DatabaseConfig.TABLE_PREFIX + "permission_url";

    public static final String ID = "id";
    public static final String OID = "oid";
    public static final String PID = "pid";
    public static final String URL = "url";
    public static final String BASE_URL = "baseUrl";
    public static final String METHOD = "method";
    public static final String NAME = "name";
    public static final String CONFIG = "config";
    public static final String DESCRIPTION = "description";

    private Integer id;
    private Integer oid;
    private Integer pid;
    private String url;
    private String baseUrl;
    private String method;
    private String name;
    private String config;
    private String description;

}
