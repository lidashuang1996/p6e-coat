package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Password Signature Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordSignatureService {

    /**
     * Execute Password Signature
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Password Signature Context Request Object
     * @return Password Signature Context Dto Object
     */
    PasswordSignatureContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            PasswordSignatureContext.Request param
    );

}
