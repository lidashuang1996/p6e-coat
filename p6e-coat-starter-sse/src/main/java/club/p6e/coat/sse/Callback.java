package club.p6e.coat.sse;

/**
 * Callback
 *
 * @author lidashuang
 * @version 1.0
 */
public interface Callback {

    /**
     * Open
     *
     * @param session Session Object
     */
    void onOpen(Session session);

    /**
     * Close
     *
     * @param session Session Object
     */
    void onClose(Session session);

    /**
     * Error
     *
     * @param session   Session Object
     * @param throwable Throwable Object
     */
    void onError(Session session, Throwable throwable);

}
