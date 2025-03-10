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
public class GlobalUserLanguage implements Serializable {

    public static GlobalUserLanguage DEBUG = new GlobalUserLanguage("zh-cn");

    private String language;

    public static void setDebug(GlobalUserLanguage debug) {
        DEBUG = debug;
    }

    public GlobalUserLanguage(String language) {
        this.language = language;
    }

}
