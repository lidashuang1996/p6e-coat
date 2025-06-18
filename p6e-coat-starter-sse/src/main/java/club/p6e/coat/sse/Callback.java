package club.p6e.coat.sse;

import club.p6e.coat.websocket.Session;

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
    void onOpen(club.p6e.coat.websocket.Session session);

    /**
     * Close
     *
     * @param session Session Object
     */
    void onClose(club.p6e.coat.websocket.Session session);

    /**
     * Text Message
     *
     * @param session Session Object
     * @param text    Text
     */
    void onMessage(club.p6e.coat.websocket.Session session, String text);

    /**
     * Bytes Message
     *
     * @param session Session Object
     * @param bytes   Bytes
     */
    void onMessage(club.p6e.coat.websocket.Session session, byte[] bytes);

    /**
     * Error
     *
     * @param session   Session Object
     * @param throwable Throwable Object
     */
    void onError(Session session, Throwable throwable);

}
