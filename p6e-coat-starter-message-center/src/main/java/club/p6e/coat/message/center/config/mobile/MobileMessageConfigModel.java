package club.p6e.coat.message.center.config.mobile;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Mobile Message Config Model
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigModel extends ConfigModel, Serializable {

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
     * Get Application Key
     *
     * @return Application Key
     */
    String getApplicationKey();

    /**
     * Set Application Key
     *
     * @param key Application Key
     */
    void setApplicationKey(String key);

    /**
     * Get Application Name
     *
     * @return Application Name
     */
    String getApplicationName();

    /**
     * Set Application Name
     *
     * @param name Application Name
     */
    void setApplicationName(String name);

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
