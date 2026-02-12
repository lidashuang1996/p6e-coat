package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.service.BlockingLogoutService;
import club.p6e.coat.common.exception.ServiceNotEnableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Logout Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController("club.p6e.coat.auth.controller.BlockingLogoutController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLogoutController {

    /**
     * Blocking Logout Service Object
     */
    private final BlockingLogoutService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Register Service Object
     */
    public BlockingLogoutController(BlockingLogoutService service) {
        this.service = service;
    }

    @RequestMapping("/logout")
    public Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse);
        } else {
            throw new ServiceNotEnableException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)",
                    "logout is not enabled"
            );
        }
    }

}
