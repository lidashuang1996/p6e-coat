package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.user.SimpleUserModel;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import io.r2dbc.spi.Readable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * User Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = UserRepository.class,
        ignored = UserRepositoryImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class UserRepositoryImpl implements UserRepository {

    /**
     *
     */
    private static final String BASE_SELECT_SQL = """
            SELECT
            _user.id,
            _user.status,
            _user.enabled,
            _user.internal,
            _user.administrator,
            _user.account,
            _user.phone,
            _user.mailbox,
            _user.name,
            _user.nickname,
            _user.language,
            _user.avatar,
            _user.description
            FROM
            @{TABLE} AS _user
            """;

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initialization
     *
     * @param client Database Client Object
     */
    public UserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    /**
     * Covert Readable To User Object
     *
     * @param readable Readable Object
     * @return User Object
     */
    private User convertReadableToUser(Readable readable) {
        return SpringUtil.getBean(UserBuilder.class).create(new HashMap<>() {{
            put("id", TransformationUtil.objectToString(readable.get("id")));
            put("status", TransformationUtil.objectToString(readable.get("status")));
            put("enabled", TransformationUtil.objectToString(readable.get("enabled")));
            put("internal", TransformationUtil.objectToString(readable.get("internal")));
            put("administrator", TransformationUtil.objectToString(readable.get("administrator")));
            put("account", TransformationUtil.objectToString(readable.get("account")));
            put("phone", TransformationUtil.objectToString(readable.get("phone")));
            put("mailbox", TransformationUtil.objectToString(readable.get("mailbox")));
            put("name", TransformationUtil.objectToString(readable.get("name")));
            put("nickname", TransformationUtil.objectToString(readable.get("nickname")));
            put("language", TransformationUtil.objectToString(readable.get("language")));
            put("avatar", TransformationUtil.objectToString(readable.get("avatar")));
            put("description", TransformationUtil.objectToString(readable.get("description")));
        }});
    }

    /**
     * Get User Table Name
     *
     * @return User Table Name
     */
    protected String getUserTableName() {
        return "p6e_user";
    }

    /**
     * Get User Auth Table Name
     *
     * @return User Auth Table Name
     */
    protected String getUserAuthTableName() {
        return "p6e_user_auth";
    }

    /**
     * Query Password By Id
     *
     * @param id User ID
     * @return Password
     */
    @SuppressWarnings("ALL")
    protected Mono<String> findPasswordById(Integer id) {
        return client
                .sql(TemplateParser.execute("SELECT _user_auth.id, _user_auth.password FROM @{TABLE} AS _user_auth WHERE _user_auth.id = :ID ; ", "TABLE", getUserAuthTableName()))
                .bind("ID", id)
                .map(readable -> TransformationUtil.objectToString(readable.get("password")))
                .first();
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> findById(Integer id) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL + " WHERE _user.id = :ID ; ", "TABLE", getUserTableName()))
                .bind("ID", id)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> findByAccount(String account) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL + " WHERE _user.account = :ACCOUNT ; ", "TABLE", getUserTableName()))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> findByPhone(String phone) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL + " WHERE _user.phone = :PHONE ; ", "TABLE", getUserTableName()))
                .bind("PHONE", phone)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> findByMailbox(String mailbox) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL + " WHERE _user.mailbox = :MAILBOX ; ", "TABLE", getUserTableName()))
                .bind("MAILBOX", mailbox)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> findByPhoneOrMailbox(String account) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL + " WHERE \"phone\" = :ACCOUNT OR \"mailbox\" = :ACCOUNT ; ", "TABLE", getUserTableName()))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> create(User user) {
        if (user instanceof SimpleUserModel sum) {
            return client.sql(TemplateParser.execute("""
                            INSERT INTO @{TABLE} (
                                        status ,
                                        enabled ,
                                        internal ,
                                        administrator ,
                                        account ,
                                        phone ,
                                        mailbox ,
                                        name ,
                                        nickname ,
                                        language ,
                                        avatar ,
                                        description ,
                                        creator ,
                                        modifier ,
                                        creation_date_time ,
                                        modification_date_time ,
                                        version ,
                                        is_deleted
                                    ) VALUES (
                                        :STATUS ,
                                        :ENABLED ,
                                        :INTERNAL ,
                                        :ADMINISTRATOR ,
                                        :ACCOUNT ,
                                        :PHONE ,
                                        :MAILBOX ,
                                        :NAME ,
                                        :NICKNAME ,
                                        :LANGUAGE ,
                                        :AVATAR ,
                                        :DESCRIPTION ,
                                        :CREATOR ,
                                        :MODIFIER ,
                                        :CREATION_DATE_TIME ,
                                        :MODIFICATION_DATE_TIME ,
                                        :VERSION ,
                                        :IS_DELETED
                                    )  RETURNING id ;
                            """, "TABLE", getUserAuthTableName()))
                    .bind("STATUS", sum.getStatus() == null ? 1 : sum.getStatus())
                    .bind("ENABLED", sum.getEnabled() == null ? 1 : sum.getEnabled())
                    .bind("INTERNAL", sum.getInternal() == null ? 0 : sum.getInternal())
                    .bind("ADMINISTRATOR", sum.getAdministrator() == null ? 0 : sum.getAdministrator())
                    .bind("ACCOUNT", sum.getAccount())
                    .bind("PHONE", sum.getPhone())
                    .bind("MAILBOX", sum.getMailbox())
                    .bind("NAME", sum.getName())
                    .bind("NICKNAME", sum.getNickname())
                    .bind("LANGUAGE", sum.getLanguage() == null ? "zh-cn" : sum.getLanguage())
                    .bind("AVATAR", sum.getAvatar())
                    .bind("DESCRIPTION", sum.getDescription())
                    .bind("CREATOR", "sys")
                    .bind("MODIFIER", "sys")
                    .bind("CREATION_DATE_TIME", LocalDateTime.now())
                    .bind("MODIFICATION_DATE_TIME", LocalDateTime.now())
                    .bind("VERSION", 0)
                    .bind("IS_DELETED", 0)
                    .map(r -> TransformationUtil.objectToInteger(r.get("id")))
                    .first()
                    .flatMap(i -> client.sql(TemplateParser.execute("""
                                    INSERT INTO @{TABLE} (
                                        id ,
                                        account ,
                                        phone ,
                                        mailbox ,
                                        password ,
                                        creator ,
                                        modifier ,
                                        creation_date_time ,
                                        modification_date_time ,
                                        version
                                    ) VALUES (
                                        :ID ,
                                        :ACCOUNT ,
                                        :PHONE ,
                                        :MAILBOX ,
                                        :PASSWORD ,
                                        :CREATOR ,
                                        :MODIFIER ,
                                        :CREATION_DATE_TIME ,
                                        :MODIFICATION_DATE_TIME ,
                                        :VERSION
                                    )  RETURNING id ;
                                    """, "TABLE", getUserAuthTableName()))
                            .bind("ID", i)
                            .bind("ACCOUNT", sum.getAccount())
                            .bind("PHONE", sum.getPhone())
                            .bind("MAILBOX", sum.getMailbox())
                            .bind("PASSWORD", sum.getPassword())
                            .bind("CREATOR", "sys")
                            .bind("MODIFIER", "sys")
                            .bind("CREATION_DATE_TIME", LocalDateTime.now())
                            .bind("MODIFICATION_DATE_TIME", LocalDateTime.now())
                            .bind("VERSION", 0)
                            .fetch()
                            .rowsUpdated()
                            .flatMap(l -> findById(i))
                    );
        } else {
            return Mono.empty();
        }
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<User> updatePassword(Integer uid, String password) {
        return client.sql(TemplateParser.execute("UPDATE @{TABLE} AS _user_auth SET password = :PASSWORD WHERE _user_auth.id = :ID ; ", "TABLE", getUserAuthTableName()))
                .bind("ID", uid)
                .bind("PASSWORD", password)
                .fetch()
                .rowsUpdated()
                .flatMap(r -> findById(uid));
    }

}
