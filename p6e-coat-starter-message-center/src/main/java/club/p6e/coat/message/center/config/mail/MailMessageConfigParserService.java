package club.p6e.coat.message.center.config.mail;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * Mail Message Config Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageConfigParserService extends ConfigParserService<MailMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.MAIL;
    }

}
