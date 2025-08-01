package club.p6e.coat.message.center.launcher.mail;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.mail.MailMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * Mail Message Launcher Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageLauncherService extends LauncherService<MailMessageConfigModel> {

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
