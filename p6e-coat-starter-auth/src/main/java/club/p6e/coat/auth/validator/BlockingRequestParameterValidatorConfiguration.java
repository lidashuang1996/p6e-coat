package club.p6e.coat.auth.validator;

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

    public BlockingRequestParameterValidator validatorLoginContextAccountPasswordRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.AccountPassword.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final LoginContext.AccountPassword.Request lrr) {
                    if (lrr.getAccount() != null && !lrr.getAccount().isEmpty()
                            && lrr.getPassword() != null && !lrr.getPassword().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorLoginContextAuthenticationRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.Authentication.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof LoginContext.Authentication.Request) {
                    return param;
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorLoginContextQuickResponseCodeAcquisitionRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.QuickResponseCodeAcquisition.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof LoginContext.QuickResponseCodeAcquisition.Request) {
                    return param;
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorLoginContextQuickResponseCodeCallbackRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.QuickResponseCodeCallback.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof LoginContext.QuickResponseCodeCallback.Request) {
                    return param;
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorLoginContextQuickResponseCodeRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.QuickResponseCode.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof LoginContext.QuickResponseCode.Request) {
                    return param;
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorLoginContextVerificationCodeAcquisitionRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.VerificationCodeAcquisition.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof LoginContext.VerificationCodeAcquisition.Request lrr) {
                    if (lrr.getAccount() != null && !lrr.getAccount().isEmpty()
                            && lrr.getLanguage() != null && !lrr.getLanguage().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorLoginContextVerificationCodeRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.VerificationCode.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof LoginContext.VerificationCode.Request lrr) {
                    if (lrr.getCode() != null && !lrr.getCode().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorPasswordSignatureContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return PasswordSignatureContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof PasswordSignatureContext.Request) {
                    return param;
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorRegisterContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return RegisterContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof RegisterContext.Request rrr) {
                    if (rrr.getCode() != null && !rrr.getCode().isEmpty()
                            && rrr.getPassword() != null && !rrr.getPassword().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validatorRegisterContextVerificationCodeAcquisitionRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return RegisterContext.VerificationCodeAcquisition.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof RegisterContext.VerificationCodeAcquisition.Request rrr) {
                    if (rrr.getAccount() != null && !rrr.getAccount().isEmpty()
                            && rrr.getLanguage() != null && !rrr.getLanguage().isEmpty()) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

}
