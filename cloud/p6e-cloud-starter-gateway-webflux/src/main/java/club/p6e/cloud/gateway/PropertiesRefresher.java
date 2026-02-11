package club.p6e.cloud.gateway;

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
     * Route Locator Object
     */
    private final RouteLocator locator;

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param locator    Route Locator Object
     * @param properties Properties Object
     */
    public PropertiesRefresher(RouteLocator locator, Properties properties) {
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
