package club.p6e.coat.resource.task;

import club.p6e.coat.resource.Properties;
import club.p6e.coat.resource.utils.FileUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件分片清除任务
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(FileSliceCleanTask.class)
public class FileSliceCleanTask {

    /**
     * Permission Task Scheduled Executor Service Object
     */
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "FILE-SLICE-TASK-THREAD-" + count.getAndIncrement());
        }
    });

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public FileSliceCleanTask(Properties properties) {
        this.properties = properties;
    }

    /**
     * Init
     */
    @PostConstruct
    public void init() {
        executor.scheduleWithFixedDelay(this::execute, 10L, 12 * 3600L, TimeUnit.SECONDS);
    }

    /**
     * Close
     */
    @PreDestroy
    public void close() {
        executor.shutdown();
    }

    /**
     * Execute Task
     */
    public void execute() {
        final Map<String, Properties.Upload> uploads = properties.getUploads();
        if (!uploads.isEmpty()) {
            for (final String key : uploads.keySet()) {
                final Properties.Upload upload = uploads.get(key);
                if (upload.getSlice() != null && upload.getSlice().getPath() != null) {
                    final File folder = new File(upload.getSlice().getPath());
                    if (folder.exists()) {
                        final File[] deletes = folder.listFiles((f, _) ->
                                f.isDirectory() && f.lastModified() < System.currentTimeMillis() - 1000 * 3600 * 24);
                        if (deletes != null) {
                            for (final File delete : deletes) {
                                FileUtil.deleteFolder(delete);
                            }
                        }
                    }
                }
            }
        }
    }

}
