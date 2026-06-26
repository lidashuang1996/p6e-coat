package club.p6e.coat.resource;

import club.p6e.coat.resource.error.ResourcePathException;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Mono;

import java.io.File;
import java.security.MessageDigest;

/**
 * Simple File Signature
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(SimpleFileSignature.class)
public class SimpleFileSignature implements FileSignature {

    /**
     * Hex Chars Array
     */
    private static final char[] HEX_CHARS = new char[]
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * File Buffer Size
     */
    private static final int FILE_BUFFER_SIZE = 1024 * 1024 * 5;

    /**
     * Default Data Buffer Factory
     */
    private static final DefaultDataBufferFactory DEFAULT_DATA_BUFFER_FACTORY = new DefaultDataBufferFactory();


    @Override
    public Mono<String> execute(File file) {
        final FileSignature.DigestAlgorithm digestAlgorithm = new DefaultDigestAlgorithm();
        return Mono.just(file)
                .flatMap(f -> FileUtil.checkFileExist(f) ? Mono.just(f) : Mono.error(new ResourcePathException(
                        SimpleFileSignature.class,
                        "fun execute(File file)",
                        "resource path file <" + file.getName() + "> exception"
                )))
                .flatMap(f -> DataBufferUtils
                        .read(new FileSystemResource(f), DEFAULT_DATA_BUFFER_FACTORY, FILE_BUFFER_SIZE)
                        .flatMap(buffer -> {
                            try {
                                final int count = buffer.readableByteCount();
                                final byte[] bytes = new byte[count];
                                buffer.read(bytes);
                                digestAlgorithm.input(bytes);
                                return Mono.just(count);
                            } finally {
                                DataBufferUtils.release(buffer);
                            }
                        }).count())
                .map(_ -> digestAlgorithmBytesToHexString(digestAlgorithm.output()));
    }

    /**
     * Digest Algorithm Bytes To Hex String
     *
     * @param bytes Digest Algorithm Bytes Object
     * @return Hex String Object
     */
    private String digestAlgorithmBytesToHexString(byte[] bytes) {
        final char[] chars = new char[32];
        for (int i = 0; i < chars.length; i += 2) {
            final byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[b >>> 4 & 15];
            chars[i + 1] = HEX_CHARS[b & 15];
        }
        return new String(chars);
    }

    /**
     * Default Digest Algorithm
     */
    private static class DefaultDigestAlgorithm implements FileSignature.DigestAlgorithm {

        /**
         * Message Digest Object
         */
        private final MessageDigest md;

        /**
         * Constructor Initialization
         */
        public DefaultDigestAlgorithm() {
            try {
                this.md = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void input(byte[] bytes) {
            md.update(bytes);
        }

        @Override
        public byte[] output() {
            return md.digest();
        }

    }

}
