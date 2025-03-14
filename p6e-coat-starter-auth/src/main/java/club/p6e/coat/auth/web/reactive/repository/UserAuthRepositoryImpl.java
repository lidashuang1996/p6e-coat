package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.TemplateParser;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * User Auth Repository Impl [ DEFAULT: PostgreSQL ]
 *
 * @author lidashuang
 * @version 1.0
 */
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private static final String TABLE_NAME = "ss_user_auth";

    @SuppressWarnings("ALL")
    private static final String BASE_SELECT_SQL = """
                SELECT "id", "account", "phone", "mailbox", "password", "creator", "modifier", "creation_date_time", "modification_date_time", "version" FROM "@{TABLE_NAME}"
            """;

    private final DatabaseClient client;

    public UserAuthRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Mono<Object> init() {
        // verify if there are tables in the database
        final String sql1 = TemplateParser.execute("SELECT EXISTS ( SELECT 1 FROM " +
                "\"information_schema\".\"tables\" WHERE \"table_name\" = \"@{TABLE_NAME}\" );", "TABLE_NAME", TABLE_NAME);
        final String sql2 = TemplateParser.execute("INSERT INTO \"@{TABLE_NAME}\" ( " + "\"id\", " +
                "\"account\", \"phone\", \"mailbox\", \"password\", \"creator\", \"modifier\", \"creation_date_time\", " +
                "\"modification_date_time\", \"version\", \"qq\" ) VALUES (\"account\", \"phone\", \"mailbox\", \"password\", " +
                "\"creator\", \"modifier\", \"creation_date_time\", \"modification_date_time\", \"version\" );", TABLE_NAME);
        return client
                .sql(sql1)
                .map(row -> {
                    final Map<String, Object> data = new HashMap<>();
                    data.put("id", row.get("id"));
                    data.put("account", row.get("account"));
                    data.put("phone", row.get("phone"));
                    data.put("mailbox", row.get("mailbox"));
                    data.put("password", row.get("password"));
                    data.put("creator", row.get("creator"));
                    data.put("modifier", row.get("modifier"));
                    data.put("creation_date_time", row.get("creation_date_time"));
                    data.put("modification_date_time", row.get("modification_date_time"));
                    data.put("version", row.get("version"));
                    return data;
                })
                .all()
                .switchIfEmpty(client
                        .sql(sql2)
                        .bind("account", "123")
                        .flatMap(result ->
                                result.map(row -> {
                                    final Map<String, Object> data = new HashMap<>();
                                    data.put("data", row.get(0));
                                    return data;
                                })
                        )
                )
                .collectList()
                .map(l -> l);
    }

    @Override
    public Mono<User> findById(Integer id) {
//        final String sql = TemplateParser.execute(BASE_SELECT_SQL + "  WHERE  \"" + id + "\"  =  :ID", new HashMap<>() {{
//            put("TABLE_PREFIX", TABLE_PREFIX);
//        }});
//        return client.sql();
        return null;
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
