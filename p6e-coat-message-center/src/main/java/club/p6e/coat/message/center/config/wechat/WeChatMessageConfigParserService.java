package club.p6e.coat.message.center.config.wechat;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * We Chat Message Config Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageConfigParserService extends ConfigParserService<WeChatMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.WECHAT;
    }

}
