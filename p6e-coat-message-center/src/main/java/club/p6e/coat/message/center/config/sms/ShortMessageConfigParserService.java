package club.p6e.coat.message.center.config.sms;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * Short Message Config Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigParserService extends ConfigParserService<ShortMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.SMS;
    }

}
