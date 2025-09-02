package club.p6e.coat.auth.web.repository;

import club.p6e.coat.auth.User;

/**
 * User Repository
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public interface UserRepository {

    /**
     * Query By ID
     *
     * @param id ID
     * @return User Object
     */
    User findById(Integer id);

    /**
     * Query By Account
     *
     * @param account Account
     * @return User Object
     */
    User findByAccount(String account);

    /**
     * Query By Phone Account
     *
     * @param phone Phone Account
     * @return User Object
     */
    User findByPhone(String phone);

    /**
     * Query By MailBox Account
     *
     * @param mailbox MailBox Account
     * @return User Object
     */
    User findByMailbox(String mailbox);

    /**
     * Query By Phone Account Or Email Account
     *
     * @param content Phone Account Or Mailbox Account
     * @return User Object
     */
    User findByPhoneOrMailbox(String content);

    /**
     * Create User
     *
     * @param user User Object
     * @return User Object
     */
    User create(User user);

    /**
     * Update Password
     *
     * @param uid      User ID
     * @param password Password
     * @return User Object
     */
    User updatePassword(Integer uid, String password);

}
