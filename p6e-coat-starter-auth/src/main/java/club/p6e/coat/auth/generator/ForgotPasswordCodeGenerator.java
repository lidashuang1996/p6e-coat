package club.p6e.coat.auth.generator;

/**
 * 忘记密码
 * 验证码生成器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ForgotPasswordCodeGenerator {

    /**
     * 生成验证码
     *
     * @param type 类型
     * @return 验证码
     */
    String execute(String type);

}
