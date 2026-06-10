package club.p6e.coat.permission.task;

import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.matcher.PermissionGroupMatcher;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import club.p6e.coat.permission.repository.BlockingPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Blocking Permission Auto Refresh Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@ConditionalOnMissingBean(BlockingPermissionAutoRefreshTask.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingPermissionAutoRefreshTaskImpl implements BlockingPermissionAutoRefreshTask {

    /**
     * Blocking Permission Repository Object
     */
    private final BlockingPermissionRepository repository;

    /**
     * Permission Path Matcher Object
     */
    private final PermissionPathMatcher permissionPathMatcher;

    /**
     * Permission Group Matcher Object
     */
    private final PermissionGroupMatcher permissionGroupMatcher;

    /**
     * Version Object
     */
    private final AtomicInteger version = new AtomicInteger(0);

    /**
     * Constructor Initialization
     *
     * @param repository             Blocking Permission Repository Object
     * @param permissionPathMatcher  Permission Path Matcher Object
     * @param permissionGroupMatcher Permission Group Matcher Object
     */
    public BlockingPermissionAutoRefreshTaskImpl(
            BlockingPermissionRepository repository,
            PermissionPathMatcher permissionPathMatcher,
            PermissionGroupMatcher permissionGroupMatcher
    ) {
        this.repository = repository;
        this.permissionPathMatcher = permissionPathMatcher;
        this.permissionGroupMatcher = permissionGroupMatcher;
    }

    @Override
    public Long execute() {
        final LocalDateTime now = LocalDateTime.now();
        log.info("[ PERMISSION AUTO REFRESH TASK ] => NOW: {}", now);
        log.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION PATH UPDATE TASK");
        final long result = executePermissionPath(this.version.incrementAndGet());
        this.permissionPathMatcher.cleanExpiredVersionData(this.version.get());
        log.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE PERMISSION PATH UPDATE TASK, COUNT >>> {}, VERSION >>> {}", result, this.version.get());
        log.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION GROUP UPDATE TASK");
        executePermissionGroup();
        log.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE EXECUTE PERMISSION GROUP UPDATE TASK");
        return result;
    }

    @Override
    public Integer version() {
        return this.version.get();
    }

    /**
     * Execute Permission Path Refresh
     *
     * @param version New Version
     * @return Permission Path Data Size
     */
    private Long executePermissionPath(long version) {
        int page = 1;
        long count = 0L;
        List<PermissionDetails> list;
        do {
            list = this.repository.getPermissionDetailsList(page++, 20);
            count += list.size();
            for (final PermissionDetails item : list) {
                this.permissionPathMatcher.register(item.setVersion(version));
            }
        } while (!list.isEmpty());
        return count;
    }

    /**
     * Execute Permission Group Refresh
     */
    private void executePermissionGroup() {
        int page = 1;
        Map<String, List<String>> temporary;
        final Map<String, List<String>> data = new HashMap<>();
        do {
            temporary = this.repository.getPermissionGroupList(page++, 20);
            for (final String key : temporary.keySet()) {
                data.put(key, temporary.get(key));
            }
        } while (!temporary.isEmpty());
        this.permissionGroupMatcher.refresh(data);
    }

}
