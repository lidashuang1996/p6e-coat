package club.p6e.coat.permission;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.permission.task.BlockingPermissionAutoRefreshTask;
import club.p6e.coat.permission.task.ReactivePermissionAutoRefreshTask;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Permission Task
 *
 * @author lidashuang
 * @version 1.0
 */
public class PermissionTask {

    /**
     * Permission Task Scheduled Executor Service Object
     */
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "PERMISSION-TASK-THREAD-" + count.getAndIncrement());
        }
    });

    /**
     * Init Task
     */
    @PostConstruct
    public void init() {
        executor.scheduleWithFixedDelay(this::execute, 30L, 3600L, TimeUnit.SECONDS);
    }

    /**
     * Close Task
     */
    @PreDestroy
    public void close() {
        executor.shutdown();
    }

    /**
     * Execute Task
     */
    private void execute() {
        boolean run = false;
        PermissionTaskCallback callback = null;
        if (SpringUtil.exist(PermissionTaskCallback.class)) {
            callback = SpringUtil.getBean(PermissionTaskCallback.class);
        }
        try {
            Class.forName("org.springframework.web.servlet.DispatcherServlet");
            final BlockingPermissionAutoRefreshTask task = SpringUtil.getBean(BlockingPermissionAutoRefreshTask.class);
            if (callback != null) {
                callback.before(task.version());
            }
            task.execute();
            if (callback != null) {
                callback.after(task.version());
            }
            run = true;
        } catch (ClassNotFoundException _) {
            // ignore exception
        }
        if (!run) {
            try {
                Class.forName("org.springframework.web.reactive.DispatcherHandler");
                final ReactivePermissionAutoRefreshTask task = SpringUtil.getBean(ReactivePermissionAutoRefreshTask.class);
                if (callback != null) {
                    callback.before(task.version());
                }
                task.execute().block();
                if (callback != null) {
                    callback.after(task.version());
                }
            } catch (ClassNotFoundException _) {
                // ignore exception
            }
        }
    }

}
