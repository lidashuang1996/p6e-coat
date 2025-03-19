package club.p6e.coat.permission.web;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.PermissionRepository;
import club.p6e.coat.permission.PermissionTask;
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
 * Permission Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PermissionTask.class,
        ignored = PermissionTaskImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class PermissionTaskImpl implements PermissionTask {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionTaskImpl.class);

    /**
     * Version
     */
    private final AtomicInteger version = new AtomicInteger(1);

    @Override
    public Long execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[ PERMISSION TASK ] ==> now: {}", now);
        LOGGER.info("[ PERMISSION TASK ] start execute permission update task.");
        final long result = execute(version.incrementAndGet());
        SpringUtil.getBean(PermissionPathMatcher.class).cleanExpiredVersionData(version.get() - 1);
        LOGGER.info("[ PERMISSION TASK ] complete the task of execute permission updates.");
        return result;
    }

    private Long execute(long version) {
        final List<PermissionDetails> list = new ArrayList<>();
        execute(1, 20, list);
        LOGGER.info("[ PERMISSION TASK ] successfully read data, list data >>> [{}].", list.size());
        list.forEach(item -> SpringUtil.getBean(PermissionPathMatcher.class).register(item.setVersion(version)));
        return (long) list.size();
    }

    @SuppressWarnings("ALL")
    private void execute(int page, int size, List<PermissionDetails> list) {
        final PermissionRepository repository = SpringUtil.getBean(PermissionRepository.class);
        final Object data = repository.getPermissionDetailsList(page, size);
        if (data instanceof final List<?> tmp) {
            for (final Object item : tmp) {
                if (item instanceof final PermissionDetails details) {
                    list.add(details);
                }
            }
        }
    }

}
