package club.p6e.coat.websocket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
            0, 0, 0, 16, 0, 16, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0
    };

    /**
     * Channel Name List
     */
    private static final List<String> CHANNEL_NAME_LIST = new CopyOnWriteArrayList<>();

    /**
     * Scheduled Executor Service Object
     */
    private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(
            1, r -> new Thread(r, "P6E-WS-HEARTBEAT-THREAD-" + r.hashCode()));

    /**
     * Interval Time
     */
    @Getter
    private final long interval;

    /**
     * Constructor Initialization
     */
    public HeartbeatCallback() {
        this(60L);
    }

    /**
     * Constructor Initialization
     *
     * @param interval Interval Time
     */
    public HeartbeatCallback(long interval) {
        this.interval = interval <= 0 ? 60L : interval;
        EXECUTOR.scheduleAtFixedRate(new Task(this.interval), this.interval, this.interval, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            EXECUTOR.shutdown();
            try {
                if (!EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                    EXECUTOR.shutdownNow();
                }
            } catch (Exception e) {
                EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }

    /**
     * Register
     *
     * @param name Channel Name
     */
    public static void register(String name) {
        CHANNEL_NAME_LIST.add(name);
    }

    /**
     * Unregister
     *
     * @param name Channel Name
     */
    public static void unregister(String name) {
        CHANNEL_NAME_LIST.remove(name);
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
            session.push(CONTENT_BYTES);
        }
    }

    @Override
    public void onError(Session session, Throwable throwable) {
    }

    /**
     * Task
     */
    @Slf4j
    private static class Task implements Runnable {

        /**
         * Interval Time
         */
        private final long interval;

        /**
         * Constructor Initialization
         *
         * @param interval Interval Time
         */
        public Task(long interval) {
            this.interval = interval;
        }

        @Override
        public void run() {
            try {
                final long now = System.currentTimeMillis();
                for (final String name : CHANNEL_NAME_LIST) {
                    SessionManager.forEachSessionInChannel(name, session -> {
                        try {
                            if (now - session.getDate() > this.interval * 2 * 1000L) {
                                session.close();
                            }
                        } catch (Exception e) {
                            log.error("[ HEARTBEAT TASK FOR ] {}/{} ERROR =>: {}", name, session, e.getMessage(), e);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("[ HEARTBEAT TASK ] ERROR => {} ", e.getMessage(), e);
            }
        }

    }

}
