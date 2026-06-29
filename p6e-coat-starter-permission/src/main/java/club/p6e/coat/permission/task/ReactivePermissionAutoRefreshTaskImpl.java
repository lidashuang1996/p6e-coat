package club.p6e.coat.permission.task;

import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.matcher.PermissionGroupMatcher;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import club.p6e.coat.permission.repository.ReactivePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Reactive Permission Auto Refresh Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@ConditionalOnMissingBean(ReactivePermissionAutoRefreshTask.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactivePermissionAutoRefreshTaskImpl implements ReactivePermissionAutoRefreshTask {

    /**
     * Reactive Permission Repository Object
     */
    private final ReactivePermissionRepository repository;

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
    private final AtomicLong version = new AtomicLong(0);

    /**
     * Constructor Initialization
     *
     * @param repository             Reactive Permission Repository Object
     * @param permissionPathMatcher  Permission Path Matcher Object
     * @param permissionGroupMatcher Permission Group Matcher Object
     */
    public ReactivePermissionAutoRefreshTaskImpl(
            ReactivePermissionRepository repository,
            PermissionPathMatcher permissionPathMatcher,
            PermissionGroupMatcher permissionGroupMatcher
    ) {
        this.repository = repository;
        this.permissionPathMatcher = permissionPathMatcher;
        this.permissionGroupMatcher = permissionGroupMatcher;
    }

    @Override
    public Mono<Long> execute() {
        final LocalDateTime now = LocalDateTime.now();
        log.info("[ PERMISSION AUTO REFRESH TASK ] => NOW: {}", now);
        log.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION UPDATE TASK");
        return executePermissionPath(this.version.incrementAndGet()).map(l -> {
            this.permissionPathMatcher.cleanExpiredVersionData(this.version.get());
            log.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE PERMISSION UPDATE TASK, COUNT >>> {}, VERSION >>> {}", l, this.version.get());
            return l;
        }).flatMap(_ -> {
            log.info("[ PERMISSION AUTO REFRESH TASK ] START EXECUTE PERMISSION GROUP UPDATE TASK");
            return executePermissionGroup().map(m -> {
                this.permissionGroupMatcher.refresh(m);
                log.info("[ PERMISSION AUTO REFRESH TASK ] COMPLETE EXECUTE PERMISSION GROUP UPDATE TASK");
                return (long) m.size();
            });
        });
    }

    @Override
    public Long version() {
        return this.version.get();
    }

    /**
     * Execute Permission Path Refresh
     *
     * @param version New Version
     * @return Permission Path Data Size
     */
    private Mono<Long> executePermissionPath(long version) {
        return executePermissionPath(version, 1, new AtomicLong(0));
    }

    /**
     * Execute Permission Path Refresh
     *
     * @param version New Version
     * @param page    Page Number
     * @param count   Count Number
     * @return Permission Path Data Size
     */
    private Mono<Long> executePermissionPath(long version, int page, AtomicLong count) {
        return this.repository
                .getPermissionDetailsList(page, 20)
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.just(count.get());
                    }
                    for (final PermissionDetails item : list) {
                        this.permissionPathMatcher.register(item.setVersion(version));
                    }
                    count.addAndGet(list.size());
                    return executePermissionPath(version, page + 1, count);
                });
    }

    /**
     * Execute Permission Group Refresh
     *
     * @return Permission Group Data Object
     */
    private Mono<Map<String, List<String>>> executePermissionGroup() {
        return executePermissionGroup(1, new HashMap<>());
    }

    /**
     * Execute Permission Group Refresh
     *
     * @param page   Page Number
     * @param result Result Object
     * @return Permission Group Data Object
     */
    private Mono<Map<String, List<String>>> executePermissionGroup(int page, Map<String, List<String>> result) {
        return this.repository
                .getPermissionGroupList(page, 20)
                .flatMap(map -> {
                    if (map.isEmpty()) {
                        return Mono.just(result);
                    }
                    result.putAll(map);
                    return executePermissionGroup(page + 1, result);
                });
    }

}
