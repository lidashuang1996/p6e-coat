package club.p6e.coat.common.global;

import club.p6e.coat.common.controller.BaseController;
import club.p6e.coat.common.controller.BlockingWebUtil;
import club.p6e.coat.common.controller.ReactiveWebUtil;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class Globals {

    /**
     * 是否为调试模式
     */
    private static boolean DEBUG = false;

    /**
     * 语言的信息头
     */
    @SuppressWarnings("ALL")
    private static final String USER_LANGUAGE_HEADER = "P6e-Language";

    /**
     * 项目的信息头
     */
    @SuppressWarnings("ALL")
    private static final String USER_PROJECT_HEADER = "P6e-User-Project";

    /**
     * 语言的信息头
     */
    @SuppressWarnings("ALL")
    private static final String USER_ORGANIZATION_HEADER = "P6e-User-Organization";

    /**
     * 用户的信息头
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * 用户的权限头
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_PERMISSION = "P6e-User-Permission";

    public static void setDebug() {
        DEBUG = true;
    }

    private static final ThreadLocal<String> USER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> PERMISSION_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> PROJECT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> ORGANIZATION_THREAD_LOCAL = new ThreadLocal<>();


    public static void setUser(String user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static void setUserPermission(String permission) {
        PERMISSION_THREAD_LOCAL.set(permission);
    }

    public static void setUserProject(String project) {
        PROJECT_THREAD_LOCAL.set(project);
    }

    public static void setUserOrganization(String organization) {
        ORGANIZATION_THREAD_LOCAL.set(organization);
    }

    public static void clean() {
        USER_THREAD_LOCAL.remove();
        PERMISSION_THREAD_LOCAL.remove();
        PROJECT_THREAD_LOCAL.remove();
        ORGANIZATION_THREAD_LOCAL.remove();
    }

    public static GlobalUserInfo getUserInfo() {
        if (BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserInfo.DEBUG;
                } else {
                    String result = USER_THREAD_LOCAL.get();
                    if (result == null) {
                        result = BlockingWebUtil.getHeader(USER_INFO_HEADER);
                    }
                    return (result == null || result.isEmpty()) ? null : JsonUtil.fromJson(result, GlobalUserInfo.class);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static GlobalUserInfo getUserInfo(ServerHttpRequest request) {
        if (!BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserInfo.DEBUG;
                } else {
                    String result = USER_THREAD_LOCAL.get();
                    if (result == null) {
                        result = ReactiveWebUtil.getHeader(request, USER_INFO_HEADER);
                    }
                    return (result == null || result.isEmpty()) ? null : JsonUtil.fromJson(result, GlobalUserInfo.class);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getUserInfoContent() {
        if (BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return JsonUtil.toJson(GlobalUserInfo.DEBUG);
                } else {
                    String result = USER_THREAD_LOCAL.get();
                    if (result == null) {
                        result = BlockingWebUtil.getHeader(USER_INFO_HEADER);
                    }
                    return (result == null || result.isEmpty()) ? null : result;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getUserInfoContent(ServerHttpRequest request) {
        final GlobalUserInfo data = getUserInfo(request);
        return data == null ? null : JsonUtil.toJson(data);
    }

    public static GlobalUserPermission getUserPermission() {
        if (BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserPermission.DEBUG;
                } else {
                    String result = PERMISSION_THREAD_LOCAL.get();
                    if (result == null) {
                        result = BlockingWebUtil.getHeader(USER_INFO_PERMISSION);
                    }
                    return (result == null || result.isEmpty()) ? null : JsonUtil.fromJson(result, GlobalUserPermission.class);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static GlobalUserPermission getUserPermission(ServerHttpRequest request) {
        if (!BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserPermission.DEBUG;
                } else {
                    String result = PERMISSION_THREAD_LOCAL.get();
                    if (result == null) {
                        result = ReactiveWebUtil.getHeader(request, USER_INFO_PERMISSION);
                    }
                    return (result == null || result.isEmpty()) ? null : JsonUtil.fromJson(result, GlobalUserPermission.class);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getUserProject() {
        if (BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserProject.DEBUG.getProject();
                } else {
                    String result = PROJECT_THREAD_LOCAL.get();
                    if (result == null) {
                        result = BlockingWebUtil.getHeader(USER_PROJECT_HEADER);
                    }
                    return (result == null || result.isEmpty()) ? null : result;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getUserProject(ServerHttpRequest request) {
        return "20000";
    }

    public static String getUserOrganization() {
        if (BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserOrganization.DEBUG.getOrganization();
                } else {
                    String result = ORGANIZATION_THREAD_LOCAL.get();
                    if (result == null) {
                        result = BlockingWebUtil.getHeader(USER_ORGANIZATION_HEADER);
                    }
                    return (result == null || result.isEmpty()) ? null : result;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getUserOrganization(ServerHttpRequest request) {
        return "20000";
    }

    public static String getLanguage() {
        if (BaseController.isServletRequest()) {
            try {
                if (DEBUG) {
                    return GlobalUserLanguage.DEBUG.getLanguage();
                } else {
                    return BlockingWebUtil.getHeader(USER_LANGUAGE_HEADER);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getLanguage(ServerHttpRequest request) {
        return "zh-cn";
    }

}
