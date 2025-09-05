package club.p6e.coat.auth.web.reactive;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.context.RegisterContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Request Parameter Validator Configuration
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class RequestParameterValidatorConfiguration {

    public RequestParameterValidator validatorForgotPasswordContextRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return ForgotPasswordContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final ForgotPasswordContext.Request frr) {
                    if (frr.getCode() != null && !frr.getCode().isEmpty()
                            && frr.getPassword() != null && !frr.getPassword().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorForgotPasswordContextVerificationCodeAcquisitionRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return ForgotPasswordContext.VerificationCodeAcquisition.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final ForgotPasswordContext.VerificationCodeAcquisition.Request frr) {
                    if (frr.getAccount() != null && !frr.getAccount().isEmpty()
                            && frr.getLanguage() != null && !frr.getLanguage().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextAccountPasswordRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.AccountPassword.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final LoginContext.AccountPassword.Request lrr) {
                    if (lrr.getAccount() != null && !lrr.getAccount().isEmpty()
                            && lrr.getPassword() != null && !lrr.getPassword().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextAuthenticationRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.Authentication.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof LoginContext.Authentication.Request) {
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextQuickResponseCodeAcquisitionRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.QuickResponseCodeAcquisition.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof LoginContext.QuickResponseCodeAcquisition.Request) {
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextQuickResponseCodeCallbackRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.QuickResponseCodeCallback.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof LoginContext.QuickResponseCodeCallback.Request) {
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextQuickResponseCodeRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.QuickResponseCode.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof LoginContext.QuickResponseCode.Request) {
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextVerificationCodeAcquisitionRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.VerificationCodeAcquisition.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof LoginContext.VerificationCodeAcquisition.Request lrr) {
                    if (lrr.getAccount() != null && !lrr.getAccount().isEmpty()
                            && lrr.getLanguage() != null && !lrr.getLanguage().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorLoginContextVerificationCodeRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return LoginContext.VerificationCode.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof LoginContext.VerificationCode.Request lrr) {
                    if (lrr.getCode() != null && !lrr.getCode().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorPasswordSignatureContextRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return PasswordSignatureContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof PasswordSignatureContext.Request) {
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorRegisterContextRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return RegisterContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof RegisterContext.Request rrr) {
                    if (rrr.getCode() != null && !rrr.getCode().isEmpty()
                            && rrr.getPassword() != null && !rrr.getPassword().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public RequestParameterValidator validatorRegisterContextVerificationCodeAcquisitionRequest() {
        return new RequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return RegisterContext.VerificationCodeAcquisition.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof RegisterContext.VerificationCodeAcquisition.Request rrr) {
                    if (rrr.getAccount() != null && !rrr.getAccount().isEmpty()
                            && rrr.getLanguage() != null && !rrr.getLanguage().isEmpty()) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

}
