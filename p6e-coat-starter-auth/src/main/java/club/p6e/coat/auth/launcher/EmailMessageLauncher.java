package club.p6e.coat.auth.launcher;

/**
 * 邮件消息推送器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface EmailMessageLauncher extends MessageLauncher {

    /**
     * 推送器类型
     *
     * @return 类型
     */
    @Override
    default String toType() {
        return "EMAIL_TYPE";
    }
    
}
