package club.p6e.coat.auth.oauth2.repository;

import club.p6e.coat.auth.oauth2.model.ClientModel;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.utils.TemplateParser;
import io.r2dbc.spi.Readable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive User Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveUserRepository.class,
        ignored = ReactiveUserRepositoryImpl.class
)
@Component("club.p6e.coat.auth.repository.ReactiveUserRepositoryImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveUserRepositoryImpl implements ReactiveClientRepository {

    /**
     * Base Client Select SQL
     */
    private static final String BASE_CLIENT_SELECT_SQL = """
            SELECT
                _oauth2_client.id_ AS id_,
                _oauth2_client.enable_ AS enable_,
                _oauth2_client.type_ AS type_,
                _oauth2_client.scope_ AS scope_,
                _oauth2_client.redirect_uri_ AS redirect_uri_,
                _oauth2_client.reconfirm_ AS reconfirm_,
                _oauth2_client.client_id_ AS client_id_,
                _oauth2_client.client_secret_ AS client_secret_,
                _oauth2_client.client_name_ AS client_name_,
                _oauth2_client.client_avatar_ AS client_avatar_,
                _oauth2_client.client_description_ AS client_description_,
                _oauth2_client.creator_ AS creator_,
                _oauth2_client.modifier_ AS modifier_,
                _oauth2_client.creation_date_time_ AS creation_date_time_,
                _oauth2_client.modification_date_time_ AS modification_date_time_,
                _oauth2_client.version_ AS version_
            FROM
                @{TABLE} AS _oauth2_client
            """;

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initialization
     *
     * @param client Database Client Object
     */
    public ReactiveUserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    /**
     * Covert Readable To User Object
     *
     * @param readable Readable Object
     * @return User Object
     */
    private ClientModel convertReadableToClientModel(Readable readable) {
        final ClientModel model = new ClientModel();
        model.setId(readable.get("id_", Integer.class));
        model.setEnable(readable.get("enable_", Integer.class));
        model.setType(readable.get("type_", String.class));
        model.setScope(readable.get("scope_", String.class));
        model.setRedirectUri(readable.get("redirect_uri_", String.class));
        model.setReconfirm(readable.get("reconfirm_", Integer.class));
        model.setClientName(readable.get("client_name_", String.class));
        model.setClientAvatar(readable.get("client_avatar_", String.class));
        model.setClientDescription(readable.get("client_description_", String.class));
        model.setCreator(readable.get("creator_", String.class));
        model.setModifier(readable.get("modifier_", String.class));
        model.setCreationDateTime(readable.get("creation_date_time_", LocalDateTime.class));
        model.setModificationDateTime(readable.get("modification_date_time_", LocalDateTime.class));
        return model;
    }

    /**
     * Get User Table Name
     *
     * @return User Table Name
     */
    protected String getClientTableName() {
        return "p6e_oauth2_client";
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<ClientModel> findByAppId(String id) {
        return client.sql(TemplateParser.execute(BASE_CLIENT_SELECT_SQL + " WHERE _oauth2_client.client_id_ = :ID LIMIT 1 ; ", "TABLE", getClientTableName()))
                .bind("ID", id)
                .map(this::convertReadableToClientModel)
                .first();
    }

}
