package club.p6e.coat.auth.listener;

import club.p6e.coat.auth.event.PushMessageEvent;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Push Message Event Listener
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class PushMessageEventListener implements ApplicationListener<PushMessageEvent> {

    /**
     * Inject Log Object
     */
    private final Logger LOGGER = LoggerFactory.getLogger(PushMessageEventListener.class);

    @Override
    public void onApplicationEvent(@Nonnull PushMessageEvent event) {
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] ===================================== ");
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] received push message event: {}", event);
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] received push message event >>> recipients: {}", event.getRecipients());
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] received push message event >>> type: {}", event.getType());
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] received push message event >>> language: {}", event.getLanguage());
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] received push message event >>> data: {}", event.getData());
        LOGGER.info("[PUSH_MESSAGE_EVENT_LISTENER] ===================================== ");
    }

}
