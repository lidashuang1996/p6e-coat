package club.p6e.coat.message.center.launcher.sms;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.sms.ShortMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * Short Message Launcher Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageLauncherService extends LauncherService<ShortMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.MOBILE;
    }

}
