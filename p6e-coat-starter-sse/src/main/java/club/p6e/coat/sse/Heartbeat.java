package club.p6e.coat.sse;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Slf4j
public class Heartbeat {

    /**
     * HEARTBEAT CONTENT TEXT
     */
    public static final String CONTENT_TEXT = "{\"type\":\"heartbeat\"}";

    /**
     * Channel Name List
     */
    private static final List<String> CHANNEL_NAME_LIST = new CopyOnWriteArrayList<>();

    /**
     * Scheduled Executor Service Object
     */
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(
            1, r -> new Thread(r, "P6E-WS-HEARTBEAT-THREAD-" + r.hashCode()));

    /**
     * Interval Time
     */
    @Getter
    private final long interval;

    /**
     * Init
     */
    @Getter
    private boolean init;

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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (Exception e) {
                executor.shutdownNow();
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

    /**
     * Init
     */
    @PostConstruct
    public synchronized void init() {
        if (!init) {
            init = true;
            log.info("[ SSE HEARTBEAT ] INIT ] {}", this.interval);
            executor.scheduleAtFixedRate(new Task(), this.interval, this.interval, TimeUnit.SECONDS);
        }
    }

    /**
     * Task
     */
    @Slf4j
    private static class Task implements Runnable {

        @Override
        public void run() {
            try {
                for (final String name : CHANNEL_NAME_LIST) {
                    SessionManager.forEachSessionInChannel(name, session -> {
                        try {
                            session.push(CONTENT_TEXT);
                        } catch (Exception e) {
                            log.warn("[ SSE HEARTBEAT TASK RUN PUSH MESSAGE ] {}/{} ERROR =>: {}", name, session, e.getMessage(), e);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("[ SSE HEARTBEAT TASK ] ERROR => {} ", e.getMessage(), e);
            }
        }

    }

}
