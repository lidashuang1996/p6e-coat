package club.p6e.coat.permission.task;

/**
 * Blocking Permission Auto Refresh Task
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingPermissionAutoRefreshTask {

    /**
     * Execute Task
     *
     * @return Refresh Count
     */
    Long execute();

    /**
     * Get Version
     *
     * @return Version
     */
    Integer version();

}
