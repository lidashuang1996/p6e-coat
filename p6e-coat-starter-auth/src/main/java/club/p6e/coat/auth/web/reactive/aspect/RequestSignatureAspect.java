package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.common.utils.Md5Util;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request Signature Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
public class RequestSignatureAspect extends Aspect {

    @Override
    public int getOrder() {
        return -10000;
    }

    @Override
    Mono<Object> before(Object[] o) {
        ServerWebExchange exchange = null;
        for (final Object item : o) {
            if (item instanceof ServerWebExchange) {
                exchange = (ServerWebExchange) item;
                break;
            }
        }
        if (exchange == null) {
            return Mono.just(o);
        } else {
            final Object object = o[o.length - 1];
            final Map<String, Object> body = CopyUtil.objectToMap(object);
            final MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
            final String timestamp = params.getFirst("timestamp");
            final String signature = params.getFirst("signature");
            final String localSignature = signature(body, timestamp);
            if (localSignature != null && localSignature.equals(signature)
                    && System.currentTimeMillis() < Long.parseLong(timestamp) + 180000L) {
                return Mono.just(o);
            }
            return Mono.error(GlobalExceptionContext.executeSignatureException(
                    this.getClass(),
                    "fun before(Object[] o).",
                    "request parameter verification signature exception or timestamp expires exception."
            ));
        }
    }

    @Override
    Mono<Object> after(Object[] o) {
        return Mono.just(o[o.length - 1]);
    }

    /**
     * Signature Map Data
     *
     * @param data Data
     * @return Signature Content
     */
    private String signature(Map<String, Object> data) {
        final StringBuilder content = new StringBuilder();
        final List<String> keys = new ArrayList<>(data.keySet());
        keys.sort(String::compareTo);
        for (String key : keys) {
            if (data.get(key) instanceof final Map<?, ?> tmp) {
                final Map<String, Object> map = new HashMap<>();
                tmp.forEach((m, o) -> map.put(String.valueOf(m), o));
                content.append("&").append(key).append("=").append(signature(map));
            } else {
                content.append("&").append(key).append("=").append(data.get(key));
            }
        }
        return content.isEmpty() ? "" : content.substring(1);
    }

    /**
     * Signature Data
     *
     * @param data      Data
     * @param timestamp Timestamp
     * @return Signature
     */
    private String signature(Map<String, Object> data, String timestamp) {
        if (data == null || timestamp == null) {
            return null;
        }
        return Md5Util.execute(Md5Util.execute(signature(data) + "@timestamp=" + timestamp));
    }

}
