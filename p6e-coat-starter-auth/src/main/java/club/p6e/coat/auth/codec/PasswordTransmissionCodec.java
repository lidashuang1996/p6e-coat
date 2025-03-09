package club.p6e.coat.auth.codec;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 密码传输编码解码器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public interface PasswordTransmissionCodec {

    /**
     * 传输编解码模型
     */
    @Data
    @Accessors(chain = true)
    class Model {
        private String publicKey;
        private String privateKey;
    }

    /**
     * 生成密钥对
     *
     * @return 密钥对模型
     */
    Model generate();

    /**
     * 加密
     *
     * @param model   密钥对模型
     * @param content 待编码内容
     * @return 编码之后的内容
     */
    String encryption(Model model, String content);

    /**
     * 解密
     *
     * @param model   密钥对模型
     * @param content 待解码内容
     * @return 解码之后的内容
     */
    String decryption(Model model, String content);

}
