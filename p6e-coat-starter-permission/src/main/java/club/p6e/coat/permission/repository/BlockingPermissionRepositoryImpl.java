package club.p6e.coat.permission.repository;

import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.permission.PermissionDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Blocking Permission Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(BlockingPermissionRepository.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class BlockingPermissionRepositoryImpl implements BlockingPermissionRepository {

    /**
     * Jdbc Template Object
     */
    private final JdbcTemplate template;

    /**
     * Constructor Initializers
     *
     * @param template Jdbc Template Object
     */
    public BlockingPermissionRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Get Permission Url Table Name
     *
     * @return Permission Url Table Name
     */
    public String getPermissionUrlTableName() {
        return "p6e_permission_url";
    }

    /**
     * Get Permission Url Group Table Name
     *
     * @return Permission Url Group Table Name
     */
    public String getPermissionUrlGroupTableName() {
        return "p6e_permission_url_group";
    }

    /**
     * Get Permission Url Group Mapper Url Table Name
     *
     * @return Permission Url Group Mapper Url Table Name
     */
    public String getPermissionUrlGroupMapperUrlTableName() {
        return "p6e_permission_url_group_mapper_url";
    }

    @SuppressWarnings("ALL")
    @Override
    public List<PermissionDetails> getPermissionDetailsList(Integer page, Integer size) {
        return template.query(TemplateParser.execute(TemplateParser.execute("""
                SELECT
                    _permission_url_table.url,
                    _permission_url_table.base_url,
                    _permission_url_table.method,
                    _permission_url_group_table.mark,
                    _permission_url_group_table.weight,
                    _permission_url_group_mapper_url_table.gid,
                    _permission_url_group_mapper_url_table.uid,
                    _permission_url_group_mapper_url_table.config,
                    _permission_url_group_mapper_url_table.attribute
                FROM
                    (
                        SELECT
                            _permission_url.id,
                            _permission_url.url,
                            _permission_url.base_url,
                            _permission_url.method
                        FROM
                            @{TABLE1} AS _permission_url
                        ORDER BY
                            _permission_url.id
                            ASC
                        LIMIT 
                            :LIMIT
                        OFFSET
                            :OFFSET
                    ) AS _permission_url_table
                    LEFT JOIN 
                        @{TABLE2} AS _permission_url_group_mapper_url_table
                        ON _permission_url_table.id = _permission_url_group_mapper_url_table.uid
                    LEFT JOIN 
                        @{TABLE3} AS _permission_url_group_table
                        ON _permission_url_group_mapper_url_table.gid = _permission_url_group_table.id
                """, "TABLE1", getPermissionUrlTableName(), "TABLE2", getPermissionUrlGroupMapperUrlTableName(), "TABLE3", getPermissionUrlGroupTableName()
        )), new ResultSetExtractor<List<PermissionDetails>>() {
            @Override
            public List<PermissionDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
                final List<PermissionDetails> list = new ArrayList<>();
                while (rs.next()) {
                    final PermissionDetails details = new PermissionDetails();
                    details.setGid(rs.getInt("gid"));
                    details.setUid(rs.getInt("uid"));
                    details.setUrl(rs.getString("url"));
                    details.setBaseUrl(rs.getString("base_url"));
                    details.setMethod(rs.getString("method"));
                    details.setMark(rs.getString("mark"));
                    details.setWeight(rs.getInt("weight"));
                    details.setConfig(rs.getString("config"));
                    details.setAttribute(rs.getString("attribute"));
                    final String url = details.getUrl();
                    final String baseUrl = details.getBaseUrl();
                    details.setPath((baseUrl == null ? "" : baseUrl) + (url == null ? "" : url));
                    list.add(details);
                }
                return list;
            }
        });
    }

}
