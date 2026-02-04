package club.p6e.coat.auth.oauth2.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Component("club.p6e.coat.auth.client.Properties")
@ConfigurationProperties(prefix = "p6e.coat.auth.client")
public class Properties implements Serializable {


    /**
     * Properties Instance Object
     */
    private static Properties INSTANCE = new Properties();
    public static Properties getInstance() {
        return INSTANCE;
    }

    private String authorizeTemplate = "@{URL}?response_type=@{TYPE}&client_id=@{APP_ID}&redirect_uri=@{URI}&scope=@{SCOPE}&state=@{STATE}@{EXTEND}";
    /**
     * AUTHORIZE URL
     */
    private String authorizeUrl;

    /**
     * AUTHORIZE TOKEN URL
     */
    private String authorizeTokenUrl;
    private String authorizeLogoutUrl;

    /**
     * AUTHORIZE APP ID
     */
    private String authorizeAppId;

    /**
     * AUTHORIZE APP SECRET
     */
    private String authorizeAppSecret;
    private String authorizeType;
    private String authorizeScope;
    /**
     * APP REDIRECT URI
     */
    private String authorizeAppRedirectUri;

    /**
     * APP EXTEND
     */
    private Map<String, String> authorizeAppExtend = new HashMap<>();

    /**
     * JWT ACCESS TOKEN SECRET
     */
    private String jwtAccessTokenSecret;

    /**
     * JWT REFRESH TOKEN SECRET
     */
    private String jwtRefreshTokenSecret;

}
