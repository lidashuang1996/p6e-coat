package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

/**
 * User Token Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
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
        return client.sql(TemplateParser.execute("""
                        SELECT
                            _user_token.id_ AS id_,
                            _user_token.uid_ AS uid_,
                            _user_token.content_ AS content_,
                            _user_token.end_date_time_ AS end_date_time_,
                            _user_token.start_date_time_ AS start_date_time_
                        FROM
                            @{TABLE} AS _user_token
                        WHERE 
                            _user_token.content_ = :TOKEN
                        ;  
                        """, "TABLE", getUserTokenTableName()
                ))
                .bind("TOKEN", token)
                .map((readable) -> {
                    final Object endDateTime = readable.get("end_date_time_");
                    final Object startDateTime = readable.get("start_date_time_");
                    final UserTokenModel model = new UserTokenModel();
                    model.setId(TransformationUtil.objectToInteger(readable.get("id_")));
                    model.setUid(TransformationUtil.objectToInteger(readable.get("uid_")));
                    model.setContent(TransformationUtil.objectToString(readable.get("content_")));
                    model.setEndDateTime(endDateTime == null ? null : ((OffsetDateTime) endDateTime).toLocalDateTime());
                    model.setStartDateTime(startDateTime == null ? null : ((OffsetDateTime) startDateTime).toLocalDateTime());
                    return model;
                })
                .first();
    }

}
