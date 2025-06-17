package club.p6e.coat.common.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Result Context
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public final class ResultContext implements Serializable {

    /**
     * Default Code
     */
    private static final int DEFAULT_CODE = 0;

    /**
     * Default Data
     */
    private static final String DEFAULT_DATA = null;

    /**
     * Default Message
     */
    private static final String DEFAULT_MESSAGE = "SUCCESS";

    /**
     * Code
     */
    private Integer code;

    /**
     * Data
     */
    private Object data;

    /**
     * Message
     */
    private String message;

    /**
     * Constructor Initialization
     *
     * @param code    Code Object
     * @param data    Data Object
     * @param message Message Object
     */
    private ResultContext(Integer code, String message, Object data) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * Build Result Context Object
     *
     * @return Result Context Object
     */
    public static ResultContext build() {
        return new ResultContext(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_DATA);
    }

    /**
     * Build Result Context Object
     *
     * @param data Data Object
     * @return Result Context Object
     */
    public static ResultContext build(Object data) {
        return new ResultContext(DEFAULT_CODE, DEFAULT_MESSAGE, data);
    }

    /**
     * Build Result Context Object
     *
     * @param code    Code Object
     * @param data    Data Object
     * @param message Message Object
     * @return Result Context Object
     */
    public static ResultContext build(Integer code, String message, Object data) {
        return new ResultContext(code, message, data);
    }

}

