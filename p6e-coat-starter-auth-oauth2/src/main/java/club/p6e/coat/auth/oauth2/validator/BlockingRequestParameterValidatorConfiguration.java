package club.p6e.coat.auth.oauth2.validator;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.context.RegisterContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Request Parameter Validator Configuration
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingRequestParameterValidatorConfiguration {

    public BlockingRequestParameterValidator validatorForgotPasswordContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return ForgotPasswordContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final ForgotPasswordContext.Request frr) {
                    if (frr.getCode() != null && !frr.getCode().isEmpty()
                            && frr.getPassword() != null && !frr.getPassword().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorForgotPasswordContextVerificationCodeAcquisitionRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return ForgotPasswordContext.VerificationCodeAcquisition.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final ForgotPasswordContext.VerificationCodeAcquisition.Request frr) {
                    if (frr.getAccount() != null && !frr.getAccount().isEmpty()
                            && frr.getLanguage() != null && !frr.getLanguage().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

}
