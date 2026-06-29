package club.p6e.coat.permission;

import club.p6e.coat.permission.task.BlockingPermissionAutoRefreshTask;
import club.p6e.coat.permission.task.ReactivePermissionAutoRefreshTask;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Permission Task
 *
 * @author lidashuang
 * @version 1.0
 */
public class PermissionTask {

    /**
     * Permission Task Callback Object
     */
    private final PermissionTaskCallback permissionTaskCallback;

    /**
     * Blocking Permission Auto Refresh Task Object
     */
    private final BlockingPermissionAutoRefreshTask blockingPermissionAutoRefreshTask;

    /**
     * Reactive Permission Auto Refresh Task Object
     */
    private final ReactivePermissionAutoRefreshTask reactivePermissionAutoRefreshTask;

    /**
     * Permission Task Scheduled Executor Service Object
     */
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(
            1, runnable -> new Thread(runnable, "PERMISSION-TASK-THREAD"));

    /**
     * Constructor Initialization
     *
     * @param context Application Context Object
     */
    public PermissionTask(ApplicationContext context) {
        PermissionTaskCallback callback = null;
        BlockingPermissionAutoRefreshTask blockingRefreshTask = null;
        ReactivePermissionAutoRefreshTask reactiveRefreshTask = null;
        try {
            callback = context.getBean(PermissionTaskCallback.class);
        } catch (Exception e) {
            // ignore exception
        }
        try {
            blockingRefreshTask = context.getBean(BlockingPermissionAutoRefreshTask.class);
        } catch (Exception e) {
            // ignore exception
        }
        try {
            reactiveRefreshTask = context.getBean(ReactivePermissionAutoRefreshTask.class);
        } catch (Exception e) {
            // ignore exception
        }
        this.permissionTaskCallback = callback;
        this.blockingPermissionAutoRefreshTask = blockingRefreshTask;
        this.reactivePermissionAutoRefreshTask = reactiveRefreshTask;
    }

    /**
     * Init Task
     */
    @PostConstruct
    public void init() {
        executor.scheduleWithFixedDelay(this::execute, 5L, 3600L, TimeUnit.SECONDS);
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
        if (permissionTaskCallback != null) {
            if (blockingPermissionAutoRefreshTask != null) {
                permissionTaskCallback.before(blockingPermissionAutoRefreshTask.version());
                blockingPermissionAutoRefreshTask.execute();
                permissionTaskCallback.after(blockingPermissionAutoRefreshTask.version());
            }
            if (reactivePermissionAutoRefreshTask != null) {
                permissionTaskCallback.before(reactivePermissionAutoRefreshTask.version());
                reactivePermissionAutoRefreshTask.execute();
                permissionTaskCallback.after(reactivePermissionAutoRefreshTask.version());
            }
        }
    }

}
