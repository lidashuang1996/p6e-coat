package club.p6e.coat.auth.listener;

import club.p6e.coat.auth.event.PushMessageEvent;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Push Message Listener
 *
 * @author lidashuang
 * @version 1.0
 */
public class PushMessageListener implements ApplicationListener<PushMessageEvent> {

    /**
     * Inject Log Object
     */
    private final Logger LOGGER = LoggerFactory.getLogger(PushMessageListener.class);

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
