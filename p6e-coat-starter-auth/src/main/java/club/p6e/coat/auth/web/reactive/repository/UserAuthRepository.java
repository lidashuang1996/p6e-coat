package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.user.User;
import reactor.core.publisher.Mono;

/**
 * User Auth Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface UserAuthRepository {

    /**
     * Init
     *
     * @return Object Data
     */
    Mono<Object> init();

    /**
     * Query By ID
     *
     * @param id ID
     * @return User Object
     */
    Mono<User> findById(Integer id);

    /**
     * Query By Account
     *
     * @param account Account
     * @return User Object
     */
    Mono<User> findByAccount(String account);

    /**
     * Query By Phone Account
     *
     * @param phone Phone Account
     * @return User Object
     */
    Mono<User> findByPhone(String phone);

    /**
     * Query By Mailbox Account
     *
     * @param mailbox 账号
     * @return User Object
     */
    Mono<User> findByMailbox(String mailbox);

    /**
     * Query By Phone Account Or Mailbox Account
     *
     * @param account Phone Account Or Mailbox Account
     * @return User Object
     */
    Mono<User> findByPhoneOrMailbox(String account);

    /**
     * Query By ID Update Password
     *
     * @param id       ID
     * @param password Password
     * @return Affected Number Count
     */
    Mono<Long> updatePassword(Integer id, String password);

    Mono<User> create(User user, String password);
}
