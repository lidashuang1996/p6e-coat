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
     * Interval Time
     */
    private static long INTERVAL = 50;

    /**
     * Scheduled Executor Service Object
     */
    private static ScheduledExecutorService EXECUTOR;

    /**
     * Run
     */
    @SuppressWarnings("ALL")
    public static void run() {
        EXECUTOR = new ScheduledThreadPoolExecutor(
                1, r -> new Thread(r, "P6E-SSE-HEARTBEAT-THREAD-" + r.hashCode()));
        EXECUTOR.submit(new Task());
    }

    /**
     * Run
     */
    @SuppressWarnings("ALL")
    public static void stop() {
        EXECUTOR.shutdownNow();
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
     * Set Interval
     *
     * @param interval Interval Data
     */
    @SuppressWarnings("ALL")
    public static void setInterval(long interval) {
        Heartbeat.INTERVAL = interval;
    }

    /**
     * Task
     */
    private static class Task implements Runnable {

        /**
         * Inject Log Object
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

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
                    Thread.sleep(INTERVAL * 1000L);
                }
            } catch (Exception e) {
                LOGGER.error("[ HEARTBEAT TASK ] ERROR => {} ", e.getMessage(), e);
            }
        }

    }

}
