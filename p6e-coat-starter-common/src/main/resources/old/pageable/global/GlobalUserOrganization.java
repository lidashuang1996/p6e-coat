package club.p6e.coat.common.old.pageable.global;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class GlobalUserOrganization implements Serializable {

    public static GlobalUserOrganization DEBUG = new GlobalUserOrganization("-1");

    private String organization;

    public static void setDebug(GlobalUserOrganization debug) {
        DEBUG = debug;
    }

    public GlobalUserOrganization(String organization) {
        this.organization = organization;
    }

}
