package club.p6e.coat.permission.web.reactive;

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
import reactor.core.publisher.Mono;

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
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class PermissionAutoRefreshTaskImpl implements club.p6e.coat.permission.PermissionAutoRefreshTask {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAutoRefreshTaskImpl.class);

    /**
     * Version
     */
    private final AtomicInteger version = new AtomicInteger(1);

    @Override
    public Mono<Long> execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] => NOW: {}", now);
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION UPDATE TASK...");
        return execute(version.incrementAndGet()).map(l -> {
            SpringUtil.getBean(PermissionPathMatcher.class).cleanExpiredVersionData(version.get() - 1);
            LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE PERMISSION UPDATE TASK.");
            return l;
        });
    }

    /**
     * Execute
     * 1. Read All Permission Data
     * 2. Register Permission Data To Permission Path Matcher
     *
     * @param version New Version
     * @return Permission Data Size
     */
    private Mono<Long> execute(long version) {
        return execute(1, 20, new ArrayList<>()).map(list -> {
            LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] SUCCESSFULLY READ ALL DATA COMPLETED, LIST DATA SIZE >>> {}.", list.size());
            list.forEach(i -> SpringUtil.getBean(PermissionPathMatcher.class).register(i.setVersion(version)));
            return Long.valueOf(list.size());
        });
    }

    /**
     * Execute Read Data (Page/Size) Permission Data To Permission Details List Object
     *
     * @param page Page
     * @param size Size
     * @param list Permission Details List Object
     * @return Permission Details List Mono Object
     */
    private Mono<List<PermissionDetails>> execute(int page, int size, List<PermissionDetails> list) {
        final PermissionRepository repository = SpringUtil.getBean(PermissionRepository.class);
        final Object data = repository.getPermissionDetailsList(page, size);
        if (data instanceof final Mono<?> mono) {
            return mono.flatMap(o -> {
                final List<PermissionDetails> tmp = new ArrayList<>();
                if (o instanceof List<?> l) {
                    for (final Object i : l) {
                        if (i instanceof PermissionDetails details) {
                            tmp.add(details);
                        }
                    }
                }
                list.addAll(tmp);
                return tmp.isEmpty() ? Mono.just(list) : execute(page + 1, size, list);
            }).switchIfEmpty(Mono.just(list));
        } else {
            return Mono.just(list);
        }
    }

}
