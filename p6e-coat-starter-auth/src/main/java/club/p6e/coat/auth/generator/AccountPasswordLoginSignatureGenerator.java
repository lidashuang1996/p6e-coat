package club.p6e.coat.auth.generator;

/**
 * 账号密码登录对密码传输加密标记生成器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AccountPasswordLoginSignatureGenerator {

    /**
     * 生成标记
     *
     * @return 标记内容
     */
    String execute();

}
