package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.TemplateParser;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * User Auth Repository Impl [ DEFAULT: PostgreSQL ]
 *
 * @author lidashuang
 * @version 1.0
 */
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private static final String TABLE = "p6e_user_auth";

    @SuppressWarnings("ALL")
    private static final String BASE_SELECT_SQL = """
                SELECT "id", "account", "phone", "mailbox", "password" FROM "@{TABLE}"
            """;

    private final DatabaseClient client;

    public UserAuthRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Mono<Object> init() {
        // verify if there are tables in the database
        // auto create tables
        return client.sql("SELECT EXISTS ( SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'your_table_name'");
    }

    @Override
    public Mono<User> findById(Integer id) {
        final String sql = TemplateParser.execute(BASE_SELECT_SQL + "  WHERE  \"" + id + "\"  =  :ID", new HashMap<>() {{
            put("TABLE", TABLE);
        }});
        return client.sql(sql).bind("ID", id).flatMap(result -> {
            return result.map(r -> {
                
            });
        });
    }

    @Override
    public Mono<User> findByAccount(String account) {
        return null;
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        return null;
    }

    @Override
    public Mono<User> findByMailbox(String mailbox) {
        return null;
    }

    @Override
    public Mono<User> findByPhoneOrMailbox(String account) {
        return null;
    }

    @Override
    public Mono<Long> updatePassword(String id, String password) {
        return null;
    }

    @Override
    public Mono<User> create(User user, String password) {
        return null;
    }

}
