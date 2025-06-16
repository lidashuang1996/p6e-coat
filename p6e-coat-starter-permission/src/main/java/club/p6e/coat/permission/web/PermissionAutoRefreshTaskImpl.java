package club.p6e.coat.permission.web;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.permission.PermissionAutoRefreshTask;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.PermissionRepository;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Permission Auto Refresh Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(PermissionAutoRefreshTask.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class PermissionAutoRefreshTaskImpl implements PermissionAutoRefreshTask {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAutoRefreshTaskImpl.class);

    /**
     * Version
     */
    private final AtomicInteger version = new AtomicInteger(1);

    @Override
    public Long execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] => NOW: {}", now);
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION UPDATE TASK...");
        final long result = execute(version.incrementAndGet());
        SpringUtil.getBean(PermissionPathMatcher.class).cleanExpiredVersionData(version.get() - 1);
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE PERMISSION UPDATE TASK.");
        return result;
    }

    /**
     * Execute
     * 1. Read All Permission Data
     * 2. Register Permission Data To Permission Path Matcher
     *
     * @param version New Version
     * @return Permission Data Size
     */
    private Long execute(long version) {
        final List<PermissionDetails> list = new ArrayList<>();
        execute(1, 20, list);
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] SUCCESSFULLY READ ALL DATA COMPLETED, LIST DATA SIZE >>> {}.", list.size());
        list.forEach(i -> SpringUtil.getBean(PermissionPathMatcher.class).register(i.setVersion(version)));
        return (long) list.size();
    }

    /**
     * Execute Read Data (Page/Size) Permission Data To Permission Details List Object
     *
     * @param page        Page
     * @param size        Size
     * @param permissions Permission Details List Object
     */
    private void execute(int page, int size, List<PermissionDetails> permissions) {
        final PermissionRepository repository = SpringUtil.getBean(PermissionRepository.class);
        final Object data = repository.getPermissionDetailsList(page, size);
        if (data instanceof final List<?> list) {
            final List<PermissionDetails> tmp = new ArrayList<>();
            for (final Object item : list) {
                if (item instanceof final PermissionDetails details) {
                    tmp.add(details);
                }
            }
            permissions.addAll(tmp);
            if (!tmp.isEmpty()) {
                execute(page + 1, size, permissions);
            }
        }
    }

}
