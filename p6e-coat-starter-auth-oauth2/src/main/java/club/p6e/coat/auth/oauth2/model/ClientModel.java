package club.p6e.coat.auth.oauth2.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientModel {

    private Integer enable;

    private String scope;
    private String redirectUri;

}
