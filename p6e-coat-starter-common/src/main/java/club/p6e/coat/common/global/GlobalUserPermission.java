package club.p6e.coat.common.global;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class GlobalUserPermission implements Serializable {

    public static GlobalUserPermission DEBUG = new GlobalUserPermission();

    public static void setDebug(GlobalUserPermission debug) {
        DEBUG = debug;
    }

    private Integer uid;
    private Integer gid;
    private String url;
    private String baseUrl;
    private String method;
    private Integer weight;
    private String mark;
    private String config;
    private String attribute;
    private String path;
}
