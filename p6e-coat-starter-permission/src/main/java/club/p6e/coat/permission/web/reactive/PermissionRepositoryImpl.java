package club.p6e.coat.permission.web.reactive;

import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.PermissionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Permission Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(PermissionRepository.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class PermissionRepositoryImpl implements PermissionRepository {

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initializers
     *
     * @param client Database Client Object
     */
    public PermissionRepositoryImpl(DatabaseClient client) {
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

}
