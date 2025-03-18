//package club.p6e.coat.permission.web;
//
//import club.p6e.coat.common.utils.SpringUtil;
//import club.p6e.coat.permission.PermissionDetails;
//import club.p6e.coat.permission.PermissionRepository;
//import club.p6e.coat.permission.PermissionTask;
//import club.p6e.coat.permission.matcher.PermissionPathMatcher;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Permission Task Impl
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = PermissionTask.class,
//        ignored = PermissionTaskImpl.class
//)
//public class PermissionTaskImpl implements PermissionTask {
//
//    /**
//     * Inject log object
//     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionTaskImpl.class);
//
//    /**
//     * Version
//     */
//    private final AtomicInteger version = new AtomicInteger(1);
//
//    @Override
//    public Mono<Long> execute() {
//        final LocalDateTime now = LocalDateTime.now();
//        LOGGER.info("[ PERMISSION TASK ] ==> now: {}", now);
//        LOGGER.info("[ PERMISSION TASK ] start execute permission update task.");
//        return execute(version.incrementAndGet()).map(l -> {
//            SpringUtil.getBean(PermissionPathMatcher.class).cleanExpiredVersionData(version.get() - 1);
//            LOGGER.info("[ PERMISSION TASK ] complete the task of execute permission updates.");
//            return l;
//        });
//    }
//
//    private Mono<Long> execute(long version) {
//        return execute(1, 20, new ArrayList<>()).map(list -> {
//            LOGGER.info("[ PERMISSION TASK ] successfully read data, list data >>> [{}].", list.size());
//            list.forEach(item -> SpringUtil.getBean(PermissionPathMatcher.class).register(item.setVersion(version)));
//            return Long.valueOf(list.size());
//        });
//    }
//
//    private Mono<List<PermissionDetails>> execute(int page, int size, List<PermissionDetails> list) {
//        final PermissionRepository repository = SpringUtil.getBean(PermissionRepository.class);
//        return repository.getPermissionDetailsList(page, size).flatMap(l -> {
//            list.addAll(l);
//            return l.isEmpty() ? Mono.just(l) : execute(page + 1, size, list);
//        }).switchIfEmpty(Mono.just(list));
//    }
//
//}
