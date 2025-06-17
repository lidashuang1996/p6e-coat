package club.p6e.cloud.gateway;

import club.p6e.coat.common.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic Web Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BasicWebFilter.class)
public class BasicWebFilter implements WebFilter, Ordered {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicWebFilter.class);

    /**
     * Data Buffer Factory Object
     */
    private static final DataBufferFactory DATA_BUFFER_FACTORY = new DefaultDataBufferFactory();

    /**
     * Order
     */
    private static final int ORDER = Integer.MIN_VALUE + 3000;

    /**
     * User Info Header Name
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * Only Response Header
     */
    private static final String[] ONLY_RESPONSE_HEADERS = new String[]{
            "Content-Type",
            "Access-Control",
            "Access-Control-Max-Age",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Credentials"
    };

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public BasicWebFilter(Properties properties) {
        this.properties = properties;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final Model model = new Model();
        final ServerHttpRequest request = new CustomServerHttpRequestDecorator(exchange, properties, model);
        final ServerHttpResponse response = new CustomServerHttpResponseDecorator(exchange, properties, model);
        return chain.filter(exchange.mutate().request(request).response(response).build());
    }

    /**
     * Log Model
     */
    @Data
    @Accessors(chain = true)
    private static class Model implements Serializable {
        private volatile String id;
        private volatile String path;
        private volatile LocalDateTime requestDateTime;
        private volatile String requestMethod;
        private volatile String requestCookies;
        private volatile String requestHeaders;
        private volatile String requestBody;
        private volatile String requestQueryParams;
        private volatile String responseBody;
        private volatile String responseHeaders;
        private volatile String responseCookies;
        private volatile LocalDateTime responseDateTime;
        private volatile long intervalDateTime;
        private volatile String ip;
        private volatile String user;

        public String toJsonString() {
            return JsonUtil.toJson(this);
        }

    }

    /**
     * Custom Server Http Request Decorator
     */
    private static class CustomServerHttpRequestDecorator extends ServerHttpRequestDecorator {

        /**
         * IP Request Header
         */
        @SuppressWarnings("ALL")
        private static final String LOCAL_IP = "127.0.0.1";
        /**
         * IP Request Header
         */
        @SuppressWarnings("ALL")
        private final static String IP_UNKNOWN = "unknown";
        /**
         * IP Request Header
         */
        @SuppressWarnings("ALL")
        private static final String IP_HEADER_X_REQUEST_IP = "x-request-ip";
        /**
         * IP Request Header
         */
        @SuppressWarnings("ALL")
        private static final String IP_HEADER_X_FORWARDED_FOR = "x-forwarded-for";
        /**
         * IP Request Header
         */
        @SuppressWarnings("ALL")
        private static final String IP_HEADER_PROXY_CLIENT_IP = "proxy-client-ip";
        /**
         * IP Request Header
         */
        @SuppressWarnings("ALL")
        private static final String IP_HEADER_WL_PROXY_CLIENT_IP = "wl-proxy-client-ip";
        /**
         * Log Mode Object
         */
        private final Model model;
        /**
         * Properties Object
         */
        private final Properties properties;
        /**
         * Server Http Request Object
         */
        private final ServerHttpRequest request;

        /**
         * Constructor Initialization
         *
         * @param exchange   Server Web Exchange Object
         * @param properties Properties Object
         * @param model      Log Mode Object
         */
        public CustomServerHttpRequestDecorator(ServerWebExchange exchange, Properties properties, Model model) {
            super(exchange.getRequest());
            this.model = model;
            this.properties = properties;
            this.request = exchange.getRequest();

            // request header is used internally for calling
            // prohibit sending requests that carry this request header to downstream services
            for (final String key : this.getHeaders().keySet()) {
                if (key.toLowerCase().startsWith("p6e-")) {
                    this.getHeaders().remove(key);
                }
            }

            // log info
            if (properties.getLog().isEnable()) {
                model.setIp(ip(request));
                model.setId(request.getId());
                model.setPath(request.getPath().value());
                model.setRequestDateTime(LocalDateTime.now());
                model.setRequestMethod(request.getMethod().name());
                model.setRequestCookies(JsonUtil.toJson(request.getCookies()));
                model.setRequestHeaders(JsonUtil.toJson(request.getHeaders()));
                model.setRequestQueryParams(JsonUtil.toJson(request.getQueryParams()));
            }
        }

        @NonNull
        @Override
        public Flux<DataBuffer> getBody() {
            if (properties.getLog().isEnable()) {
                return DataBufferUtils.join(super.getBody())
                        .map(buffer -> {
                            final byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);
                            DataBufferUtils.release(buffer);
                            return bytes;
                        })
                        .defaultIfEmpty(new byte[0])
                        .map(bytes -> {
                            final Map<String, String> rBodyMap = new HashMap<>(3);
                            final List<String> types = request.getHeaders().get(HttpHeaders.CONTENT_TYPE);
                            if (types == null || types.isEmpty()) {
                                rBodyMap.put("type", "unknown");
                                rBodyMap.put("size", String.valueOf(bytes.length));
                            } else {
                                final String type = types.get(0);
                                rBodyMap.put("type", type);
                                rBodyMap.put("size", String.valueOf(bytes.length));
                                final byte[] content = new byte[Math.min(bytes.length, 1024 * 30)];
                                System.arraycopy(bytes, 0, content, 0, content.length);
                                // if the requested type is JSON/FORM, then print all the information
                                if (type.startsWith(MediaType.APPLICATION_JSON_VALUE)
                                        || type.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                                    rBodyMap.put("content", new String(content, StandardCharsets.UTF_8)
                                            .replaceAll("\r", "").replaceAll("\n", ""));
                                } else {
                                    rBodyMap.put("content", new String(content, StandardCharsets.UTF_8));
                                }
                            }
                            model.setRequestBody(JsonUtil.toJson(rBodyMap));
                            return DATA_BUFFER_FACTORY.wrap(bytes);
                        })
                        .flux();
            } else {
                return super.getBody();
            }
        }

        /**
         * IP
         *
         * @param request Server Http Request Object
         * @return IP
         */
        public String ip(ServerHttpRequest request) {
            final HttpHeaders httpHeaders = request.getHeaders();
            List<String> list = httpHeaders.get(IP_HEADER_X_FORWARDED_FOR);
            if (list == null || list.isEmpty() || IP_UNKNOWN.equalsIgnoreCase(list.get(0))) {
                list = httpHeaders.get(IP_HEADER_PROXY_CLIENT_IP);
            }
            if (list == null || list.isEmpty() || IP_UNKNOWN.equalsIgnoreCase(list.get(0))) {
                list = httpHeaders.get(IP_HEADER_WL_PROXY_CLIENT_IP);
            }
            if (list == null || list.isEmpty() || IP_UNKNOWN.equalsIgnoreCase(list.get(0))) {
                list = httpHeaders.get(IP_HEADER_X_REQUEST_IP);
            }
            if (list == null || list.isEmpty() || IP_UNKNOWN.equalsIgnoreCase(list.get(0))) {
                final InetSocketAddress inetSocketAddress = request.getRemoteAddress();
                if (inetSocketAddress != null
                        && inetSocketAddress.getAddress() != null
                        && inetSocketAddress.getAddress().getHostAddress() != null) {
                    final String inetSocketAddressHost = inetSocketAddress.getAddress().getHostAddress();
                    if (LOCAL_IP.equals(inetSocketAddressHost)) {
                        try {
                            list = List.of(InetAddress.getLocalHost().getHostAddress());
                        } catch (Exception e) {
                            LOGGER.error("[ LOG IP ERROR] >>> {}", e.getMessage());
                        }
                    } else {
                        list = List.of(inetSocketAddressHost);
                    }
                }
            }
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            return IP_UNKNOWN;
        }
    }

    /**
     * Custom Server Http Response Decorator
     */
    private static class CustomServerHttpResponseDecorator extends ServerHttpResponseDecorator {

        /**
         * Mode object
         */
        private final Model model;

        /**
         * Properties object
         */
        private final Properties properties;

        /**
         * ServerHttpRequest object
         */
        private final ServerHttpRequest request;

        /**
         * ServerHttpResponse object
         */
        private final ServerHttpResponse response;

        /**
         * Constructor Initialization
         *
         * @param exchange   Server WebExchange object
         * @param model      Mode object
         * @param properties Properties object
         */
        public CustomServerHttpResponseDecorator(ServerWebExchange exchange, Properties properties, Model model) {
            super(exchange.getResponse());
            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();
            this.model = model;
            this.request = request;
            this.response = response;
            this.properties = properties;
        }

        @Override
        public @NonNull
        Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
            // only response headers
            final HttpHeaders httpHeaders = response.getHeaders();
            for (final String key : httpHeaders.keySet()) {
                final List<String> value = httpHeaders.get(key);
                if (value != null && value.size() > 1) {
                    for (final String item : ONLY_RESPONSE_HEADERS) {
                        if (key.equalsIgnoreCase(item)) {
                            httpHeaders.set(key, value.get(0));
                        }
                    }
                }
            }
            if (properties.getLog().isEnable()) {
                return super.writeWith(DataBufferUtils.join(body)
                        .map(buffer -> {
                            final byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);
                            DataBufferUtils.release(buffer);
                            return bytes;
                        })
                        .defaultIfEmpty(new byte[0])
                        .flatMap(bytes -> {
                            final Map<String, String> rBodyMap = new HashMap<>(3);
                            final List<String> types = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);
                            if (types == null || types.isEmpty()) {
                                rBodyMap.put("type", "unknown");
                                rBodyMap.put("size", String.valueOf(bytes.length));
                            } else {
                                final byte[] content = new byte[Math.min(bytes.length, 10240)];
                                System.arraycopy(bytes, 0, content, 0, content.length);
                                final String type = types.get(0);
                                rBodyMap.put("type", type);
                                rBodyMap.put("size", String.valueOf(bytes.length));
                                // if the requested type is JSON/FORM, then print all the information
                                if (type.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                                    rBodyMap.put("content", new String(content, StandardCharsets.UTF_8)
                                            .replaceAll("\r", "").replaceAll("\n", ""));
                                } else {
                                    rBodyMap.put("content", new String(content, StandardCharsets.UTF_8));
                                }
                            }
                            model.setResponseDateTime(LocalDateTime.now());
                            model.setResponseBody(JsonUtil.toJson(rBodyMap));
                            model.setResponseHeaders(JsonUtil.toJson(response.getHeaders()));
                            model.setResponseCookies(JsonUtil.toJson(response.getCookies()));
                            // retrieve the latest user information from the request header
                            // ===== USER INFO ========================================
                            final List<String> userInfoData = request.getHeaders().get(USER_INFO_HEADER);
                            if (userInfoData != null) {
                                model.setUser(JsonUtil.toJson(userInfoData));
                            }
                            // ===== USER INFO ========================================
                            if (model.getRequestDateTime() != null && model.getResponseDateTime() != null) {
                                final long s = model.getRequestDateTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
                                final long e = model.getResponseDateTime().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
                                // time interval for writing requests
                                model.setIntervalDateTime(e - s);
                            }
                            // output logs according to configuration
                            if (properties.getLog().isDetails()) {
                                LOGGER.info(model.toJsonString());
                            } else {
                                LOGGER.info("{} >>> [{}] {} ::: {}",
                                        model.getIp(),
                                        model.getRequestMethod(),
                                        model.getPath(),
                                        model.getIntervalDateTime()
                                );
                            }
                            return Mono.just(DATA_BUFFER_FACTORY.wrap(bytes));
                        }));
            } else {
                return super.writeWith(body);
            }
        }
    }

}
