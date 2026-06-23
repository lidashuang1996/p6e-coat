package club.p6e.coat.permission.repository;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.permission.PermissionDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blocking Permission Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingPermissionRepository.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingPermissionRepositoryImpl implements BlockingPermissionRepository {

    /**
     * Jdbc Template Object
     */
    private final JdbcTemplate template;

    /**
     * Constructor Initialization
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
                        _permission_url_table.url_ AS url,
                        _permission_url_table.base_url_ AS base_url,
                        _permission_url_table.method_ AS method,
                        _permission_url_group_table.mark_ AS mark,
                        _permission_url_group_table.weight_ AS weight,
                        _permission_url_group_mapper_url_table.gid_ AS gid,
                        _permission_url_group_mapper_url_table.uid_ AS uid,
                        _permission_url_group_mapper_url_table.config_ AS config,
                        _permission_url_group_mapper_url_table.attribute_ AS attribute
                    FROM
                    (
                        SELECT
                        _permission_url.id_,
                        _permission_url.url_,
                        _permission_url.base_url_,
                        _permission_url.method_
                        FROM
                            @{TABLE1} AS _permission_url
                        ORDER BY
                            _permission_url.id_
                            ASC
                        LIMIT 
                            ?
                        OFFSET
                            ?
                    ) AS _permission_url_table
                    LEFT JOIN 
                        @{TABLE2} AS _permission_url_group_mapper_url_table
                    ON _permission_url_table.id_ = _permission_url_group_mapper_url_table.uid_
                    LEFT JOIN 
                        @{TABLE3} AS _permission_url_group_table
                    ON _permission_url_group_mapper_url_table.gid_ = _permission_url_group_table.id_
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
        }, size, (page - 1) * size);
    }

    @SuppressWarnings("ALL")
    @Override
    public Map<String, List<String>> getPermissionGroupList(Integer page, Integer size) {
        return template.query(TemplateParser.execute(TemplateParser.execute("""
                SELECT
                    _permission_url_group_table.id_ AS id,
                    _permission_url_group_table.parent_ AS parent
                FROM
                    @{TABLE} AS _permission_url_group_table
                ORDER BY
                    _permission_url_group_table.id_
                    ASC
                LIMIT
                    ?
                OFFSET
                    ?
                """, "TABLE", getPermissionUrlGroupTableName()
        )), new ResultSetExtractor<Map<String, List<String>>>() {
            @Override
            public Map<String, List<String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                final Map<String, List<String>> result = new HashMap<>();
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final String parent = rs.getString("parent");
                    result.put(String.valueOf(id), JsonUtil.fromJsonToList(parent, String.class));
                }
                return result;
            }
        }, size, (page - 1) * size);
    }


}
