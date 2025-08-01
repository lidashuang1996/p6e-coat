package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import reactor.core.publisher.Mono;

/**
 * User Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface UserRepository {

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
     * Query By MailBox Account
     *
     * @param mailbox MailBox Account
     * @return User Object
     */
    Mono<User> findByMailbox(String mailbox);

    /**
     * Query By Phone Account Or Email Account
     *
     * @param content Phone Account Or Mailbox Account
     * @return User Object
     */
    Mono<User> findByPhoneOrMailbox(String content);

    Mono<User> create(User user);
}
