package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.user.SimpleUserModel;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

/**
 * User Auth Repository Impl [ DEFAULT: PostgreSQL ]
 *
 * @author lidashuang
 * @version 1.0
 */
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private static final String TABLE = "ss_user_auth";

    @SuppressWarnings("ALL")
    private static final String BASE_SELECT_SQL = """
            SELECT "id", "account", "phone", "mailbox", "password" FROM "@{TABLE}"
            """;

    private final DatabaseClient client;

    public UserAuthRepositoryImpl(DatabaseClient client) {
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
    public Mono<Object> init() {
        return null;
    }

    @Override
    public Mono<User> findById(Integer id) {
        return client
                .sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"id\" = :ID ; ", "TABLE", TABLE))
                .bind("ID", id)
                .map(this::convertReadableToUser)
                .first();
    }

    @Override
    public Mono<User> findByAccount(String account) {
        return client
                .sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"account\" = :ACCOUNT ; ", "TABLE", TABLE))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first();
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        return client
                .sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"phone\" = :PHONE ; ", "TABLE", TABLE))
                .bind("PHONE", phone)
                .map(this::convertReadableToUser)
                .first();
    }

    @Override
    public Mono<User> findByMailbox(String mailbox) {
        return client
                .sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"mailbox\" = :MAILBOX ; ", "TABLE", TABLE))
                .bind("MAILBOX", mailbox)
                .map(this::convertReadableToUser)
                .first();
    }

    @Override
    public Mono<User> findByPhoneOrMailbox(String account) {
        return client
                .sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"phone\" = :ACCOUNT \" OR \"account\" = :ACCOUNT ; ", "TABLE", TABLE))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first();
    }

    @Override
    public Mono<Long> updatePassword(Integer id, String password) {
        return findById(id).flatMap(u -> client
                .sql(TemplateParser.execute("UPDATE @{TABLE} "
                        + "SET \"password\" = :PASSWORD WHERE \"id\" = :ID ; ", "TABLE", TABLE))
                .bind("ID", id)
                .bind("PASSWORD", password)
                .fetch()
                .rowsUpdated());
    }

    @Override
    public Mono<User> create(User user, String password) {
        return null;
    }

}
