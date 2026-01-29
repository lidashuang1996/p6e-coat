package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.TokenContext;
import club.p6e.coat.auth.oauth2.service.BlockingTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Token Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingAuthorizeController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.BlockingTokenController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingTokenController {

    /**
     * Blocking Token Service Object
     */
    private final BlockingTokenService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Token Service Object
     */
    public BlockingTokenController(BlockingTokenService service) {
        this.service = service;
    }

    @PostMapping("/oauth2/token")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            TokenContext.Request request
    ) {
        return service.execute(httpServletRequest, httpServletResponse, request);
    }

}
