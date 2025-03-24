package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.user.SimpleUserModel;
import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import io.r2dbc.spi.Readable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = UserRepository.class,
        ignored = UserRepositoryImpl.class
)
public class UserRepositoryImpl implements UserRepository {

    private static final String USER_TABLE = "ss_user";
    private static final String USER_AUTH_TABLE = "ss_user_auth";

    @SuppressWarnings("ALL")
    private static final String BASE_SELECT_SQL = """
                SELECT "id", "status", "enabled", "internal", "administrator", "account", "phone",
                 "mailbox", "name", "nickname", "language", "avatar", "description" FROM "@{TABLE}"
            """;

    private final DatabaseClient client;

    public UserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    private User convertReadableToUser(Readable readable) {
        return new SimpleUserModel(
                TransformationUtil.objectToInteger(readable.get("id")),
                TransformationUtil.objectToInteger(readable.get("status")),
                TransformationUtil.objectToInteger(readable.get("enabled")),
                TransformationUtil.objectToInteger(readable.get("internal")),
                TransformationUtil.objectToInteger(readable.get("administrator")),
                TransformationUtil.objectToString(readable.get("account")),
                TransformationUtil.objectToString(readable.get("phone")),
                TransformationUtil.objectToString(readable.get("mailbox")),
                TransformationUtil.objectToString(readable.get("name")),
                TransformationUtil.objectToString(readable.get("nickname")),
                TransformationUtil.objectToString(readable.get("language")),
                TransformationUtil.objectToString(readable.get("avatar")),
                TransformationUtil.objectToString(readable.get("description"))
        );
    }

    @Override
    public Mono<User> findById(Integer id) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"id\" = :ID ; ", "TABLE", USER_TABLE))
                .bind("ID", id)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> findByAccount(String account) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"account\" = :ACCOUNT ; ", "TABLE", USER_TABLE))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> {
                    System.out.println("bbbbbbbbbbb ?11111 ");
                    System.out.println("uuu ::" + u);
                    return findPasswordById(Integer.valueOf(u.id())).map(u::password);
                });
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"phone\" = :PHONE ; ", "TABLE", USER_TABLE))
                .bind("PHONE", phone)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> findByMailbox(String mailbox) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"mailbox\" = :MAILBOX ; ", "TABLE", USER_TABLE))
                .bind("MAILBOX", mailbox)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> findByPhoneOrMailbox(String account) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"phone\" = :ACCOUNT OR \"mailbox\" = :ACCOUNT ; ", "TABLE", USER_TABLE))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> create(User user) {
        return null;
    }

    private Mono<String> findPasswordById(Integer id) {
        return client
                .sql(TemplateParser.execute("SELECT \"id\", \"password\" FROM "
                        + "\"@{TABLE}\" WHERE \"id\" = :ID ; ", "TABLE", USER_AUTH_TABLE))
                .bind("ID", id)
                .map(readable -> {
                    System.out.println("OOOOOOOPPPPPPPPP");
                    System.out.println(readable.get("password") + "?>>>>>>>>>");
                    return TransformationUtil.objectToString(readable.get("password"));
                })
                .first();
    }

}
