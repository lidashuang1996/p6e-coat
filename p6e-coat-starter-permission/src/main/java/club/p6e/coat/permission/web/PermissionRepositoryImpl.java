//package club.p6e.coat.permission.web.reactive;
//
//import club.p6e.coat.permission.PermissionDetails;
//import club.p6e.coat.permission.PermissionRepository;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.r2dbc.core.DatabaseClient;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Permission Repository Impl
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = PermissionRepository.class,
//        ignored = PermissionRepositoryImpl.class
//)
//public class PermissionRepositoryImpl implements PermissionRepository {
//
//    /**
//     * Database Client Object
//     */
//    private final DatabaseClient client;
//
//    /**
//     * Constructor Initializers
//     *
//     * @param client Database Client Object
//     */
//    public PermissionRepositoryImpl(DatabaseClient client) {
//        this.client = client;
//    }
//
//    @Override
//    public Mono<List<PermissionDetails>> getPermissionDetailsList(Integer page, Integer size) {
//        return Mono.just(new ArrayList<>());
//    }
//
//}
