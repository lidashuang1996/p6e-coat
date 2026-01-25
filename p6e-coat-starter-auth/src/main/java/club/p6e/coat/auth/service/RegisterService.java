package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.RegisterContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Register Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface RegisterService {

    /**
     * Execution Register
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Register Context Request Object
     * @return Register Context Dto Object
     */
    RegisterContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.Request param
    );

}
