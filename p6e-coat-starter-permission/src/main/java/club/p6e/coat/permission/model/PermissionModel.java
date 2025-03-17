package club.p6e.coat.permission.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Permission Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
public class PermissionModel implements Serializable {

    private Integer oid;
    private Integer pid;

    private Integer uid;
    private String uUrl;
    private String uBaseUrl;
    private String uMethod;

    private Integer gid;
    private String gMark;
    private Integer gWeight;

    private String rConfig;
    private String rAttribute;

}
