package club.p6e.coat.auth;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 认证路径匹配器
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthPathMatcher {

    /**
     * 缓存需要拦截的路径匹配器
     */
    private final List<PathPattern> list = Collections.synchronizedList(new ArrayList<>());

    /**
     * 匹配路径
     *
     * @param path 路径内容
     * @return 匹配请求的路径是否为拦截的路径地址
     */
    public boolean match(String path) {
        final PathContainer container = PathContainer.parsePath(path);
        for (final PathPattern pattern : list) {
            if (pattern.matches(container)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注册路径
     *
     * @param path 路径内容
     * @return 路径内容对象
     */
    public PathPattern register(String path) {
        final PathPattern parser = new PathPatternParser().parse(path);
        list.add(parser);
        return parser;
    }

    /**
     * 卸载路径
     *
     * @param path 路径内容
     */
    public void unregister(PathPattern path) {
        list.remove(path);
    }

}
