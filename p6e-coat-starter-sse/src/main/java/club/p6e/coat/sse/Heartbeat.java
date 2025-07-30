package club.p6e.coat.sse;

import club.p6e.coat.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Heartbeat
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class Heartbeat {

    /**
     * Channel Name List
     */
    private static final List<String> CHANNEL_NAME_LIST = new ArrayList<>();

    /**
     * Scheduled Executor Service Object
     */
    private static ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(
            1, r -> new Thread(r, "P6E-SSE-HEARTBEAT-THREAD-" + r.hashCode()));

    /**
     * Interval Time
     */
    private long interval;

    /**
     * Constructor Initialization
     */
    public Heartbeat() {
        this(60L);
    }

    /**
     * Constructor Initialization
     *
     * @param interval Interval Time
     */
    public Heartbeat(long interval) {
        this.interval = interval <= 0 ? 60L : interval;
        EXECUTOR.submit(new Task(this.interval));
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

    /**
     * Task
     */
    private static class Task implements Runnable {

        /**
         * Inject Log Object
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

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

        @SuppressWarnings("ALL")
        @Override
        public void run() {
            try {
                while (true) {
                    for (final String name : CHANNEL_NAME_LIST) {
                        final List<Session> list = SessionManager.getChannelList(name);
                        for (final Session session : list) {
                            try {
                                session.push("HEARTBEAT", JsonUtil.toJson(
                                        Map.of("type", "HEARTBEAT", "data",
                                                String.valueOf(System.currentTimeMillis()))));
                            } catch (Exception e) {
                                LOGGER.error("[ HEARTBEAT TASK ] PUSH ERROR => {} ", e.getMessage(), e);
                            }
                        }
                    }
                    Thread.sleep(interval * 1000L);
                }
            } catch (Exception e) {
                LOGGER.error("[ HEARTBEAT TASK ] ERROR => {} ", e.getMessage(), e);
            }
        }

    }

}
