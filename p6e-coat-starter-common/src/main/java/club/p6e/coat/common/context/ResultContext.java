package club.p6e.coat.common.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 请求结果的封装
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public final class ResultContext implements Serializable {

    /**
     * 默认的状态码
     */
    private static final int DEFAULT_CODE = 0;

    /**
     * 默认的消息内容
     */
    private static final String DEFAULT_MESSAGE = "SUCCESS";

    /**
     * 默认的数据内容
     */
    private static final String DEFAULT_DATA = null;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据的对象
     */
    private Object data;

    /**
     * 编译方法
     *
     * @return 结果上下文对象
     */
    public static ResultContext build() {
        return new ResultContext(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_DATA);
    }

    /**
     * 编译方法
     *
     * @param data 数据的对象
     * @return 结果上下文对象
     */
    public static ResultContext build(Object data) {
        return new ResultContext(DEFAULT_CODE, DEFAULT_MESSAGE, data);
    }

    /**
     * 编译方法
     *
     * @param code    消息状态码
     * @param message 消息内容
     * @param data    数据的对象
     * @return 结果上下文对象
     */
    public static ResultContext build(Integer code, String message, Object data) {
        return new ResultContext(code, message, data);
    }

    /**
     * 构造方法初始化
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据的对象
     */
    private ResultContext(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

}

