package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import club.p6e.coat.auth.oauth2.service.BlockingReconfirmService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Reconfirm Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingReconfirmController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.BlockingReconfirmController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingReconfirmController {

    /**
     * Blocking Reconfirm Service Object
     */
    private final BlockingReconfirmService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Reconfirm Service Object
     */
    public BlockingReconfirmController(BlockingReconfirmService service) {
        this.service = service;
    }

    @PostMapping("/oauth2/reconfirm")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ReconfirmContext.Request request
    ) {
        return service.execute(httpServletRequest, httpServletResponse, request);
    }

}
