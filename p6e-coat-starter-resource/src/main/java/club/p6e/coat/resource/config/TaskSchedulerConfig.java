package club.p6e.coat.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 任务调度配置
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@Configuration
public class TaskSchedulerConfig {

    @Bean
    public TaskScheduler injectTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("P6E-COAT-TASK-SCHEDULER-");
        return taskScheduler;
    }

}
