package club.p6e.coat.common.global;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;

import java.util.Map;

/**
 * Globals
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class Globals {

    /**
     * User Model
     */
    public interface UserModel {

        /**
         * Get User ID
         *
         * @return User ID
         */
        Integer getId();

    }

    /**
     * User Builder
     */
    public interface UserBuilder<U extends UserModel> {

        /**
         * Build
         *
         * @param content User Content String
         * @return User Model Object
         */
        U build(String content);

    }

    /**
     * Permission Model
     */
    public interface PermissionModel {

        /**
         * Get Permission Mark
         *
         * @return Permission Mark
         */
        String getMark();

    }

    /**
     * Permission Builder
     */
    public interface PermissionBuilder<U extends PermissionModel> {

        /**
         * Build
         *
         * @param content Permission Content String
         * @return Permission Model Object
         */
        U build(String content);

    }

    /**
     * User Thread Local
     */
    private static final ThreadLocal<String> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Language Thread Local
     */
    private static final ThreadLocal<String> LANGUAGE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Permission Thread Local
     */
    private static final ThreadLocal<String> PERMISSION_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Project Thread Local
     */
    private static final ThreadLocal<String> PROJECT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Organization Thread Local
     */
    private static final ThreadLocal<String> ORGANIZATION_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * User Builder Object
     */
    private static final UserBuilder<?> USER_BUILDER = (UserBuilder<UserModel>) content -> {
        if (content == null) {
            return null;
        } else {
            return (UserModel) () -> {
                final Map<String, Object> data = JsonUtil.fromJsonToMap(content, String.class, Object.class);
                return data == null ? null : TransformationUtil.objectToInteger(data.get("id"));
            };
        }
    };

    /**
     * Permission Builder Object
     */
    private static final PermissionBuilder<?> PERMISSION_BUILDER = (PermissionBuilder<PermissionModel>) content -> {
        if (content == null) {
            return null;
        } else {
            return (PermissionModel) () -> {
                final Map<String, Object> data = JsonUtil.fromJsonToMap(content, String.class, Object.class);
                return data == null ? null : TransformationUtil.objectToString(data.get("mark"));
            };
        }
    };

    /**
     * Set User
     *
     * @param content User Content String
     */
    public static void setUser(String content) {
        USER_THREAD_LOCAL.set(content);
    }

    /**
     * Set Permission
     *
     * @param content Permission Content String
     */
    public static void setPermission(String content) {
        PERMISSION_THREAD_LOCAL.set(content);
    }

    /**
     * Set Project
     *
     * @param content Project Content String
     */
    public static void setProject(String content) {
        PROJECT_THREAD_LOCAL.set(content);
    }

    /**
     * Set Organization
     *
     * @param content Organization Content String
     */
    public static void setOrganization(String content) {
        ORGANIZATION_THREAD_LOCAL.set(content);
    }

    /**
     * Clean Thread Local
     */
    public static void clean() {
        USER_THREAD_LOCAL.remove();
        LANGUAGE_THREAD_LOCAL.remove();
        PERMISSION_THREAD_LOCAL.remove();
        PROJECT_THREAD_LOCAL.remove();
        ORGANIZATION_THREAD_LOCAL.remove();
    }

    /**
     * Get Language
     *
     * @return Language
     */
    public static String getLanguage() {
        return LANGUAGE_THREAD_LOCAL.get();
    }

    /**
     * Get Project
     *
     * @return Project
     */
    public static String getProject() {
        return PROJECT_THREAD_LOCAL.get();
    }

    /**
     * Get Organization
     *
     * @return Organization
     */
    public static String getOrganization() {
        return ORGANIZATION_THREAD_LOCAL.get();
    }

    /**
     * Get User
     *
     * @return User Model Object
     */
    public static UserModel getUser() {
        return USER_BUILDER.build(getUserContent());
    }

    /**
     * Get User
     *
     * @return User Content String
     */
    public static String getUserContent() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * Get Permission
     *
     * @return Permission Model Object
     */
    public static PermissionModel getPermission() {
        return PERMISSION_BUILDER.build(getPermissionContent());
    }

    /**
     * Get Permission
     *
     * @return Permission Content String
     */
    public static String getPermissionContent() {
        return PERMISSION_THREAD_LOCAL.get();
    }

}
