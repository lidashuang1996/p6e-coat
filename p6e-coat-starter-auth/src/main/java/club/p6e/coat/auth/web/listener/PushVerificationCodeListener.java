package club.p6e.coat.auth.web.listener;

import club.p6e.coat.auth.web.event.PushVerificationCodeEvent;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Push Verification Code Listener
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = PushVerificationCodeListener.class,
        ignored = PushVerificationCodeListener.class
)
@Component("club.p6e.coat.auth.web.listener.PushVerificationCodeListener")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class PushVerificationCodeListener implements ApplicationListener<PushVerificationCodeEvent> {

    /**
     * Inject Log Object
     */
    private final Logger LOGGER = LoggerFactory.getLogger(PushVerificationCodeListener.class);

    @Override
    public void onApplicationEvent(@Nonnull PushVerificationCodeEvent event) {
        LOGGER.info("[PVC_EVENT] ===================================== ");
        LOGGER.info("[PVC_EVENT] EVENT: {}", event);
        LOGGER.info("[PVC_EVENT] EVENT >>> RECIPIENTS: {}", event.getRecipients());
        LOGGER.info("[PVC_EVENT] EVENT >>> TYPE: {}", event.getType());
        LOGGER.info("[PVC_EVENT] EVENT >>> LANGUAGE: {}", event.getLanguage());
        LOGGER.info("[PVC_EVENT] EVENT >>> DATA: {}", event.getData());
        LOGGER.info("[PVC_EVENT] ===================================== ");
    }

}
