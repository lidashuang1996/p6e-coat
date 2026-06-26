package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

import java.io.File;

/**
 * File Signature
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileSignature {

    /**
     * Execute File Signature
     *
     * @param file File Object
     * @return File Signature Object
     */
    Mono<String> execute(File file);

    /**
     * Digest Algorithm
     */
    interface DigestAlgorithm {

        /**
         * Input Bytes to Digest Algorithm
         *
         * @param bytes Signature Bytes Object
         */
        void input(byte[] bytes);

        /**
         * Output Digest Algorithm Result
         *
         * @return Signature Bytes Object
         */
        byte[] output();

    }

}
