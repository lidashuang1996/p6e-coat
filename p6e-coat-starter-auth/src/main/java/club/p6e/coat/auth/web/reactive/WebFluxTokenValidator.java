package club.p6e.coat.auth.web.reactive;

import club.p6e.coat.auth.token.web.reactive.LocalStorageCacheTokenValidator;
import club.p6e.coat.auth.token.web.reactive.TokenValidator;
import club.p6e.coat.auth.token.web.reactive.UserTokenCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = TokenValidator.class,
        ignored = WebFluxTokenValidator.class
)
public class WebFluxTokenValidator extends LocalStorageCacheTokenValidator implements TokenValidator {

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public WebFluxTokenValidator(UserTokenCache cache) {
        super(cache);
    }

}
