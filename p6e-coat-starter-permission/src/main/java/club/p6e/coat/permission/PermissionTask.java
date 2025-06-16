package club.p6e.coat.permission;

import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Permission Task
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@EnableScheduling
@ConditionalOnMissingBean(PermissionTask.class)
public class PermissionTask {

    @Scheduled(initialDelay = 10000L, fixedRate = 3600000L)
    public void run() {
        executeRefresh();
    }

    /**
     * Execute Refresh Task
     */
    public synchronized void executeRefresh() {
        final Object o = SpringUtil.getBean(PermissionAutoRefreshTask.class).execute();
        if (o instanceof Mono<?> m) {
            m.block();
        }
    }

}
