package club.p6e.cloud.common;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Database Polling Properties Refresher
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public abstract class DatabasePollingPropertiesRefresher {

    /**
     * TOPIC
     */
    private static String CONFIG_TOPIC = "p6e-cloud-config";

    /**
     * Scheduled Executor Service Object
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /**
     * Constructor Initializers
     *
     * @param context Configurable Application Context Object
     */
    public DatabasePollingPropertiesRefresher(ConfigurableApplicationContext context) {
        context.addApplicationListener(new ReadyEventListener(this));
        context.addApplicationListener(new ClosedEventListener(this));
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
     * Set Config Topic
     *
     * @param topic Topic Object
     */
    public static void setConfigTopic(String topic) {
        CONFIG_TOPIC = topic;
    }

    /**
     * Init Data
     */
    protected void init() {
        this.executor.scheduleAtFixedRate(() -> {
            boolean status = true;
            try {
                final Config config = getBlockingData(CONFIG_TOPIC);
                if (config != null && config.getFormat() != null && config.getContent() != null) {
                    status = false;
                    config(config.getFormat(), config.getContent());
                    return;
                }
            } catch (Exception e) {
                // ignore exception
            }
            if (status) {
                try {
                    final Config config = getReactiveData(CONFIG_TOPIC).block();
                    if (config != null && config.getFormat() != null && config.getContent() != null) {
                        config(config.getFormat(), config.getContent());
                    }
                } catch (Exception e) {
                    // ignore exception
                }
            }
        }, 0L, interval(), TimeUnit.SECONDS);
    }

    /**
     * Close
     */
    protected void close() {
        this.executor.shutdown();
    }

    /**
     * Interval
     */
    protected long interval() {
        return 3600L;
    }

    /**
     * Config
     */
    protected abstract void config(String format, String content);

    /**
     * Blocking Data
     */
    protected abstract Config getBlockingData(String topic);

    /**
     * Reactive Data
     */
    protected abstract Mono<Config> getReactiveData(String topic);

    /**
     * Config
     */
    @Data
    @Accessors(chain = true)
    public static class Config implements Serializable {

        /**
         * Format
         */
        private String format;

        /**
         * Content
         */
        private String content;

    }

    /**
     * Spring Ready Event Listener
     */
    public static class ReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

        /**
         * Database Polling Properties Refresher Object
         */
        private final DatabasePollingPropertiesRefresher refresher;

        /**
         * Constructor Initializers
         *
         * @param refresher Database Polling Properties Refresher Object
         */
        public ReadyEventListener(DatabasePollingPropertiesRefresher refresher) {
            this.refresher = refresher;
        }

        @Override
        public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
            refresher.init();
        }

    }

    /**
     * Spring Close Event Listener
     */
    public static class ClosedEventListener implements ApplicationListener<ContextClosedEvent> {

        /**
         * Database Polling Properties Refresher Object
         */
        private final DatabasePollingPropertiesRefresher refresher;

        /**
         * Constructor Initializers
         *
         * @param refresher Database Polling Properties Refresher Object
         */
        public ClosedEventListener(DatabasePollingPropertiesRefresher refresher) {
            this.refresher = refresher;
        }

        @Override
        public void onApplicationEvent(@NonNull ContextClosedEvent event) {
            refresher.close();
        }

    }

}
