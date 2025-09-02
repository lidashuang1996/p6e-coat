package club.p6e.coat.auth.web.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Index Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface IndexService {

    /**
     * Default Index Data
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @return String Object
     */
    String[] execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

}
