package club.p6e.coat.auth.oauth2.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Index Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class IndexContext implements Serializable {

    /**
     * Index Context / Dto
     */
    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {

        /**
         * Type
         */
        private String type;

        /**
         * Content
         */
        private String content;

    }

}
