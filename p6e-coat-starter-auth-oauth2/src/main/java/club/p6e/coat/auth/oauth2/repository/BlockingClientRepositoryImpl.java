package club.p6e.coat.auth.oauth2.repository;

import club.p6e.coat.auth.oauth2.model.ClientModel;
import club.p6e.coat.common.utils.TemplateParser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Blocking Client Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingClientRepository.class,
        ignored = BlockingClientRepositoryImpl.class
)
@Component("club.p6e.coat.auth.oauth2.repository.BlockingClientRepositoryImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingClientRepositoryImpl implements BlockingClientRepository {

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
     * Jdbc Template Object
     */
    private final JdbcTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Jdbc Template Object
     */
    public BlockingClientRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Get Client Table Name
     *
     * @return Client Table Name
     */
    protected String getClientTableName() {
        return "p6e_oauth2_client";
    }

    @SuppressWarnings("ALL")
    @Override
    public ClientModel findByAppId(String id) {
        final List<ClientModel> list = template.query(TemplateParser.execute(
                BASE_CLIENT_SELECT_SQL + " WHERE _oauth2_client.client_id_ = ? LIMIT 1 ; ",
                "TABLE", getClientTableName()
        ), new Object[]{id}, new BeanPropertyRowMapper<>(ClientModel.class) {

            @NonNull
            @Override
            public ClientModel mapRow(@NonNull ResultSet rs, int rn) throws SQLException {
                final ClientModel model = new ClientModel();
                model.setId(rs.getInt("id_"));
                model.setEnable(rs.getInt("enable_"));
                model.setType(rs.getString("type_"));
                model.setScope(rs.getString("scope_"));
                model.setRedirectUri(rs.getString("redirect_uri_"));
                model.setReconfirm(rs.getInt("reconfirm_"));
                model.setClientName(rs.getString("client_name_"));
                model.setClientAvatar(rs.getString("client_avatar_"));
                model.setClientDescription(rs.getString("client_description_"));
                model.setCreator(rs.getString("creator_"));
                model.setModifier(rs.getString("modifier_"));
                model.setCreationDateTime(rs.getTimestamp("creation_date_time_").toLocalDateTime());
                model.setModificationDateTime(rs.getTimestamp("modification_date_time_").toLocalDateTime());
                return model;
            }

        });
        return list.isEmpty() ? null : list.get(0);
    }

}
