package club.p6e.coat.message.center;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Message Center Properties
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class MessageCenterProperties {

    /**
     * Tmp Resource Path
     */
    private String tmpResourcePath;

}
