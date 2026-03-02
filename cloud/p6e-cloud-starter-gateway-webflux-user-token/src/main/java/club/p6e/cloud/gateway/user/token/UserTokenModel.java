package club.p6e.cloud.gateway.user.token;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Token Model
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UserTokenModel implements Serializable {

    /**
     * ID
     */
    private Integer id;

    /**
     * UID
     */
    private Integer uid;

    /**
     * Content
     */
    private String content;

    /**
     * End Date Time
     */
    private LocalDateTime endDateTime;

    /**
     * Start Date Time
     */
    private LocalDateTime startDateTime;

}
