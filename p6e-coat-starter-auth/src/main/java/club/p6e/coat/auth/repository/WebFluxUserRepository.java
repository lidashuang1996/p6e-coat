package club.p6e.coat.auth.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.model.UserModel;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * User Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WebFluxUserRepository {

    /**
     * 根据 ID 查询数据
     *
     * @param id ID
     * @return Mono/UserModel 用户模型对象
     */
    Mono<UserModel> findById(Integer id);

    /**
     * 根据账号查询数据
     *
     * @param account 账号
     * @return Mono/UserModel 用户模型对象
     */
    Mono<User> findByAccount(String account)

    /**
     * 创建用户
     *
     * @param model 用户模型对象
     * @return 用户模型对象
     */
    Mono<User> create(UserModel model);

    Mono<User> findByPhone(String phone);

    /**
     * 创建用户
     *
     * @param model 用户模型对象
     * @return 用户模型对象
     */
    Mono<UserModel> createPhone(UserModel model) {
        model
                .setId(null)
                .setStatus(0)
                .setEnabled(1)
                .setInternal(0)
                .setAdministrator(0)
                .setAccount(null)
                .setMailbox(null)
                .setName(model.getPhone())
                .setNickname(model.getPhone())
                .setAvatar("default.jpg")
                .setDescription("")
                .setVersion(0)
                .setIsDeleted(0)
                .setCreator("register_sys")
                .setModifier("register_sys")
                .setCreationDateTime(LocalDateTime.now())
                .setModificationDateTime(LocalDateTime.now());
        return template.insert(model);
    }

    /**
     * 根据邮箱查询数据
     *
     * @param mailbox 邮箱
     * @return Mono/UserModel 用户模型对象
     */
    Mono<User> findByMailbox(String mailbox);

    /**
     * 创建用户
     *
     * @param model 用户模型对象
     * @return 用户模型对象
     */
    public Mono<UserModel> createMailbox(UserModel model) {
        model
                .setId(null)
                .setStatus(0)
                .setEnabled(1)
                .setInternal(0)
                .setAdministrator(0)
                .setAccount(null)
                .setPhone(null)
                .setName(model.getMailbox())
                .setNickname(model.getMailbox())
                .setAvatar("default.jpg")
                .setDescription("")
                .setVersion(0)
                .setIsDeleted(0)
                .setCreator("register_sys")
                .setModifier("register_sys")
                .setCreationDateTime(LocalDateTime.now())
                .setModificationDateTime(LocalDateTime.now());
        return template.insert(model);
    }

    /**
     * 根据手机号码或者邮箱查询数据
     *
     * @param content ID
     * @return Mono/UserModel 用户模型对象
     */
    Mono<User> findByPhoneOrMailbox(String content);

    Mono<UserModel> createPhoneOrMailbox(UserModel model);

}
