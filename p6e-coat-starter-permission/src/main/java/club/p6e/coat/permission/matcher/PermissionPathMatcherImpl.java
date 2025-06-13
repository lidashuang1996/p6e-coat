package club.p6e.coat.permission.matcher;

import club.p6e.coat.permission.PermissionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permission Path Matcher
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PermissionPathMatcher.class,
        ignored = PermissionPathMatcherImpl.class
)
public class PermissionPathMatcherImpl implements PermissionPathMatcher {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionPathMatcherImpl.class);

    /**
     * Path Pattern Parser Object
     */
    private final PathPatternParser parser = new PathPatternParser();

    /**
     * Path Pattern Parser / Permission Details Cache Object
     */
    private final Map<PathPattern, List<PermissionDetails>> cache = new ConcurrentHashMap<>();

    @Override
    public List<PermissionDetails> match(String path) {
        final List<PermissionDetails> result = new ArrayList<>();
        final PathContainer container = PathContainer.parsePath(path);
        for (final PathPattern pattern : cache.keySet()) {
            System.out.println("AAAAAAAAA >>> " + pattern);
            if (pattern.matches(container)) {
                System.out.println("bbbbbbbbbbbbbb >>>> " + pattern.matches(container));
                result.addAll(cache.get(pattern));
            }
        }
        result.sort(Comparator.comparingInt(PermissionDetails::getWeight).reversed());
        return result;
    }

    @Override
    public void register(PermissionDetails model) {
        if (model != null
                && model.getGid() != null
                && model.getUid() != null
                && model.getPath() != null) {
            final String path = model.getPath();
            for (final PathPattern pattern : cache.keySet()) {
                if (pattern.getPatternString().equalsIgnoreCase(path)) {
                    final String mark = model.getGid() + "_" + model.getUid();
                    final List<PermissionDetails> list = cache.get(pattern);
                    list.removeIf(item -> mark.equals(item.getGid() + "_" + item.getUid()));
                    LOGGER.info("[ REGISTER (ADD/REPLACE) ] {}({}) >>> {}", path, model.getMethod(), model);
                    cache.get(parser.parse(path)).add(model);
                    return;
                }
            }
            LOGGER.info("[ REGISTER (ADD) ] {}({}) >>> {}", path, model.getMethod(), model);
            cache.put(parser.parse(path), new ArrayList<>(List.of(model)));
        }
    }

    @Override
    public void unregister(PathPattern path) {
        cache.remove(path);
    }

    @Override
    public synchronized void cleanExpiredVersionData(long version) {
        for (final PathPattern key : cache.keySet()) {
            final List<PermissionDetails> list = cache.get(key);
            if (list != null && !list.isEmpty()) {
                list.removeIf(item -> item.getVersion() == null || item.getVersion() < version);
                if (list.isEmpty()) {
                    cache.remove(key);
                }
            }
        }
    }

}
