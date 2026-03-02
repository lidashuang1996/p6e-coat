package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * User Token Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(UserTokenRepository.class)
public class UserTokenRepositoryImpl implements UserTokenRepository {

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initialization
     *
     * @param client Database Client Object
     */
    public UserTokenRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    /**
     * Get User Token Table Name
     *
     * @return User Token Table Name
     */
    public String getUserTokenTableName() {
        return "p6e_user_token";
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<UserTokenModel> get(String token) {
        return client.sql(TemplateParser.execute(TemplateParser.execute("""
                        SELECT
                            _user_token.id_,
                            _user_token.uid_,
                            _user_token.content_
                        FROM
                            @{TABLE} AS _user_token
                        WHERE
                            content_ = '123'
                        WHERE 
                            _user_token.content_ = :TOKEN
                        ;  
                        """, "TABLE", getUserTokenTableName()
                )))
                .bind("TOKEN", token)
                .map((readable) -> {
                    final UserTokenModel model = new UserTokenModel();
                    model.setId(TransformationUtil.objectToInteger(readable.get("id_")));
                    model.setUid(TransformationUtil.objectToInteger(readable.get("uid_")));
                    model.setContent(TransformationUtil.objectToString(readable.get("content_")));
                    return model;
                })
                .one();
    }

}
