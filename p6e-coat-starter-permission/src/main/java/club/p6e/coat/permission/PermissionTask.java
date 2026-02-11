package club.p6e.coat.permission;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.permission.task.BlockingPermissionAutoRefreshTask;
import club.p6e.coat.permission.task.ReactivePermissionAutoRefreshTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

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

    /**
     * Counter Object
     */
    private final AtomicLong counter = new AtomicLong(0);

    @Scheduled(initialDelay = 10000L, fixedRate = 3600000L)
    public void run() {
        execute();
    }

    /**
     * Execute Task
     */
    public synchronized void execute() {
        boolean run = false;
        PermissionTaskCallback callback = null;
        if (SpringUtil.exist(PermissionTaskCallback.class)) {
            callback = SpringUtil.getBean(PermissionTaskCallback.class);
        }
        final long num = counter.getAndIncrement();
        if (callback != null) {
            callback.before(num);
        }
        try {
            Class.forName("org.springframework.web.servlet.package-info");
            SpringUtil.getBean(BlockingPermissionAutoRefreshTask.class).execute();
            run = true;
        } catch (ClassNotFoundException e) {
            // ignore exception
        }
        if (!run) {
            try {
                Class.forName("org.springframework.web.reactive.package-info");
                SpringUtil.getBean(ReactivePermissionAutoRefreshTask.class).execute().block();
            } catch (ClassNotFoundException e) {
                // ignore exception
            }
        }
        if (callback != null) {
            callback.after(num);
        }
    }

}
