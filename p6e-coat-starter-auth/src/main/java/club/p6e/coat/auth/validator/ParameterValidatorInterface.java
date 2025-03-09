package club.p6e.coat.auth.validator;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 参数验证器接口
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ParameterValidatorInterface {

    /**
     * 执行顺序，越小越先执行
     *
     * @return 排序
     */
    int order();

    /**
     * 选择验证对象的类型
     *
     * @return 验证对象的类型
     */
    Class<?> select();

    /**
     * 执行验证
     *
     * @param exchange ServerWebExchange 对象
     * @param data     待验证数据的对象
     * @return 验证结果
     */
    Mono<Boolean> execute(ServerWebExchange exchange, Object data);

}
