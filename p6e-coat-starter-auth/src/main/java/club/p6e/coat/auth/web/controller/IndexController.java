package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.web.service.IndexService;
import club.p6e.coat.common.utils.SpringUtil;
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
@ConditionalOnMissingBean(
        value = IndexController.class,
        ignored = IndexController.class
)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class IndexController {

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

    public Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final String[] r = SpringUtil.getBean(IndexService.class).execute(httpServletRequest, httpServletResponse);
        if (r.length > 1) {
            httpServletResponse.setContentType(r[0]);
            httpServletResponse.setCharacterEncoding("UTF-8");
            return r[1];
        } else {
            return "";
        }
    }

}
