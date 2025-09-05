package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import io.r2dbc.spi.Readable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * User Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(UserRepository.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class UserRepositoryImpl implements UserRepository {

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

    private final DatabaseClient client;

    public UserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

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

    @Override
    public Mono<User> create(User user) {
        return null;
    }

    @SuppressWarnings("ALL")
    private Mono<String> findPasswordById(Integer id) {
        return client
                .sql(TemplateParser.execute("SELECT _user_auth.id, _user_auth.password FROM @{TABLE} AS _user_auth WHERE _user_auth.id = :ID ; ", "TABLE", getUserAuthTableName()))
                .bind("ID", id)
                .map(readable -> TransformationUtil.objectToString(readable.get("password")))
                .first();
    }

    public String getUserTableName() {
        return "p6e_user";
    }

    public String getUserAuthTableName() {
        return "p6e_user_auth";
    }

}
