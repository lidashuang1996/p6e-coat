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
import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger version = new AtomicInteger(0);

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
    public Integer version() {
        return this.version.get();
    }

    /**
     * Execute Permission Path Refresh
     *
     * @param version New Version
     * @return Permission Path Data Size
     */
    private Mono<Long> executePermissionPath(long version) {
        return executePermissionPath(1, 20, 0L, version);
    }

    /**
     * Execute Read Data (Page/Size) Permission Path Data To Permission Path Details List Object
     *
     * @param page    Page
     * @param size    Size
     * @param count   Count
     * @param version Version
     * @return Permission Path Data Size
     */
    private Mono<Long> executePermissionPath(int page, int size, long count, long version) {
        return this
                .repository
                .getPermissionDetailsList(page, size)
                .switchIfEmpty(Mono.just(List.of()))
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.just(count);
                    } else {
                        for (final PermissionDetails item : list) {
                            this.permissionPathMatcher.register(item.setVersion(version));
                        }
                        return executePermissionPath(page + 1, size, count + list.size(), version);
                    }
                });

    }

    /**
     * Execute Permission Group Refresh
     *
     * @return Permission Group Data Object
     */
    private Mono<Map<String, List<String>>> executePermissionGroup() {
        return executePermissionGroup(1, 20, new HashMap<>());
    }

    /**
     * Execute Read Data (Page/Size) Permission Group Data Object
     *
     * @param page   Page
     * @param size   Size
     * @param result Permission Group Data Object
     * @return Permission Group Data Object
     */
    private Mono<Map<String, List<String>>> executePermissionGroup(int page, int size, Map<String, List<String>> result) {
        return this
                .repository
                .getPermissionGroupList(page, size)
                .switchIfEmpty(Mono.just(Map.of()))
                .flatMap(map -> {
                    if (map.isEmpty()) {
                        return Mono.just(result);
                    } else {
                        for (final String key : map.keySet()) {
                            result.put(key, map.get(key));
                        }
                        return executePermissionGroup(page + 1, size, result);
                    }
                });
    }

}
