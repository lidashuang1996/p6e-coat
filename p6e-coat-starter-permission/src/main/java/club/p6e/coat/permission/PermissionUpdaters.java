package club.p6e.coat.permission;

import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Permission Updaters
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@EnableScheduling
@ConditionalOnMissingBean(
        value = PermissionUpdaters.class,
        ignored = PermissionUpdaters.class
)
public class PermissionUpdaters {

    @Scheduled(initialDelay = 10000L, fixedRate = 3600000L)
    public void run() {
        execute();
    }

    /**
     * Execute Update
     */
    public synchronized void execute() {
        final Object o = SpringUtil.getBean(PermissionTask.class).execute();
        if (o instanceof Mono<?> m) {
            m.block();
        }
    }

}
