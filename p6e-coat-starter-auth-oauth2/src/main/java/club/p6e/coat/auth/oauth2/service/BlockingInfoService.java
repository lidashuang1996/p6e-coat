package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.InfoContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Blocking Info Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingInfoService {

    /**
     * Get User Info
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Authorize Context Request Object
     * @return Result Object
     */
    Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request);

    /**
     * Get Client Info
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Authorize Context Request Object
     * @return Result Object
     */
    Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request);

}
