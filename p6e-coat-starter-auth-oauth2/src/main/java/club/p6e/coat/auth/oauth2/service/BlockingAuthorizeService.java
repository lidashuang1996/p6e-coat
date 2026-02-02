package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Authorize Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingAuthorizeService {

    /**
     * Execute
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Authorize Context Request Object
     * @return Index Context Dto Object
     */
    IndexContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AuthorizeContext.Request request
    );

}
