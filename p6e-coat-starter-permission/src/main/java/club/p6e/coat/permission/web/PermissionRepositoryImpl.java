package club.p6e.coat.permission.web;

import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.PermissionRepository;
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
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class PermissionRepositoryImpl implements PermissionRepository {

    /**
     * Jdbc Template Object
     */
    private final JdbcTemplate template;

    /**
     * Constructor Initializers
     *
     * @param template Jdbc Template Object
     */
    public PermissionRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("ALL")
    @Override
    public List<PermissionDetails> getPermissionDetailsList(Integer page, Integer size) {
        return template.query("""
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
                                        "p6epermission_url"
                                    ORDER BY
                                        "id"
                                        ASC
                                    LIMIT 
                                        :LIMIT
                                    OFFSET
                                        :OFFSET
                                ) AS "PU"
                                LEFT JOIN 
                                    "p6epermission_url_group_mapper_url" AS "PUG" 
                                    ON "PU"."id" = "PUG"."uid"
                                LEFT JOIN 
                                    "p6epermission_url_group" AS "PG" 
                                    ON "PG"."id" = "PUG"."gid"
                        """,
                new ResultSetExtractor<List<PermissionDetails>>() {
                    @Override
                    public List<PermissionDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        final List<PermissionDetails> list = new ArrayList<>();
                        while (rs.next()) {
                            final PermissionDetails details = new PermissionDetails();
                            details.setOid(rs.getInt("oid"));
                            details.setPid(rs.getInt("pid"));
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
