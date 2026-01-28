package club.p6e.coat.auth.password;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Password Encryptor Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class PasswordEncryptorImpl implements PasswordEncryptor {

    @Override
    public String execute(String content) {
        if (content == null || content.isEmpty()) {
            throw GlobalExceptionContext.exceptionPasswordEncryptorException(
                    this.getClass(),
                    "fun String execute(String content)",
                    "password content cannot be null or empty"
            );
        }
        try {
            return BCrypt.hashpw(content, BCrypt.gensalt(10));
        } catch (Exception e) {
            throw GlobalExceptionContext.exceptionPasswordEncryptorException(
                    this.getClass(),
                    "fun String execute(String content)",
                    "password encryption(B_Crypt) failed: " + e.getMessage()
            );
        }
    }

    @Override
    public boolean validate(String pwdOriginal, String pwdEncryption) {
        if (pwdOriginal == null || pwdEncryption == null
                || pwdOriginal.isEmpty() || pwdEncryption.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(pwdOriginal, pwdEncryption);
        } catch (Exception e) {
            return false;
        }
    }

}
