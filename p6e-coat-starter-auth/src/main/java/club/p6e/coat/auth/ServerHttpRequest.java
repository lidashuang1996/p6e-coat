package club.p6e.coat.auth;

import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public class ServerHttpRequest extends ServerHttpRequestDecorator {

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private static final String ACCOUNT_PASSWORD_SIGNATURE_MARK = "ACCOUNT_PASSWORD_SIGNATURE_MARK";

    public ServerHttpRequest(org.springframework.http.server.reactive.ServerHttpRequest delegate) {
        super(delegate);
    }


    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {

    }

    public String getAccountPasswordSignatureMark() {
        final Object data = getAttribute(ACCOUNT_PASSWORD_SIGNATURE_MARK);
        return data == null ? null : String.valueOf(data);
    }

    public String setAccountPasswordSignatureMark(String accountPasswordSignatureMark) {
        return attributes.put(ACCOUNT_PASSWORD_SIGNATURE_MARK, accountPasswordSignatureMark).toString();
    }

    public Mono<ServerHttpRequest> init() {
        return Mono.just(this);
    }

}
