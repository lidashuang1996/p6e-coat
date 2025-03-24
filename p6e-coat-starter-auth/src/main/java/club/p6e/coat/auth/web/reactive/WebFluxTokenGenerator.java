package club.p6e.coat.auth.web.reactive;

import club.p6e.coat.auth.token.web.reactive.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = TokenGenerator.class,
        ignored = WebFluxTokenGenerator.class
)
public class WebFluxTokenGenerator extends LocalStorageCacheTokenGenerator implements TokenGenerator {

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public WebFluxTokenGenerator(UserTokenCache cache) {
        super(cache);
    }

}
