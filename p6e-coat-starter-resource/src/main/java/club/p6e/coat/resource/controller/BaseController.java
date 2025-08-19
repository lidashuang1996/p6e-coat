package club.p6e.coat.resource.controller;

import club.p6e.coat.resource.FileReader;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base Controller
 *
 * @author lidashuang
 * @version 1.0
 */
public class BaseController {

    /**
     * Get Download Server Response
     *
     * @param request    Server Request Object
     * @param fileReader File Reader Object
     * @return Server Response Object
     */
    public static Mono<ServerResponse> getDownloadServerResponse(ServerRequest request, FileReader fileReader) {
        return getHttpRangeServerResponse(request.headers().range(), fileReader, headers ->
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="
                        + URLEncoder.encode(fileReader.getFileAttribute().getName(), StandardCharsets.UTF_8)
                )
        );
    }

    /**
     * Get Resource Server Response
     *
     * @param request    Server Request Object
     * @param fileReader File Reader Object
     * @return Server Response Object
     */
    public static Mono<ServerResponse> getResourceServerResponse(ServerRequest request, FileReader fileReader) {
        return getHttpRangeServerResponse(request.headers().range(), fileReader, headers -> {
        });
    }

    /**
     * Get Http Range Server Response
     *
     * @param httpRangeList Http Range List Object
     * @param fileReader    File Reader Object
     * @param headers       Headers Object
     * @return Server Response Object
     */
    public static Mono<ServerResponse> getHttpRangeServerResponse(List<HttpRange> httpRangeList, FileReader fileReader, Consumer<HttpHeaders> headers) {
        final long length = fileReader.getFileAttribute().getLength();
        if (httpRangeList.isEmpty()) {
            return ServerResponse
                    .ok()
                    .contentLength(length)
                    .contentType(fileReader.getFileMediaType())
                    .headers(headers)
                    .body((response, context) -> response.writeWith(fileReader.execute()));
        } else {
            final HttpRange range = httpRangeList.get(0);
            final long el = range.getRangeEnd(length);
            final long sl = range.getRangeStart(length);
            final long cl = el - sl + 1;
            return ServerResponse
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .contentLength(cl)
                    .contentType(fileReader.getFileMediaType())
                    .headers(headers)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + sl + "-" + el + "/" + length)
                    .body((response, context) -> response.writeWith(fileReader.execute(sl, cl)));
        }
    }

    /**
     * Result Context
     */
    @Data
    @Accessors(chain = true)
    public static final class ResultContext implements Serializable {

        /**
         * Default Code
         */
        private static final int DEFAULT_CODE = 0;

        /**
         * Default Message
         */
        private static final String DEFAULT_MESSAGE = "SUCCESS";

        /**
         * Default Data
         */
        private static final String DEFAULT_DATA = null;

        /**
         * Code
         */
        private Integer code;

        /**
         * Message
         */
        private String message;

        /**
         * Data
         */
        private Object data;

        /**
         * Constructor Initializers
         *
         * @param code    Code
         * @param data    Data
         * @param message Message
         */
        private ResultContext(Integer code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        /**
         * Build
         *
         * @return Result Context Object
         */
        public static ResultContext build() {
            return new ResultContext(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_DATA);
        }

        /**
         * Build
         *
         * @param data Data
         * @return Result Context Object
         */
        public static ResultContext build(Object data) {
            return new ResultContext(DEFAULT_CODE, DEFAULT_MESSAGE, data);
        }

        /**
         * Build
         *
         * @param code    Code
         * @param data    Data
         * @param message Message
         * @return Result Context Object
         */
        public static ResultContext build(Integer code, String message, Object data) {
            return new ResultContext(code, message, data);
        }

    }

}
