package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Account Password Login Signature Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordSignatureService {

    /**
     * Execute Account Password Login Password Signature
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param    Login Context Account Password Signature.Request Object
     * @return Login Context Account Password Signature Dto Object
     */
    Mono<PasswordSignatureContext.Dto> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PasswordSignatureContext.Request param);

}
