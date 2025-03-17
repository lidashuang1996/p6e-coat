package club.p6e.coat.permission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Scheduling Config
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public ThreadPoolTaskScheduler injectThreadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}
