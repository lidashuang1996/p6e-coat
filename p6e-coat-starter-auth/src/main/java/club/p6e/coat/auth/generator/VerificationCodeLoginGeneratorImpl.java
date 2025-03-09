package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;

/**
 * 验证码登录
 * 验证码生成器实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeLoginGeneratorImpl implements VerificationCodeLoginGenerator {

    @Override
    public String execute(String type) {
        return GeneratorUtil.random(6, false, false).toLowerCase();
    }

}
