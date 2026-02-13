package club.p6e.coat.permission;

/**
 * Permission Task Callback
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionTaskCallback {

    /**
     * Before
     *
     * @param num Num
     */
    void before(long num);

    /**
     * After
     *
     * @param num Num
     */
    void after(long num);

}
