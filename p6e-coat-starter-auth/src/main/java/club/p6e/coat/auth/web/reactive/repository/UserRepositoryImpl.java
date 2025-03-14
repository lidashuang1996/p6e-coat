package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserModel;
import club.p6e.coat.common.utils.TemplateParser;
import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class UserRepositoryImpl implements UserRepository {

    private static final String TABLE_NAME = "ss_user";

    @SuppressWarnings("ALL")
    private static final String BASE_SELECT_SQL = """
                SELECT "id", "status", "enabled", "internal", "administrator", "account", "phone", "mailbox", "name", "nickname", "language", "avatar", "description", "creator", "modifier", "creation_date_time", "modification_date_time", "version", "is_deleted" FROM "@{TABLE_NAME}"
            """;

    private final DatabaseClient client;

    public UserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    private User convertReadableToUser(Readable readable) {

    }

    @Override
    public Mono<User> findById(Integer id) {
        final String sql = TemplateParser.execute(BASE_SELECT_SQL
                + " WHERE \"id\" = :ID ; ", "TABLE_PREFIX", TABLE_NAME);
        return client.sql(sql)
                .bind("ID", id)
                .flatMap(result -> result.map(this::convertReadableToUser))
                .collectList().filter(l -> !l.isEmpty()).map(l -> l.get(0));
    }

    @Override
    public Mono<User> findByAccount(String account) {
        final String sql = TemplateParser.execute(BASE_SELECT_SQL
                + " WHERE \"account\" = :ACCOUNT ; ", "TABLE_PREFIX", TABLE_NAME);
        return client.sql(sql)
                .bind("ACCOUNT", account)
                .flatMap(result -> result.map(this::convertReadableToUser))
                .collectList().filter(l -> !l.isEmpty()).map(l -> l.get(0));
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        final String sql = TemplateParser.execute(BASE_SELECT_SQL
                + " WHERE \"phone\" = :PHONE ; ", "TABLE_PREFIX", TABLE_NAME);
        return client.sql(sql)
                .bind("PHONE", phone)
                .flatMap(result -> result.map(this::convertReadableToUser))
                .collectList().filter(l -> !l.isEmpty()).map(l -> l.get(0));
    }

    @Override
    public Mono<User> findByMailbox(String mailbox) {
        final String sql = TemplateParser.execute(BASE_SELECT_SQL
                + " WHERE \"mailbox\" = :MAILBOX ; ", "TABLE_PREFIX", TABLE_NAME);
        return client.sql(sql)
                .bind("MAILBOX", mailbox)
                .flatMap(result -> result.map(this::convertReadableToUser))
                .collectList().filter(l -> !l.isEmpty()).map(l -> l.get(0));
    }

    @Override
    public Mono<User> findByPhoneOrMailbox(String content) {
        final String sql = TemplateParser.execute(BASE_SELECT_SQL
                + " WHERE \"phone\" = :PHONE OR \"mailbox\" = :MAILBOX ; ", "TABLE_PREFIX", TABLE_NAME);
        return client.sql(sql)
                .bind("PHONE", content)
                .bind("MAILBOX", content)
                .flatMap(result -> result.map(this::convertReadableToUser))
                .collectList().filter(l -> !l.isEmpty()).map(l -> l.get(0));
    }

    @Override
    public Mono<User> create(User user) {
        return null;
    }
}
