package club.p6e.coat.permission;

/**
 * Permission Task
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionTask {

    /**
     * Get Version
     *
     * @return Version
     */
    public long version();

    /**
     * Get Interval
     *
     * @return Interval
     */
    public long interval();

}
