package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Blocking Reconfirm Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingReconfirmService {

    /**
     * Execute
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Reconfirm Context Request Object
     * @return Result Object
     */
    Map<String, String> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ReconfirmContext.Request request);

}
