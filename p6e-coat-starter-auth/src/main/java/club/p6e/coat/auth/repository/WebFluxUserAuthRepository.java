package club.p6e.coat.auth.repository;

import club.p6e.coat.auth.model.UserAuthModel;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * User Auth Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxUserAuthRepository {

    /**
     * 模板对象
     */
    private final R2dbcEntityTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 模板对象
     */
    public WebFluxUserAuthRepository(R2dbcEntityTemplate template) {
        this.template = template;
    }

    /**
     * 根据 ID 查询数据
     *
     * @param id ID
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> findById(Integer id) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.ID).is(id)),
                UserAuthModel.class
        );
    }

    /**
     * 根据账号查询数据
     *
     * @param account 账号
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> findByAccount(String account) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.ACCOUNT).is(account)),
                UserAuthModel.class
        );
    }

    /**
     * 根据账号查询数据
     *
     * @param account 账号
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> findByPhone(String account) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.PHONE).is(account)),
                UserAuthModel.class
        );
    }

    /**
     * 根据账号查询数据
     *
     * @param account 账号
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> findByMailbox(String account) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.MAILBOX).is(account)),
                UserAuthModel.class
        );
    }

    /**
     * 根据账号查询数据
     *
     * @param account 账号
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> findByPhoneOrMailbox(String account) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.PHONE).is(account).or(UserAuthModel.MAILBOX).is(account)),
                UserAuthModel.class
        );
    }

    /**
     * 查询 QQ 数据
     *
     * @param qq QQ
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> findByQq(String qq) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.QQ).is(qq)),
                UserAuthModel.class
        );
    }

    /**
     * 创建数据
     *
     * @param model 用户认证模型对象
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<UserAuthModel> create(UserAuthModel model) {
        model
                .setVersion(0)
                .setCreator("register_sys")
                .setModifier("register_sys")
                .setCreationDateTime(LocalDateTime.now())
                .setModificationDateTime(LocalDateTime.now());
        return template.insert(model);
    }

    /**
     * 更新密码
     *
     * @param id       ID
     * @param password 密码数据
     * @return Mono/UserAuthModel 修改的数据条数
     */
    public Mono<Long> updatePassword(Integer id, String password) {
        return template.selectOne(
                Query.query(Criteria.where(UserAuthModel.ID).is(id)),
                UserAuthModel.class
        ).flatMap(m -> template.update(
                Query.query(Criteria.where(UserAuthModel.ID).is(id).and(UserAuthModel.VERSION).is(m.getVersion())),
                Update.update(UserAuthModel.PASSWORD, password).set(UserAuthModel.VERSION, m.getVersion() + 1),
                UserAuthModel.class
        ));
    }

}
