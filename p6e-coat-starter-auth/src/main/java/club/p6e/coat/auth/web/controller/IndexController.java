package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.service.IndexService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account Password Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(IndexController.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class IndexController {

    /**
     * Index Service Object
     */
    private final IndexService service;

    /**
     * Constructor Initialization
     *
     * @param service Index Service Object
     */
    public IndexController(IndexService service) {
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
            final String[] r = service.execute(httpServletRequest, httpServletResponse);
            if (r.length > 1) {
                httpServletResponse.setContentType(r[0]);
                httpServletResponse.setCharacterEncoding("UTF-8");
                return r[1];
            } else {
                return "";
            }
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)",
                    "index is not enabled"
            );
        }
    }

}
