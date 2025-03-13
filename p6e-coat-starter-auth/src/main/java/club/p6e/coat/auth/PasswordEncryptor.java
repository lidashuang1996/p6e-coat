package club.p6e.coat.auth;

/**
 * Password Encryptor
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordEncryptor {

    /**
     * Execute Password Encryption
     *
     * @param content Password
     * @return Password Encryption Content
     */
    String execute(String content);

    /**
     * Verify If The Password Matches
     *
     * @param pwdOriginal   Password Original Content
     * @param pwdEncryption Password Encryption Content
     * @return Password Matches Result
     */
    boolean validate(String pwdOriginal, String pwdEncryption);

}
