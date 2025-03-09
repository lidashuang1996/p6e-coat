package club.p6e.coat.auth.generator;

/**
 * 验证码登录
 * 验证码生成器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeLoginGenerator {

    /**
     * 生成验证码
     *
     * @param type 类型
     * @return 验证码
     */
    String execute(String type);

}
