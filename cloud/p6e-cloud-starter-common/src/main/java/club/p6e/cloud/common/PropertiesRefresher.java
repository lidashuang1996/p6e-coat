package club.p6e.cloud.common;

import club.p6e.coat.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties Refresher
 *
 * @author lidashuang
 * @version 1.0
 */
public class PropertiesRefresher {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesRefresher.class);

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initializers
     *
     * @param properties Properties Object
     */
    public PropertiesRefresher(Properties properties) {
        this.properties = properties;
    }

    /**
     * Execute Refresh
     *
     * @param properties Properties Object
     */
    @SuppressWarnings("ALL")
    public void execute(Properties properties) {
        LOGGER.info("[ NEW PROPERTIES ] ({}) >>> {}", properties.getClass(), JsonUtil.toJson(properties));
        this.properties.setVersion(properties.getVersion());
        this.properties.setSecurity(properties.getSecurity());
        this.properties.setCrossDomain(properties.getCrossDomain());
        this.properties.setSnowflake(properties.getSnowflake());
    }

}
