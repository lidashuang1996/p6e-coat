package club.p6e.coat.auth;

/**
 * 认证密码加密
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordEncryptor {

    /**
     * 执行密码加密
     *
     * @param content 密码
     * @return 密码加密后的内容
     */
    String execute(String content);

    /**
     * 验证密码是否正确
     *
     * @param pwd1 密码
     * @param pwd2 密码
     * @return 密码加密后的内容
     */
    boolean validate(String pwd1, String pwd2);

}
