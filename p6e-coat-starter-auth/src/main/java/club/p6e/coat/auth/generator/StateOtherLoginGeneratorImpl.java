package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;

/**
 * 第三方登录的 state 生成器的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class StateOtherLoginGeneratorImpl implements StateOtherLoginGenerator {

    @Override
    public String execute() {
        return GeneratorUtil.random(8, true, false);
    }

}
