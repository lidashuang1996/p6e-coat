package club.p6e.cloud.gateway;

import club.p6e.coat.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Properties Refresher
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.cloud.gateway.PropertiesRefresher")
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
     * Custom Route Locator Object
     */
    private final RouteLocator locator;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     * @param locator    Custom Route Locator Object
     */
    public PropertiesRefresher(Properties properties, RouteLocator locator) {
        this.locator = locator;
        this.properties = properties;
        this.locator.refresh(this.properties.getRoutes());
    }

    /**
     * Execute Refresh
     *
     * @param properties Properties Object
     */
    @SuppressWarnings("ALL")
    public void execute(Properties properties) {
        LOGGER.info("[ NEW PROPERTIES ] ({}) >>> {}", properties.getClass(), JsonUtil.toJson(properties));
        this.properties.setLog(properties.getLog());
        this.properties.setRoutes(properties.getRoutes());
        // custom gateway routing locators to perform new routing configurations
        this.locator.refresh(this.properties.getRoutes());
    }

}
