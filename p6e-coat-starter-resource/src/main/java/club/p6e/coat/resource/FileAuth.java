package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

public interface FileAuth {

    /**
     * Execute File Permission
     *
     * @param type    File Permission Type Object
     * @param voucher Voucher Content Object
     * @return Permission Result Object
     */
    Mono<FileUser> execute(String voucher);


}
