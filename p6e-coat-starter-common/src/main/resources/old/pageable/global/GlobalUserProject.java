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
public class GlobalUserProject implements Serializable {

    public static GlobalUserProject DEBUG = new GlobalUserProject("-1");

    private String project;

    public static void setDebug(GlobalUserProject debug) {
        DEBUG = debug;
    }

    public GlobalUserProject(String project) {
        this.project = project;
    }

}
