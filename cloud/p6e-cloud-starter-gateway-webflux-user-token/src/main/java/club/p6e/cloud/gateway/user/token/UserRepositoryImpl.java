package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * User Repository Impl
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

    @SuppressWarnings("ALL")
    @Override
    public Mono<Map<String, Object>> get(Integer id) {
        return client.sql(TemplateParser.execute(TemplateParser.execute("""
                        SELECT
                            _user.id_,
                            _user.status_,
                            _user.enable_,
                            _user.internal_,
                            _user.administrator_,
                            _user.account_,
                            _user.phone_,
                            _user.mailbox_,
                            _user.name_,
                            _user.nickname_,
                            _user.language_,
                            _user.avatar_,
                            _user.description_
                        FROM
                            @{TABLE} AS _user
                        WHERE 
                            _user.id_ = :ID
                        ;                
                        """, "TABLE", getUserTableName()
                )))
                .bind("ID", id)
                .map((readable) -> {
                    final Map<String, Object> result = new HashMap<>();
                    result.put("id", TransformationUtil.objectToString(readable.get("id_")));
                    result.put("status", TransformationUtil.objectToString(readable.get("status_")));
                    result.put("enable", TransformationUtil.objectToString(readable.get("enable_")));
                    result.put("internal", TransformationUtil.objectToString(readable.get("internal_")));
                    result.put("administrator", TransformationUtil.objectToString(readable.get("administrator_")));
                    result.put("account", TransformationUtil.objectToString(readable.get("account_")));
                    result.put("phone", TransformationUtil.objectToString(readable.get("phone_")));
                    result.put("mailbox", TransformationUtil.objectToString(readable.get("mailbox_")));
                    result.put("name", TransformationUtil.objectToString(readable.get("name_")));
                    result.put("nickname", TransformationUtil.objectToString(readable.get("nickname_")));
                    result.put("language", TransformationUtil.objectToString(readable.get("language_")));
                    result.put("avatar", TransformationUtil.objectToString(readable.get("avatar_")));
                    result.put("description", TransformationUtil.objectToString(readable.get("description_")));
                    return result;
                })
                .one();
    }

}
