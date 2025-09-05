package club.p6e.coat.auth.web.reactive.listener;

import club.p6e.coat.auth.web.reactive.event.PushVerificationCodeEvent;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Push Verification Code Listener
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(PushVerificationCodeListener.class)
public class PushVerificationCodeListener implements ApplicationListener<PushVerificationCodeEvent> {

    /**
     * Inject Log Object
     */
    private final Logger LOGGER = LoggerFactory.getLogger(PushVerificationCodeListener.class);

    @Override
    public void onApplicationEvent(@Nonnull PushVerificationCodeEvent event) {
        event.setCallback(() -> {
            LOGGER.info("[PVC_REACTIVE_EVENT] ===================================== ");
            LOGGER.info("[PVC_REACTIVE_EVENT] EVENT: {}", event);
            LOGGER.info("[PVC_REACTIVE_EVENT] EVENT >>> RECIPIENTS: {}", event.getRecipients());
            LOGGER.info("[PVC_REACTIVE_EVENT] EVENT >>> TYPE: {}", event.getType());
            LOGGER.info("[PVC_REACTIVE_EVENT] EVENT >>> LANGUAGE: {}", event.getLanguage());
            LOGGER.info("[PVC_REACTIVE_EVENT] EVENT >>> DATA: {}", event.getData());
            LOGGER.info("[PVC_REACTIVE_EVENT] ===================================== ");
            return Mono.just(String.valueOf(System.currentTimeMillis()));
        });
    }

}
