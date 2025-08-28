package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.RegisterContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeRegisterAcquisitionService {

    /**
     * 注册验证码发送
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<RegisterContext.VerificationCodeAcquisition.Dto> execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.VerificationCodeAcquisition.Request param
    );

}
