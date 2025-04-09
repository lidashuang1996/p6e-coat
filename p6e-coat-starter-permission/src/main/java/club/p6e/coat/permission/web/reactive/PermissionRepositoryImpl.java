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
@ConditionalOnMissingBean(
        value = PermissionRepository.class,
        ignored = PermissionRepositoryImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class PermissionRepositoryImpl implements PermissionRepository {

    /**
     * TABLE PREFIX
     */
    private static final String TABLE_PREFIX = "p6e_";

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

    @SuppressWarnings("ALL")
    @Override
    public Mono<List<PermissionDetails>> getPermissionDetailsList(Integer page, Integer size) {
        System.out.println(
                TemplateParser.execute("""
                        SELECT
                            "PU"."oid",
                            "PU"."pid",
                            "PU"."url",
                            "PU"."base_url",
                            "PU"."method",
                            "PG"."mark",
                            "PG"."weight",
                            "PUG"."config",
                            "PUG"."config",
                            "PUG"."attribute"
                        FROM
                            (
                                SELECT
                                    "id",
                                    "oid",
                                    "pid",
                                    "url",
                                    "base_url",
                                    "method"
                                FROM
                                    "@{TABLE_PREFIX}permission_url"
                                ORDER BY
                                    "id"
                                    ASC
                                LIMIT 
                                    :LIMIT
                                OFFSET
                                    :OFFSET
                            ) AS "PU"
                            LEFT JOIN 
                                "@{TABLE_PREFIX}permission_url_group_mapper_url" AS "PUG" 
                                ON "PU"."id" = "PUG"."uid"
                            LEFT JOIN 
                                "@{TABLE_PREFIX}permission_url_group" AS "PG" 
                                ON "PG"."id" = "PUG"."gid"
                        """, "TABLE_PREFIX", TABLE_PREFIX
                )
        );
        return client.sql(TemplateParser.execute("""
                        SELECT
                            "PU"."oid",
                            "PU"."pid",
                            "PU"."url",
                            "PU"."base_url",
                            "PU"."method",
                            "PG"."mark",
                            "PG"."weight",
                            "PUG"."config",
                            "PUG"."config",
                            "PUG"."attribute"
                        FROM
                            (
                                SELECT
                                    "id",
                                    "oid",
                                    "pid",
                                    "url",
                                    "base_url",
                                    "method"
                                FROM
                                    "@{TABLE_PREFIX}permission_url"
                                ORDER BY
                                    "id"
                                    ASC
                                LIMIT 
                                    :LIMIT
                                OFFSET
                                    :OFFSET
                            ) AS "PU"
                            LEFT JOIN 
                                "@{TABLE_PREFIX}permission_url_group_mapper_url" AS "PUG" 
                                ON "PU"."id" = "PUG"."uid"
                            LEFT JOIN 
                                "@{TABLE_PREFIX}permission_url_group" AS "PG" 
                                ON "PG"."id" = "PUG"."gid"
                        """, "TABLE_PREFIX", TABLE_PREFIX
                ))
                .bind("LIMIT", size)
                .bind("OFFSET", (page - 1) * size)
                .map((row) -> {
                    final PermissionDetails details = new PermissionDetails();
                    details.setOid(TransformationUtil.objectToInteger(row.get("oid")));
                    details.setPid(TransformationUtil.objectToInteger(row.get("pid")));
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
