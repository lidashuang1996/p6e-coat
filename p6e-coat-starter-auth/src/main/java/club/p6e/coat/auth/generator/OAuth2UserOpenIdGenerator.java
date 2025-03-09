package club.p6e.coat.auth.generator;

/**
 * 生成 OPENID
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2UserOpenIdGenerator {

    /**
     * 生成 OPENID
     *
     * @return OPENID 内容
     */
    String execute(String clientId, String userId);

}
