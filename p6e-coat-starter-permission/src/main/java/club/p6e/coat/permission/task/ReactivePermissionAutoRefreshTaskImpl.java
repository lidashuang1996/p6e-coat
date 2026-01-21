package club.p6e.coat.permission.task;

import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import club.p6e.coat.permission.repository.ReactivePermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reactive Permission Auto Refresh Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ReactivePermissionAutoRefreshTask.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class ReactivePermissionAutoRefreshTaskImpl implements ReactivePermissionAutoRefreshTask {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactivePermissionAutoRefreshTaskImpl.class);

    /**
     * Permission Path Matcher Object
     */
    private final PermissionPathMatcher matcher;

    /**
     * Reactive Permission Repository Object
     */
    private final ReactivePermissionRepository repository;

    /**
     * Version Object
     */
    private final AtomicInteger version = new AtomicInteger(1);

    /**
     * Constructor Initialization
     *
     * @param matcher    Permission Path Matcher Object
     * @param repository Blocking Permission Repository Object
     */
    public ReactivePermissionAutoRefreshTaskImpl(PermissionPathMatcher matcher, ReactivePermissionRepository repository) {
        this.matcher = matcher;
        this.repository = repository;
    }

    @Override
    public Mono<Long> execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] => NOW: {}", now);
        LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION UPDATE TASK");
        return execute(this.version.incrementAndGet()).map(l -> {
            this.matcher.cleanExpiredVersionData(this.version.get());
            LOGGER.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE PERMISSION UPDATE TASK, VERSION >>> {}", this.version.get());
            return l;
        });
    }

    /**
     * Execute
     *
     * @param version New Version
     * @return Permission Data Size
     */
    private Mono<Long> execute(long version) {
        return execute(1, 20, 0L, version);
    }

    /**
     * Execute Read Data (Page/Size) Permission Data To Permission Details List Object
     *
     * @param page    Page
     * @param size    Size
     * @param count   Count
     * @param version Version
     * @return Permission Data Size
     */
    private Mono<Long> execute(int page, int size, long count, long version) {
        return this
                .repository
                .getPermissionDetailsList(page, size)
                .switchIfEmpty(Mono.just(List.of()))
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.just(count);
                    } else {
                        for (final PermissionDetails item : list) {
                            this.matcher.register(item.setVersion(version));
                        }
                        return execute(page + 1, size, count + list.size(), version);
                    }
                });

    }

}
