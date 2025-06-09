package club.p6e.coat.permission;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Permission Details
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class PermissionDetails implements Serializable {

    /**
     * UID
     */
    private Integer uid;

    /**
     * GID
     */
    private Integer gid;

    /**
     * Weight
     */
    private Integer weight;

    /**
     * Url
     */
    private String url;

    /**
     * Base Url
     */
    private String baseUrl;

    /**
     * Method
     */
    private String method;

    /**
     * Mark
     */
    private String mark;

    /**
     * Config
     */
    private String config;

    /**
     * Attribute
     */
    private String attribute;

    /**
     * Path
     */
    private String path;

    /**
     * Version
     */
    private Long version;

    /**
     * Data
     */
    private Map<String, Objects> data;

}
