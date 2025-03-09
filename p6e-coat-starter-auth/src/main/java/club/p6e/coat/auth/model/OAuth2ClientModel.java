package club.p6e.coat.auth.model;

import club.p6e.DatabaseConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 Client Model
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
@Table(OAuth2ClientModel.TABLE)
public class OAuth2ClientModel implements Serializable {

    public static final String TABLE = DatabaseConfig.TABLE_PREFIX + "oauth2_client";

    public static final String ID = "id";
    public static final String ENABLED = "enabled";
    public static final String TYPE = "type";
    public static final String SCOPE = "scope";
    public static final String REDIRECT_URI = "redirectUri";
    public static final String RECONFIRM = "reconfirm";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String CLIENT_NAME = "clientName";
    public static final String CLIENT_DESCRIPTION = "clientDescription";
    public static final String CREATOR = "creator";
    public static final String MODIFIER = "modifier";
    public static final String CREATION_DATE_TIME = "creationDateTime";
    public static final String MODIFICATION_DATE_TIME = "modificationDateTime";
    public static final String VERSION = "version";
    public static final String IS_DELETED = "isDeleted";

    @Id
    private Integer id;
    private Integer enabled;
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
    private Integer version;
    private Integer isDeleted;

    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("enabled", enabled);
        result.put("type", type);
        result.put("scope", scope);
        result.put("redirectUri", redirectUri);
        result.put("reconfirm", reconfirm);
        result.put("clientId", clientId);
        result.put("clientSecret", clientSecret);
        result.put("clientName", clientName);
        result.put("clientAvatar", clientAvatar);
        result.put("clientDescription", clientDescription);
        result.put("creator", creator);
        result.put("modifier", modifier);
        result.put("creationDateTime", creationDateTime);
        result.put("modificationDateTime", modificationDateTime);
        return result;
    }

}



