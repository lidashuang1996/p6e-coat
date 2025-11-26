package club.p6e.cloud.common;

import club.p6e.coat.common.pageable.PageableContext;
import club.p6e.coat.common.sortable.SortableContext;
import club.p6e.coat.common.utils.JsonUtil;
import jakarta.annotation.Nonnull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.HashMap;

public class RedisPropertiesProvider {

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
        context.addApplicationListener(new RedisPropertiesProvider.ReadyEventListener(this));
        context.addApplicationListener(new RedisPropertiesProvider.ContextClosedEventListener(this));
    }

    /**
     * Constructor Initializers
     *
     * @param template org.springframework.data.redis.core.ReactiveStringRedisTemplate Object
     * @param context  Configurable Application Context Object
     */
    public RedisPropertiesProvider(org.springframework.data.redis.core.ReactiveStringRedisTemplate template, ConfigurableApplicationContext context) {
        this.reactiveStringRedisTemplate = template;
        context.addApplicationListener(new RedisPropertiesProvider.ReadyEventListener(this));
        context.addApplicationListener(new RedisPropertiesProvider.ContextClosedEventListener(this));
    }

    /**
     * 刷新配置信息
     *
     * @param channel 频道名称
     */
    public void refreshConfig(String channel) {
        this.template.convertAndSend(channel, execute(channel));
    }

    /**
     * 查询配置信息
     *
     * @param channel 频道名称
     * @return 配置信息
     */
    protected String execute(String channel) {

    }


    /**
     * Spring Init Event Listener
     */
    public static class ReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

        /**
         * RedisPropertiesRefresher Object
         */
        private final RedisPropertiesRefresher refresher;

        /**
         * Constructor Initializers
         *
         * @param refresher RedisPropertiesRefresher Object
         */
        public ReadyEventListener(RedisPropertiesProvider refresher) {
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
    public static class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {

        /**
         * RedisPropertiesRefresher Object
         */
        private final RedisPropertiesRefresher refresher;

        /**
         * Constructor Initializers
         *
         * @param refresher RedisPropertiesRefresher Object
         */
        public ContextClosedEventListener(RedisPropertiesProvider refresher) {
            this.refresher = refresher;
        }

        @Override
        public void onApplicationEvent(@Nonnull ContextClosedEvent event) {
            refresher.close();
        }

    }

}

