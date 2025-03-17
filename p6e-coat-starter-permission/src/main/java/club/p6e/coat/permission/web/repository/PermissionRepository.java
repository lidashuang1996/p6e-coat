package club.p6e.coat.permission.web.repository;

import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.permission.model.PermissionModel;
import club.p6e.coat.permission.model.PermissionUrlGroupAssociationUrlModel;
import club.p6e.coat.permission.model.PermissionUrlGroupModel;
import club.p6e.coat.permission.model.PermissionUrlModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Permission Repository
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class PermissionRepository {

    /**
     * R2dbcEntityTemplate object
     */
    private final R2dbcEntityTemplate template;

    /**
     * Constructor initializers
     *
     * @param template R2dbcEntityTemplate object
     */
    public PermissionRepository(R2dbcEntityTemplate template) {
        this.template = template;
    }

    /**
     * Query permission list
     *
     * @param page Page Length
     * @param size Size Length
     * @return PermissionModel/List/Mono object
     */
    public Mono<List<PermissionModel>> findPermissionList(Integer page, Integer size) {
        return findPermissionUrl(page, size)
                .flatMap(this::findPermissionUrlGroupAssociationUrl)
                .flatMap(this::findPermissionUrlGroup)
                .collectList();
    }

    /**
     * Query permission url list
     *
     * @param page Page Length
     * @param size Size Length
     * @return PermissionModel/Flux object
     */
    private Flux<PermissionModel> findPermissionUrl(Integer page, Integer size) {
        return template.select(Query
                .empty()
                .sort(Sort.by(Sort.Order.asc(PermissionUrlModel.ID)))
                .offset((long) (page - 1) * size)
                .limit(size), PermissionUrlModel.class
        ).map(m -> new PermissionModel()
                .setOid(m.getOid())
                .setPid(m.getPid())
                .setUid(m.getId())
                .setUUrl(m.getUrl())
                .setUBaseUrl(m.getBaseUrl())
                .setUMethod(m.getMethod())
        );
    }

    /**
     * Query permission url group association url list
     *
     * @param pm PermissionModel object
     * @return PermissionModel/Flux object
     */
    private Flux<PermissionModel> findPermissionUrlGroupAssociationUrl(PermissionModel pm) {
        return template.select(Query
                .query(Criteria
                        .where(PermissionUrlGroupAssociationUrlModel.UID).is(pm.getUid())
                        .and(PermissionUrlGroupAssociationUrlModel.OID).is(pm.getOid())
                        .and(PermissionUrlGroupAssociationUrlModel.PID).is(pm.getPid())
                ), PermissionUrlGroupAssociationUrlModel.class
        ).map(m -> CopyUtil.run(pm, PermissionModel.class).setGid(m.getGid()).setRConfig(m.getConfig()).setRAttribute(m.getAttribute()));
    }

    /**
     * Query permission url group
     *
     * @param pm PermissionModel object
     * @return PermissionModel/Flux object
     */
    private Mono<PermissionModel> findPermissionUrlGroup(PermissionModel pm) {
        return template.selectOne(
                Query.query(Criteria
                        .where(PermissionUrlGroupModel.ID).is(pm.getGid())
                        .and(PermissionUrlGroupModel.OID).is(pm.getOid())
                        .and(PermissionUrlGroupModel.PID).is(pm.getPid())
                ),
                PermissionUrlGroupModel.class
        ).map(m -> pm.setGMark(m.getMark()).setGWeight(m.getWeight()));
    }

}
