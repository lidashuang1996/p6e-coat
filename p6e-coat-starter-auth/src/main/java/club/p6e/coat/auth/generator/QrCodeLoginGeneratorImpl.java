package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 二维码登录
 * 二维码生成器实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class QrCodeLoginGeneratorImpl implements QrCodeLoginGenerator {

    @Override
    public String execute() {
        return DigestUtils.md5DigestAsHex(
                (GeneratorUtil.uuid() + GeneratorUtil.random(12,
                        true, false).toLowerCase()).getBytes(StandardCharsets.UTF_8)
        ).toLowerCase();
    }

}
