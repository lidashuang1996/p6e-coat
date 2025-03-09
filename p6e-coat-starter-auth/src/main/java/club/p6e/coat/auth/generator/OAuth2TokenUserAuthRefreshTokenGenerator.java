package club.p6e.coat.auth.generator;

/**
 * 生成用户刷新令牌
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2TokenUserAuthRefreshTokenGenerator {

    /**
     * 生成令牌
     *
     * @return 令牌
     */
    String execute();

}
