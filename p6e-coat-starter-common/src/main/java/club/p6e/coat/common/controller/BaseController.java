package club.p6e.coat.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BaseController {

    public static String AUTH_HEADER = "Authorization";
    public static String AUTH_HEADER_TOKEN_TYPE = "Bearer";
    public static String AUTH_HEADER_TOKEN_PREFIX = AUTH_HEADER_TOKEN_TYPE + " ";
    public static String ACCESS_TOKEN_PARAM1 = "accessToken";
    public static String ACCESS_TOKEN_PARAM2 = "access_token";
    public static String ACCESS_TOKEN_PARAM3 = "access-token";
    public static String REFRESH_TOKEN_PARAM1 = "refreshToken";
    public static String REFRESH_TOKEN_PARAM2 = "refresh_token";
    public static String REFRESH_TOKEN_PARAM3 = "refresh-token";
    public static String COOKIE_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static String COOKIE_REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";

    /**
     * 注入的日志对象
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    /**
     * 获取是否包含 javax.servlet.ServletRequest 对象
     *
     * @return 是否包含 javax.servlet.ServletRequest 对象
     */
    public static boolean isServletRequest() {
        boolean bool = true;
        try {
            Class.forName("jakarta.servlet.ServletRequest");
        } catch (ClassNotFoundException e) {
            bool = false;
        }
        return bool;
    }

}
