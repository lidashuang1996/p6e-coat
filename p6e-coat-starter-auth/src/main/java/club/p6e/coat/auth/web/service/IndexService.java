package club.p6e.coat.auth.web.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface IndexService {

    String[] execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

}
