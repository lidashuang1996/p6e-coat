package club.p6e.coat.auth.listener;

import club.p6e.coat.auth.event.BlockingPushVerificationCodeEvent;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Blocking Push Verification Code Listener
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.listener.BlockingPushVerificationCodeListener")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingPushVerificationCodeListener implements ApplicationListener<BlockingPushVerificationCodeEvent> {

    /**
     * Inject Log Object
     */
    private final Logger LOGGER = LoggerFactory.getLogger(BlockingPushVerificationCodeListener.class);

    @Override
    public void onApplicationEvent(@Nonnull BlockingPushVerificationCodeEvent event) {
        LOGGER.info("[PVC_EVENT] ===================================== ");
        LOGGER.info("[PVC_EVENT] EVENT: {}", event);
        LOGGER.info("[PVC_EVENT] EVENT >>> RECIPIENTS: {}", event.getRecipients());
        LOGGER.info("[PVC_EVENT] EVENT >>> TYPE: {}", event.getType());
        LOGGER.info("[PVC_EVENT] EVENT >>> LANGUAGE: {}", event.getLanguage());
        LOGGER.info("[PVC_EVENT] EVENT >>> DATA: {}", event.getData());
        LOGGER.info("[PVC_EVENT] ===================================== ");
    }

}
