package club.p6e.coat.auth;

import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 认证过滤器
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthWebFilter implements WebFilter, Ordered {

    /**
     * 匹配器对象
     */
    private final AuthPathMatcher matcher;

    /**
     * 验证器对象
     */
    private final AuthCertificateValidator validator;

    /**
     * 构造方法初始化
     *
     * @param matcher   匹配器对象
     * @param validator 验证器对象
     */
    public AuthWebFilter(AuthPathMatcher matcher, AuthCertificateValidator validator) {
        this.matcher = matcher;
        this.validator = validator;
    }

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if (matcher.match(exchange.getRequest().getPath().value())) {
            return validator.execute(exchange).flatMap(chain::filter);
        } else {
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1000;
    }

}
