package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import reactor.core.publisher.Mono;

/**
 * Simple File Permission Service
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(FilePermissionService.class)
public class SimpleFilePermissionService implements FilePermissionService {

    @Override
    public Mono<Boolean> execute(FilePermissionType type, String voucher) {
        if (voucher == null || voucher.isEmpty()) {
            return Mono.just(false);
        }
        return Mono.just(true);
    }

}
