package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.TokenContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Blocking Token Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingTokenService {

    /**
     * Execute
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Token Context Request Object
     * @return Result Object
     */
    Map<String, Object> execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            TokenContext.Request request
    );

}
