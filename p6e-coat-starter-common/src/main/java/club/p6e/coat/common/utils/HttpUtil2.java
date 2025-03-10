//package club.p6e.coat.common.utils;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.reactive.ClientHttpRequest;
//import org.springframework.web.reactive.function.BodyInserter;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author lidashuang
// * @version 1.0
// */
//@SuppressWarnings("ALL")
//public final class HttpUtil2 {
//
//    /**
//     * 定义类
//     */
//    public interface Definition {
//
//        /**
//         * 获取 IP 信息
//         *
//         * @return IP 信息
//         */
//        public Mono<String> ip(ServerWebExchange exchange);
//
//        /**
//         * 获取设备信息
//         *
//         * @return 设备信息
//         */
//        public Mono<String> device(ServerWebExchange exchange);
//
//        /**
//         * 获取客户端的信息
//         *
//         * @return 客户端的信息
//         */
//        public Mono<String> client(ServerWebExchange exchange);
//
//        /**
//         * 获取浏览器的信息
//         *
//         * @return 浏览器的信息
//         */
//        public Mono<String> browser(ServerWebExchange exchange);
//
//        /**
//         * 发送 GET 请求
//         *
//         * @param url     请求的 URL
//         * @param headers 请求头
//         * @param params  请求的参数
//         * @return 请求结果
//         */
//        public Mono<String> doGet(
//                String url,
//                Map<String, String> headers,
//                Map<String, String> params
//        );
//
//        /**
//         * 发送 POST 请求
//         *
//         * @param url          请求的 URL
//         * @param headers      请求头
//         * @param bodyInserter 请求的参数 (BODY)
//         * @return 请求结果
//         */
//        public Mono<String> doPost(
//                String url,
//                Map<String, String> headers,
//                BodyInserter<?, ? super ClientHttpRequest> bodyInserter
//        );
//    }
//
//    /**
//     * 实现类
//     */
//    public static class Implementation implements Definition {
//
//        /**
//         * 客户端对象
//         */
//        private final WebClient wc;
//
//        private final String secret;
//
//        /**
//         * 构造方法初始化
//         */
//        public Implementation() {
//            this(WebClient.create(), "gZVZ7$UP2AZ@5B6C8JtRzFc%Jtd2WNq3");
//        }
//
//        /**
//         * 构造方法初始化
//         *
//         * @param wc     客户端对象
//         * @param secret JWT 密钥
//         */
//        public Implementation(WebClient wc, String secret) {
//            this.wc = wc;
//            this.secret = secret;
//        }
//
//        @Override
//        public Mono<String> ip(ServerWebExchange exchange) {
//            final HttpHeaders headers = exchange.getRequest().getHeaders();
//            String ipAddress = headers.getFirst("X-Forwarded-For");
//
//            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//                ipAddress = headers.getFirst("Proxy-Client-IP");
//            }
//
//            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//                ipAddress = headers.getFirst("WL-Proxy-Client-IP");
//            }
//
//            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//                ipAddress = headers.getFirst("HTTP_CLIENT_IP");
//            }
//
//            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//                ipAddress = headers.getFirst("HTTP_X_FORWARDED_FOR");
//            }
//
//            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//                ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
//            }
//
//            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//                ipAddress = "0.0.0.0";
//            }
//
//            return Mono.just(ipAddress);
//        }
//
//        @Override
//        public Mono<String> device(ServerWebExchange exchange) {
//            final HttpHeaders headers = exchange.getRequest().getHeaders();
//            String userAgent = headers.getFirst(HttpHeaders.USER_AGENT);
//            if (userAgent != null && !userAgent.isEmpty()) {
//                userAgent = userAgent.toLowerCase();
//                if (userAgent.contains("mozilla") || userAgent.contains("chrome") || userAgent.contains("safari")) {
//                    return Mono.just("pc");
//                } else if (userAgent.contains("android")) {
//                    return Mono.just("android");
//                } else if (userAgent.contains("ios")) {
//                    return Mono.just("ios");
//                } else if (userAgent.contains("client")) {
//                    return Mono.just("client");
//                }
//            }
//            return Mono.just("unknown");
//        }
//
//        @Override
//        public Mono<String> client(ServerWebExchange exchange) {
//            final HttpHeaders headers = exchange.getRequest().getHeaders();
//            final String info = headers.getFirst("P6e-Client-Info");
//            if (info != null && !info.isEmpty()) {
//                // JWT
//                try {
//                    // 必须包含时间
//                    // 不然每一次发送的消息内容是一样的
//                    return Mono.just(JWT.require(Algorithm.HMAC256(secret)).build().verify(info).getSubject());
//                } catch (Exception e) {
//                    return Mono.just("unknown");
//                }
//            }
//            return Mono.just("unknown");
//        }
//
//        @Override
//        public Mono<String> browser(ServerWebExchange exchange) {
//            final HttpHeaders headers = exchange.getRequest().getHeaders();
//            String userAgent = headers.getFirst(HttpHeaders.USER_AGENT);
//            if (userAgent != null && !userAgent.isEmpty()) {
//                return Mono.just(userAgent);
//            }
//            return Mono.just("unknown");
//        }
//
//        public Mono<String> doGet(String url, Map<String, String> headers, Map<String, String> params) {
//            if (params != null) {
//                final StringBuilder sb = new StringBuilder();
//                for (final String key : params.keySet()) {
//                    sb
//                            .append("&")
//                            .append(URLEncoder.encode(key, StandardCharsets.UTF_8))
//                            .append("=")
//                            .append(URLEncoder.encode(params.get(key), StandardCharsets.UTF_8));
//                }
//                if (sb.length() > 0) {
//                    url += "?" + sb.substring(1);
//                }
//            }
//            return this.wc
//                    .get()
//                    .uri(url)
//                    .headers(httpHeaders -> {
//                        if (headers != null) {
//                            for (final String key : headers.keySet()) {
//                                httpHeaders.set(key, headers.get(key));
//                            }
//                        }
//                    })
//                    .retrieve()
//                    .toEntity(String.class)
//                    .flatMap(response -> {
//                        final String body = response.getBody();
//                        final HttpStatusCode status = response.getStatusCode();
//                        if (body == null || status != HttpStatus.OK) {
//                            return Mono.empty();
//                        } else {
//                            return Mono.just(body);
//                        }
//                    });
//        }
//
//
//        public Mono<String> doPost(String url, Map<String, String> headers, BodyInserter<?, ? super ClientHttpRequest> bodyInserter) {
//            return this.wc
//                    .post()
//                    .uri(url)
//                    .headers(httpHeaders -> {
//                        if (headers != null) {
//                            for (final String key : headers.keySet()) {
//                                httpHeaders.set(key, headers.get(key));
//                            }
//                        }
//                    })
//                    .body(bodyInserter)
//                    .retrieve()
//                    .toEntity(String.class)
//                    .flatMap(response -> {
//                        final String body = response.getBody();
//                        final HttpStatusCode status = response.getStatusCode();
//                        if (body == null || status != HttpStatus.OK) {
//                            return Mono.empty();
//                        } else {
//                            return Mono.just(body);
//                        }
//                    });
//        }
//    }
//
//    /**
//     * 默认的模板解析实现对象
//     */
//    private static Definition DEFINITION = new Implementation();
//
//    /**
//     * 设置模板解析实现对象
//     *
//     * @param implementation 模板解析实现对象
//     */
//    public static void set(Definition implementation) {
//        DEFINITION = implementation;
//    }
//
//    /**
//     * 发送 GET 请求
//     *
//     * @param url 请求的 URL
//     * @return 请求的结果
//     */
//    public static Mono<String> doGet(String url) {
//        return DEFINITION.doGet(url, new HashMap<>(), new HashMap<>());
//    }
//
//    /**
//     * 发送 GET 请求
//     *
//     * @param url     请求的 URL
//     * @param headers 请求的头
//     * @param params  请求的参数
//     * @return 请求的结果
//     */
//    public static Mono<String> doGet(String url, Map<String, String> headers, Map<String, String> params) {
//        return DEFINITION.doGet(url, headers, params);
//    }
//
//    /**
//     * 发送 POST 请求
//     *
//     * @param url 请求的 URL
//     * @return 请求的结果
//     */
//    public static Mono<String> doPost(String url) {
//        return DEFINITION.doPost(url, new HashMap<>(), BodyInserters.empty());
//    }
//
//    /**
//     * 发送 POST 请求
//     *
//     * @param url  请求的 URL
//     * @param json 请求的 JSON 字符串内容
//     * @return 请求的结果
//     */
//    public static Mono<String> doPost(String url, String json) {
//        return doPost(url, null, json);
//    }
//
//    /**
//     * 发送 POST 请求
//     *
//     * @param url     请求的 URL
//     * @param headers 请求的头
//     * @param json    请求的 JSON 字符串内容
//     * @return 请求的结果
//     */
//    public static Mono<String> doPost(String url, Map<String, String> headers, String json) {
//        if (headers == null) {
//            headers = new HashMap<>(0);
//        }
//        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        return DEFINITION.doPost(url, headers, BodyInserters.fromValue(json));
//    }
//
//    /**
//     * 发送 POST 请求
//     *
//     * @param url          请求的 URL
//     * @param headers      请求的头
//     * @param bodyInserter 请求的 BODY
//     * @return 请求的结果
//     */
//    public static Mono<String> doPost(
//            String url,
//            Map<String, String> headers,
//            BodyInserter<?, ? super ClientHttpRequest> bodyInserter) {
//        return DEFINITION.doPost(url, headers, bodyInserter);
//    }
//
//}
