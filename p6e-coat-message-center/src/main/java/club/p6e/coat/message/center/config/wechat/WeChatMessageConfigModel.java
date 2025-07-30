package club.p6e.coat.message.center.config.wechat;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * We Chat Message Config Model
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageConfigModel extends ConfigModel, Serializable {

    /**
     * Get Application ID
     *
     * @return Application ID
     */
    String getApplicationId();

    /**
     * Set Application ID
     *
     * @param id Application ID
     */
    void setApplicationId(String id);

    /**
     * Get Application Secret
     *
     * @return Application Secret
     */
    String getApplicationSecret();

    /**
     * Set Application Secret
     *
     * @param secret Application Secret
     */
    void setApplicationSecret(String secret);

    /**
     * Get Access Token Url
     *
     * @return Access Token Url
     */
    String getAccessTokenUrl();

    /**
     * Set Access Token Url
     *
     * @param url Access Token Url
     */
    void setAccessTokenUrl(String url);

    /**
     * Get Other
     *
     * @return Other
     */
    Map<String, String> getOther();

    /**
     * Set Other
     *
     * @param other Other
     */
    void setOther(Map<String, String> other);

}
