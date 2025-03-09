package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;

/**
 * OAuth2 认证回调 CODE 数据实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2CodeGeneratorImpl implements OAuth2CodeGenerator {

    @Override
    public String execute() {
        return GeneratorUtil.random(8, true, false).toLowerCase();
    }

}
