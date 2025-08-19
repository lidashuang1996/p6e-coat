package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * File Permission Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(FilePermissionService.class)
public class FilePermissionServiceImpl implements FilePermissionService {

    @Override
    public Mono<Boolean> execute(FilePermissionType type, String voucher) {
        return Mono.just(true);
    }

}
