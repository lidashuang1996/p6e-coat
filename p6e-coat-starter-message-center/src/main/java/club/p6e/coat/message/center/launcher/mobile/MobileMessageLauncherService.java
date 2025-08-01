package club.p6e.coat.message.center.launcher.mobile;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.mobile.MobileMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * Mobile Message Launcher Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherService extends LauncherService<MobileMessageConfigModel> {

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
