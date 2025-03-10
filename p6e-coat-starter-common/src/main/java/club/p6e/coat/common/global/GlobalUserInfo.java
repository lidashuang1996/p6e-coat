package club.p6e.coat.common.global;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class GlobalUserInfo implements Serializable {

    public static GlobalUserInfo DEBUG = new GlobalUserInfo();

    public static void setDebug(GlobalUserInfo debug) {
        DEBUG = debug;
    }

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
    private Map<String, Set<String>> organizationalProject = new HashMap<>();
    private Map<String, Map<String, Set<String>>> permission = new HashMap<>();

}
