package club.p6e.coat.auth.generator;

/**
 * OAuth2 认证回调 CODE 数据
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2CodeGenerator {

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    String execute();

}
