package club.p6e.coat.sse;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Message Context
 */
@Data
@Accessors(chain = true)
public class MessageContext implements Serializable {

    /**
     * Message Context / Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Name
         */
        private String name;

        /**
         * Content
         */
        private String content;

        /**
         * Users
         */
        private List<String> users;

    }

}
