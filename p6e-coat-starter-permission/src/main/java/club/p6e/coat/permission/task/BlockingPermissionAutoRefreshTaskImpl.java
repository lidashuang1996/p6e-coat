package club.p6e.coat.permission.task;

import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import club.p6e.coat.permission.repository.BlockingPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Blocking Permission Auto Refresh Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(BlockingPermissionAutoRefreshTask.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class BlockingPermissionAutoRefreshTaskImpl implements BlockingPermissionAutoRefreshTask {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingPermissionAutoRefreshTaskImpl.class);

    /**
     * Permission Path Matcher Object
     */
    private final PermissionPathMatcher matcher;

    /**
     * Blocking Permission Repository Object
     */
    private final BlockingPermissionRepository repository;

    /**
     * Version Object
     */
    private final AtomicInteger version = new AtomicInteger(1);

    /**
     * Constructor Initializers
     *
     * @param matcher    Permission Path Matcher Object
     * @param repository Blocking Permission Repository Object
     */
    public BlockingPermissionAutoRefreshTaskImpl(PermissionPathMatcher matcher, BlockingPermissionRepository repository) {
        this.matcher = matcher;
        this.repository = repository;
    }

    @Override
    public Long execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] => NOW: {}", now);
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION UPDATE TASK");
        final long result = execute(this.version.incrementAndGet());
        this.matcher.cleanExpiredVersionData(this.version.get());
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE PERMISSION UPDATE TASK, VERSION >>> {}", this.version.get());
        return result;
    }

    /**
     * Execute
     *
     * @param version New Version
     * @return Permission Data Size
     */
    private Long execute(long version) {
        int page = 1;
        long count = 0L;
        List<PermissionDetails> list;
        do {
            list = this.repository.getPermissionDetailsList(page++, 20);
            count += list.size();
            for (final PermissionDetails item : list) {
                this.matcher.register(item.setVersion(version));
            }
        } while (!list.isEmpty());
        return count;
    }

}
