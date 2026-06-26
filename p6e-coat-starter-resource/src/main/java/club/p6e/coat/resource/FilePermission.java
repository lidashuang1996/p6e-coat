package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

/**
 * File Permission
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FilePermission {

    /**
     * Execute File Permission
     *
     * @param type    File Permission Type Object
     * @param voucher Voucher Content Object
     * @return Permission Result Object
     */
    Mono<Boolean> execute(FilePermissionType type, FileUser user);

}
