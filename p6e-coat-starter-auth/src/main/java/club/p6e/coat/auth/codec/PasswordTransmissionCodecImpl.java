package club.p6e.coat.auth.codec;

import club.p6e.coat.auth.error.PasswordTransmissionCodecException;
import club.p6e.coat.common.utils.RsaUtil;

/**
 * 密码传输编码解码器的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class PasswordTransmissionCodecImpl implements PasswordTransmissionCodec {

    @Override
    public Model generate() {
        try {
            final RsaUtil.KeyModel keyModel = RsaUtil.generateKeyPair();
            return new Model()
                    .setPublicKey(keyModel.getPublicKey())
                    .setPrivateKey(keyModel.getPrivateKey());
        } catch (Exception e) {
            throw new PasswordTransmissionCodecException(
                    this.getClass(),
                    "[ PTC ] generate exception >> " + e.getMessage() + ".",
                    "PTC generate key exception."
            );
        }
    }

    @Override
    public String encryption(Model model, String content) {
        if (model == null) {
            throw new PasswordTransmissionCodecException(
                    this.getClass(),
                    "[ PTC ] encryption exception model value is null exception.",
                    "PTC encryption model exception."
            );
        } else {
            try {
                if (model.getPublicKey() != null) {
                    return RsaUtil.publicKeyEncryption(model.getPublicKey(), content);
                } else if (model.getPrivateKey() != null) {
                    return RsaUtil.privateKeyEncryption(model.getPrivateKey(), content);
                } else {
                    throw new PasswordTransmissionCodecException(
                            this.getClass(),
                            "[ PTC ] encryption ( PublicKey/PrivateKey ) value is null exception >> " + model + ".",
                            "PTC encryption ( PublicKey/PrivateKey ) exception."
                    );
                }
            } catch (Exception e) {
                throw new PasswordTransmissionCodecException(
                        this.getClass(),
                        "[ PTC ] encryption exception model(" + model + ") content(" + content + ") >> " + e.getMessage() + ".",
                        "PTC encryption exception."
                );
            }
        }
    }

    @Override
    public String decryption(Model model, String content) {
        if (model == null) {
            throw new PasswordTransmissionCodecException(
                    this.getClass(),
                    "[ PTC ] decryption exception model value is null exception.",
                    "PTC decryption model exception."
            );
        } else {
            try {
                if (model.getPrivateKey() == null) {
                    throw new PasswordTransmissionCodecException(
                            this.getClass(),
                            "[ PTC ] decryption ( PrivateKey ) value is null exception >> " + model + ".",
                            "PTC decryption ( PrivateKey ) is null exception."
                    );
                } else {
                    return RsaUtil.privateKeyDecryption(model.getPrivateKey(), content);
                }
            } catch (Exception e) {
                throw new PasswordTransmissionCodecException(
                        this.getClass(),
                        "[ PTC ] decryption exception model(" + model + ") content(" + content + ") >> " + e.getMessage() + ".",
                        "PTC decryption exception."
                );
            }
        }
    }
}
