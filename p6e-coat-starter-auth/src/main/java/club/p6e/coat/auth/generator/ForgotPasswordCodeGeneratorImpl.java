package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;

/**
 * 忘记密码
 * 验证码生成器实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordCodeGeneratorImpl implements ForgotPasswordCodeGenerator {

    @Override
    public String execute(String type) {
        return GeneratorUtil.random(6, false, false).toLowerCase();
    }

}
