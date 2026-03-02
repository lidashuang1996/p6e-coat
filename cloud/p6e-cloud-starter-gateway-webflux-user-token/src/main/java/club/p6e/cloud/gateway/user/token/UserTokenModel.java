package club.p6e.cloud.gateway.user.token;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UserTokenModel implements Serializable {
    private Integer id;
    private Integer uid;
    private String content;
}
