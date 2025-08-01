package club.p6e.coat.auth.password;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * Password Encryptor Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class PasswordEncryptorImpl implements PasswordEncryptor {

    /**
     * Salt Content
     */
    private final String salt;

    /**
     * Is Init Status
     */
    private final boolean status;

    /**
     * Init
     *
     * @param salt Salt Content
     */
    public PasswordEncryptorImpl(String salt) {
        this.salt = salt;
        this.status = true;
    }

    /**
     * Validate Salt
     */
    private void validate() {
        if (!status) {
            throw GlobalExceptionContext.exceptionPasswordEncryptorException(
                    this.getClass(),
                    "fun execute(String content).",
                    "password encryptor salt not init exception."
            );
        }
    }

    /**
     * Format Content
     *
     * @param content Content
     * @return Format Content
     */
    private String format(String content) {
        final String hex = DigestUtils.md5DigestAsHex(
                (content + this.salt).getBytes(StandardCharsets.UTF_8)
        );
        final int index = ((int) hex.charAt(16)) % 24;
        return hex.substring(index) + DigestUtils.md5DigestAsHex(
                hex.substring(0, index).getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Execute Encryption
     *
     * @param random  Random
     * @param content Content
     * @return Result
     */
    private String execute(String random, String content) {
        final int index = ((int) random.charAt(0)) % 24;
        return random + "." + content.substring(index) + DigestUtils.md5DigestAsHex(
                (random + content.substring(0, index)).getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String execute(String content) {
        validate();
        return execute(GeneratorUtil.random(8, true, false), format(content));
    }

    @Override
    public boolean validate(String pwdOriginal, String pwdEncryption) {
        validate();
        if (pwdOriginal == null || pwdEncryption == null
                || pwdOriginal.isEmpty() || pwdEncryption.isEmpty()) {
            return false;
        } else {
            boolean bool = true;
            final StringBuilder random = new StringBuilder();
            for (final char ch : pwdEncryption.toCharArray()) {
                if (ch == '.') {
                    bool = false;
                    break;
                } else {
                    random.append(ch);
                }
            }
            if (bool) {
                return false;
            } else {
                System.out.println(
                        "PasswordEncryptorImpl.validate(String pwdOriginal, String pwdEncryption) " +
                                "pwdOriginal = " + pwdOriginal + ", " +
                                "pwdEncryption = " + pwdEncryption + ", " +
                                "random = " + random.toString() + ", " +
                                "execute(random, format(pwdOriginal)) = " + execute(random.toString(), format(pwdOriginal))
                );
                return execute(random.toString(), format(pwdOriginal)).equals(pwdEncryption);
            }
        }
    }

}
