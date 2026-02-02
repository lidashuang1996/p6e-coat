package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.auth.oauth2.service.BlockingInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Info Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingInfoController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.BlockingInfoController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingInfoController {

    /**
     * Blocking Info Service Object
     */
    private final BlockingInfoService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Info Service Object
     */
    public BlockingInfoController(BlockingInfoService service) {
        this.service = service;
    }

    @RequestMapping(value = "/oauth2/user/info", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getUserInfo(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            InfoContext.Request request
    ) {
        return service.getUserInfo(httpServletRequest, httpServletResponse, request);
    }

    @RequestMapping(value = "/oauth2/client/info", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getClientInfo(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            InfoContext.Request request
    ) {
        return service.getClientInfo(httpServletRequest, httpServletResponse, request);
    }

}
