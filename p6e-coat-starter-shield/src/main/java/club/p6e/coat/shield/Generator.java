package club.p6e.coat.shield;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
     OutputStream execute(HttpServletRequest request, HttpServletResponse response, Parameter parameter);

}
