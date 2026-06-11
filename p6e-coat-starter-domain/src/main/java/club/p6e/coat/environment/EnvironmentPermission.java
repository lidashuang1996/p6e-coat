package club.p6e.coat.environment;

/**
 * Environment Permission
 *
 * @author lidashuang
 * @version 1.0
 */
public interface EnvironmentPermission {

    /**
     * Get Environment Permission ID
     *
     * @return Environment Permission ID
     */
    Integer id();

    /**
     * Get Environment Permission Mark
     *
     * @return Environment Permission Mark
     */
    String mark();

    /**
     * Get Environment Permission Config
     *
     * @return Environment Permission Config
     */
    String config();

}
