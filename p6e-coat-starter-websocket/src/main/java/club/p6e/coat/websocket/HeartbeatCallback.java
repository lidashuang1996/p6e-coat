package club.p6e.coat.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat Callback
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class HeartbeatCallback implements Callback {

    /**
     * HEARTBEAT CONTENT TEXT
     */
    public static final String CONTENT_TEXT = "{\"type\":\"heartbeat\"}";

    /**
     * HEARTBEAT CONTENT BYTES
     */
    public static final byte[] CONTENT_BYTES = new byte[]{
            16, 0, 0, 0, 16, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    /**
     * Interval Time
     */
    private static final long INTERVAL = 50;

    /**
     * Channel Name List
     */
    private static final List<String> CHANNEL_NAME_LIST = new ArrayList<>();

    /**
     * Scheduled Executor Service Object
     */
    private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(
            1, r -> new Thread(r, "P6E-WS-HEARTBEAT-THREAD-" + r.hashCode()));

    /**
     * Constructor Initialization
     */
    public HeartbeatCallback() {
        EXECUTOR.scheduleAtFixedRate(new Task(), INTERVAL, INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Register
     *
     * @param name Channel Name
     */
    @SuppressWarnings("ALL")
    public static void register(String name) {
        synchronized (CHANNEL_NAME_LIST) {
            CHANNEL_NAME_LIST.add(name);
        }
    }

    /**
     * Unregister
     *
     * @param name Channel Name
     */
    @SuppressWarnings("ALL")
    public static void unregister(String name) {
        synchronized (CHANNEL_NAME_LIST) {
            CHANNEL_NAME_LIST.remove(name);
        }
    }

    @Override
    public void onOpen(Session session) {
    }

    @Override
    public void onClose(Session session) {
    }

    @Override
    public void onMessage(Session session, String text) {
        if (CONTENT_TEXT.equalsIgnoreCase(text)) {
            session.refresh();
            session.push(CONTENT_TEXT);
        }
    }

    @Override
    public void onMessage(Session session, byte[] bytes) {
        if (CONTENT_BYTES.length == bytes.length) {
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] != CONTENT_BYTES[i]) {
                    return;
                }
            }
            session.refresh();
            session.push(CONTENT_TEXT);
        }
    }

    @Override
    public void onError(Session session, Throwable throwable) {
    }

    /**
     * Task
     */
    private static class Task implements Runnable {

        /**
         * Inject Log Object
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

        @Override
        public void run() {
            try {
                final long now = System.currentTimeMillis();
                for (final String name : CHANNEL_NAME_LIST) {
                    final List<Session> list = SessionManager.getChannelList(name);
                    for (final Session session : list) {
                        try {
                            if (now - session.getDate() > INTERVAL * 3000L) {
                                session.close();
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("[ HEARTBEAT TASK ] ERROR => {} ", e.getMessage(), e);
            }
        }

    }

}
