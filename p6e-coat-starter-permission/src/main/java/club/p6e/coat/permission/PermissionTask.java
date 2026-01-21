package club.p6e.coat.permission;

import club.p6e.coat.permission.task.BlockingPermissionAutoRefreshTask;
import club.p6e.coat.permission.task.ReactivePermissionAutoRefreshTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
     * Blocking Permission Auto Refresh Task Object
     */
    private final BlockingPermissionAutoRefreshTask blockingPermissionAutoRefreshTask;

    /**
     * Reactive Permission Auto Refresh Task Object
     */
    private final ReactivePermissionAutoRefreshTask reactivePermissionAutoRefreshTask;

    /**
     * Constructor Initialization
     *
     * @param blockingPermissionAutoRefreshTask Blocking Permission Auto Refresh Task Object
     * @param reactivePermissionAutoRefreshTask Reactive Permission Auto Refresh Task Object
     */
    public PermissionTask(BlockingPermissionAutoRefreshTask blockingPermissionAutoRefreshTask, ReactivePermissionAutoRefreshTask reactivePermissionAutoRefreshTask) {
        this.blockingPermissionAutoRefreshTask = blockingPermissionAutoRefreshTask;
        this.reactivePermissionAutoRefreshTask = reactivePermissionAutoRefreshTask;
    }

    @Scheduled(initialDelay = 10000L, fixedRate = 3600000L)
    public void run() {
        execute();
    }

    /**
     * Execute Task
     */
    public synchronized void execute() {
        boolean run = false;
        try {
            Class.forName("org.springframework.web.servlet.package-info");
            blockingPermissionAutoRefreshTask.execute();
            run = true;
        } catch (ClassNotFoundException e) {
            // ignore exception
        }
        if (!run) {
            try {
                Class.forName("org.springframework.web.reactive.package-info");
                reactivePermissionAutoRefreshTask.execute().block();
            } catch (ClassNotFoundException e) {
                // ignore exception
            }
        }
    }

}
