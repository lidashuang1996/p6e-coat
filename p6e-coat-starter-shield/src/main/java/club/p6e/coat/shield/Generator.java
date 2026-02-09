package club.p6e.coat.shield;

import org.springframework.web.server.ServerWebExchange;

import java.io.OutputStream;

public interface Generator {

    /**
     * 名称
     * @return 名称
     */
    String name();

     /**
      * 生成
      * @param exchange 上下文对象
      * @return 结果上下文对象
      */
     OutputStream execute(ServerWebExchange exchange, Parameter parameter);

}
