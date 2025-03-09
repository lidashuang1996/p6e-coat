package club.p6e.coat.auth.generator;

/**
 * 第三方登录需要注册的时候的凭证码生成器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface RegisterOtherLoginGenerator {

    /**
     * 生成标记
     *
     * @return 标记内容
     */
    String execute();

}
