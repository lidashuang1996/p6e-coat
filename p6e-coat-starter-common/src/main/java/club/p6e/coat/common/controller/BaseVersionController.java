package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Base Version Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/__version__")
@Component(value = "club.p6e.coat.common.controller.BaseVersionController")
public class BaseVersionController extends BaseController {

    @RequestMapping("")
    public Object def1() {
        return def2();
    }

    @SuppressWarnings("ALL")
    @RequestMapping("/")
    public Object def2() {
        try {
            Class.forName("jakarta.servlet.ServletRequest");
            return ResultContext.build(SpringUtil.getBean(Properties.class).getVersion());
        } catch (Exception e) {
            return Mono.just(ResultContext.build(SpringUtil.getBean(Properties.class).getVersion()));
        }
    }

}
