package club.p6e.coat.auth.generator;

/**
 * 生成刷新令牌
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthRefreshTokenGenerator {

    /**
     * 生成令牌
     *
     * @return 令牌
     */
    String execute();

}
