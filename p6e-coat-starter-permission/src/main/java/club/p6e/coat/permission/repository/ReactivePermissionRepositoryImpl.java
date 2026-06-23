package club.p6e.coat.permission.repository;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.permission.PermissionDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reactive Permission Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactivePermissionRepository.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactivePermissionRepositoryImpl implements ReactivePermissionRepository {

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initialization
     *
     * @param client Database Client Object
     */
    public ReactivePermissionRepositoryImpl(DatabaseClient client) {
        this.client = client;
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
    public Mono<List<PermissionDetails>> getPermissionDetailsList(Integer page, Integer size) {
        return client.sql(TemplateParser.execute(TemplateParser.execute("""
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
                                    :LIMIT
                                OFFSET
                                    :OFFSET
                            ) AS _permission_url_table
                            LEFT JOIN 
                                @{TABLE2} AS _permission_url_group_mapper_url_table
                                ON _permission_url_table.id_ = _permission_url_group_mapper_url_table.uid_
                            LEFT JOIN 
                                @{TABLE3} AS _permission_url_group_table
                                ON _permission_url_group_mapper_url_table.gid_ = _permission_url_group_table.id_
                        """, "TABLE1", getPermissionUrlTableName(), "TABLE2", getPermissionUrlGroupMapperUrlTableName(), "TABLE3", getPermissionUrlGroupTableName()
                )))
                .bind("LIMIT", size)
                .bind("OFFSET", (page - 1) * size)
                .map((row) -> {
                    final PermissionDetails details = new PermissionDetails();
                    details.setGid(TransformationUtil.objectToInteger(row.get("gid")));
                    details.setUid(TransformationUtil.objectToInteger(row.get("uid")));
                    details.setUrl(TransformationUtil.objectToString(row.get("url")));
                    details.setBaseUrl(TransformationUtil.objectToString(row.get("base_url")));
                    details.setMethod(TransformationUtil.objectToString(row.get("method")));
                    details.setMark(TransformationUtil.objectToString(row.get("mark")));
                    details.setWeight(TransformationUtil.objectToInteger(row.get("weight")));
                    details.setConfig(TransformationUtil.objectToString(row.get("config")));
                    details.setAttribute(TransformationUtil.objectToString(row.get("attribute")));
                    final String url = details.getUrl();
                    final String baseUrl = details.getBaseUrl();
                    details.setPath((baseUrl == null ? "" : baseUrl) + (url == null ? "" : url));
                    return details;
                })
                .all()
                .collectList();
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<Map<String, List<String>>> getPermissionGroupList(Integer page, Integer size) {
        return client.sql(TemplateParser.execute(TemplateParser.execute("""
                        SELECT
                            _permission_url_group_table.id_ AS id,
                            _permission_url_group_table.parent_ AS parent
                        FROM
                            @{TABLE} AS _permission_url_group_table
                        ORDER BY
                            _permission_url_group_table.id_
                            ASC
                        LIMIT
                            :LIMIT
                        OFFSET
                            :OFFSET
                        """, "TABLE", getPermissionUrlGroupTableName()
                )))
                .bind("LIMIT", size)
                .bind("OFFSET", (page - 1) * size)
                .map((row) -> {
                    final Map<String, String> data = new HashMap<>();
                    data.put("id", TransformationUtil.objectToString(row.get("id")));
                    data.put("parent", TransformationUtil.objectToString(row.get("parent")));
                    return data;
                })
                .all()
                .collectList()
                .map(list -> {
                    final Map<String, List<String>> result = new HashMap<>();
                    for (Map<String, String> item : list) {
                        final String id = item.get("id");
                        final String parent = item.get("parent");
                        result.put(id, JsonUtil.fromJsonToList(parent, String.class));
                    }
                    return result;
                });
    }

}
