package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;

/**
 * 账号密码登录对密码传输加密标记生成器实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginSignatureGeneratorImpl
        implements AccountPasswordLoginSignatureGenerator {

    @Override
    public String execute() {
        return (GeneratorUtil.uuid() + GeneratorUtil.random(6, true, false)).toLowerCase();
    }

}
