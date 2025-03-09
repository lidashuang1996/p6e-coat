package club.p6e.coat.auth.generator;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 生成 OPENID 实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2UserOpenIdGeneratorImpl implements OAuth2UserOpenIdGenerator {

    /**
     * 盐值
     */
    private static final String SEASONING = "C6xfd#lKCF!6$IQB1NQZo&%m1jenVc3p";

    @Override
    public String execute(String clientId, String userId) {
        final String c = DigestUtils.md5DigestAsHex(clientId.getBytes(StandardCharsets.UTF_8));
        final int i = c.charAt(0) % 8;
        return DigestUtils.md5DigestAsHex(
                (c.substring(i)
                        + userId
                        + SEASONING
                        + c.substring(0, i)
                ).getBytes(StandardCharsets.UTF_8)
        );
    }

}
