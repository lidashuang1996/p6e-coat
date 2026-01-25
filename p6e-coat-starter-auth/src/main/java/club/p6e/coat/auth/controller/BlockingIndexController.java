package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.service.BlockingIndexService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Index Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingIndexController.class)
@RestController("club.p6e.coat.auth.web.controller.IndexController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingIndexController {

    /**
     * Index Service Object
     */
    private final BlockingIndexService service;

    /**
     * Constructor Initialization
     *
     * @param service Index Service Object
     */
    public BlockingIndexController(BlockingIndexService service) {
        this.service = service;
    }

    @RequestMapping("")
    public Object def1(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return def(httpServletRequest, httpServletResponse);
    }

    @RequestMapping("/")
    public Object def2(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return def(httpServletRequest, httpServletResponse);
    }

    @RequestMapping("/index")
    public Object def3(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return def(httpServletRequest, httpServletResponse);
    }

    /**
     * Default Index Content
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @return Index Content
     */
    public Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable()) {
            final IndexContext.Dto result = service.execute(httpServletRequest, httpServletResponse);
            httpServletResponse.setContentType(result.getType());
            httpServletResponse.setCharacterEncoding("UTF-8");
            return result.getContent();
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)",
                    "index is not enabled"
            );
        }
    }

}
