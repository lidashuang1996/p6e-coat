package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.service.BlockingAuthorizeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Authorize Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingAuthorizeController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.BlockingAuthorizeController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingAuthorizeController {

    /**
     * Blocking Authorize Service Object
     */
    private final BlockingAuthorizeService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Authorize Service Object
     */
    public BlockingAuthorizeController(BlockingAuthorizeService service) {
        this.service = service;
    }

    @PostMapping("/oauth2/authorize")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AuthorizeContext.Request request
    ) {
        return service.execute(httpServletRequest, httpServletResponse, request);
    }

}
