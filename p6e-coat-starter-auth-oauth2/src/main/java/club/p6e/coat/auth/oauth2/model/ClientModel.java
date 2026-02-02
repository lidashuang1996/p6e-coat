package club.p6e.coat.auth.oauth2.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Client Model
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class ClientModel implements Serializable {

    private Integer id;
    private Integer enable;
    private String type;
    private String scope;
    private String redirectUri;
    private Integer reconfirm;
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String clientAvatar;
    private String clientDescription;
    private String creator;
    private String modifier;
    private LocalDateTime creationDateTime;
    private LocalDateTime modificationDateTime;

}
