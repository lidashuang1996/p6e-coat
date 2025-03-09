package club.p6e.coat.auth.repository;

import club.p6e.coat.auth.model.OAuth2ClientModel;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2ClientRepository {

    /**
     * 模板对象
     */
    private final R2dbcEntityTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 模板对象
     */
    public OAuth2ClientRepository(R2dbcEntityTemplate template) {
        this.template = template;
    }

    /**
     * 根据客户端 ID 查询数据
     *
     * @param clientId 客户端 ID
     * @return Mono/UserAuthModel 用户认证模型对象
     */
    public Mono<OAuth2ClientModel> findByClientId(String clientId) {
        return template.selectOne(
                Query.query(Criteria.where(OAuth2ClientModel.CLIENT_ID).is(clientId).and(OAuth2ClientModel.IS_DELETED).is(0)),
                OAuth2ClientModel.class
        );
    }

}
