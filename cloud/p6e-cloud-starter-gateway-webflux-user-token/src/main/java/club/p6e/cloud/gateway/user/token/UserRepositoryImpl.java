package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.TemplateParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * User Token Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(UserRepository.class)
public class UserRepositoryImpl implements UserRepository {

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
     * Get User Token Table Name
     *
     * @return User Token Table Name
     */
    public String getUserTableName() {
        return "p6e_user";
    }

    @Override
    public Mono<Map<String, Object>> get(Integer id) {
        return client.sql(TemplateParser.execute(TemplateParser.execute("""
                        """, "TABLE1", getUserTableName()
                )))
                .bind("ID", id)
                .map((row) -> {
                    final Map<String, Object> data = new HashMap<>();
                    return data;
                })
                .one();
    }

}
