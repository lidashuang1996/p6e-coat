package club.p6e.coat.permission;

/**
 * Permission Task Callback
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
