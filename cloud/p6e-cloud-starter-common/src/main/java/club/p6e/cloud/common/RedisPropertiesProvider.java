package club.p6e.cloud.common;

import club.p6e.coat.common.utils.JsonUtil;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
 * Redis Properties Provider
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public abstract class RedisPropertiesProvider {

    /**
     * TOPIC
     */
    private static String CONFIG_TOPIC = "p6e-cloud-config";

    /**
     * reactor.core.Disposable Object
     */
    private reactor.core.Disposable subscription;

    /**
     * org.springframework.data.redis.core.StringRedisTemplate Object
     */
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;

    /**
     * org.springframework.data.redis.core.ReactiveStringRedisTemplate Object
     */
    private org.springframework.data.redis.core.ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * Constructor Initializers
     *
     * @param template org.springframework.data.redis.core.StringRedisTemplate Object
     * @param context  Configurable Application Context Object
     */
    public RedisPropertiesProvider(org.springframework.data.redis.core.StringRedisTemplate template, ConfigurableApplicationContext context) {
        this.stringRedisTemplate = template;
        context.addApplicationListener(new ReadyEventListener(this));
        context.addApplicationListener(new ContextClosedEventListener(this));
    }

    /**
     * Constructor Initializers
     *
     * @param template org.springframework.data.redis.core.ReactiveStringRedisTemplate Object
     * @param context  Configurable Application Context Object
     */
    public RedisPropertiesProvider(org.springframework.data.redis.core.ReactiveStringRedisTemplate template, ConfigurableApplicationContext context) {
        this.reactiveStringRedisTemplate = template;
        context.addApplicationListener(new ReadyEventListener(this));
        context.addApplicationListener(new ContextClosedEventListener(this));
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
        try {
            Class.forName("org.springframework.data.redis.core.StringRedisTemplate");
            if (stringRedisTemplate != null && stringRedisTemplate.getConnectionFactory() != null) {
                new RedisMessageListenerContainer() {{
                    addMessageListener((message, pattern) -> {
                        final String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
                        final Config config = getBlockingData(channel);
                        if (config != null) {
                            stringRedisTemplate.convertAndSend(channel, JsonUtil.toJson(new HashMap<>() {{
                                put("type", "config");
                                put("format", config.getFormat());
                                put("content", config.getContent());
                            }}));
                        }
                    }, List.of(ChannelTopic.of(CONFIG_TOPIC)));
                    setConnectionFactory(stringRedisTemplate.getConnectionFactory());
                    afterPropertiesSet();
                    start();
                }};
                return;
            }
        } catch (Exception e) {
            // ignore exception
        }
        try {
            Class.forName("org.springframework.data.redis.core.ReactiveStringRedisTemplate");
            if (reactiveStringRedisTemplate != null) {
                subscription = reactiveStringRedisTemplate
                        .listenTo(ChannelTopic.of(CONFIG_TOPIC))
                        .map(message -> {
                            final String channel = message.getChannel();
                            return getReactiveData(channel)
                                    .flatMap(config -> reactiveStringRedisTemplate
                                            .convertAndSend(channel, JsonUtil.toJson(new HashMap<>() {{
                                                put("type", "config");
                                                put("format", config.getFormat());
                                                put("content", config.getContent());
                                            }}))
                                    ).switchIfEmpty(Mono.just(0L));
                        }).subscribe();
            }
        } catch (Exception e) {
            // ignore exception
        }
    }

    /**
     * Close
     */
    @SuppressWarnings("ALL")
    protected void close() {
        try {
            if (this.subscription != null && this.subscription.isDisposed()) {
                this.subscription.dispose();
            }
        } catch (Exception e) {
            // ignore exception
        }
        try {
            Class.forName("org.springframework.data.redis.core.StringRedisTemplate");
            this.stringRedisTemplate = null;
        } catch (Exception e) {
            // ignore exception
        }
        try {
            Class.forName("org.springframework.data.redis.core.ReactiveStringRedisTemplate");
            this.reactiveStringRedisTemplate = null;
        } catch (Exception e) {
            // ignore exception
        }
    }

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
     * Spring Init Event Listener
     */
    public static class ReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

        /**
         * Redis Properties Provider Object
         */
        private final RedisPropertiesProvider provider;

        /**
         * Constructor Initializers
         *
         * @param provider Redis Properties Provider Object
         */
        public ReadyEventListener(RedisPropertiesProvider provider) {
            this.provider = provider;
        }

        @Override
        public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
            provider.init();
        }

    }

    /**
     * Spring Close Event Listener
     */
    public static class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {

        /**
         * Redis Properties Provider Object
         */
        private final RedisPropertiesProvider provider;

        /**
         * Constructor Initializers
         *
         * @param provider Redis Properties Provider Object
         */
        public ContextClosedEventListener(RedisPropertiesProvider provider) {
            this.provider = provider;
        }

        @Override
        public void onApplicationEvent(@Nonnull ContextClosedEvent event) {
            provider.close();
        }

    }

}

