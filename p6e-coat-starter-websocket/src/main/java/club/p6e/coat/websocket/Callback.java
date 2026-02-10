package club.p6e.coat.websocket;

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
     * Text Message
     *
     * @param session Session Object
     * @param text    Text
     */
    void onMessage(Session session, String text);

    /**
     * Bytes Message
     *
     * @param session Session Object
     * @param bytes   Byte Array Object
     */
    void onMessage(Session session, byte[] bytes);

    /**
     * Error
     *
     * @param session   Session Object
     * @param throwable Throwable Object
     */
    void onError(Session session, Throwable throwable);

}
