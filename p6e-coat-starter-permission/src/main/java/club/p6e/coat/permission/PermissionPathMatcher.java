package club.p6e.coat.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;
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
        ignored = PermissionPathMatcher.class
)
public class PermissionPathMatcher {

    /**
     * Inject log object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionPathMatcher.class);

    /**
     * PathPatternParser object
     */
    private final PathPatternParser parser = new PathPatternParser();

    /**
     * PathPatternParser/PermissionDetails cache object
     */
    private final Map<PathPattern, List<PermissionDetails>> cache = new ConcurrentHashMap<>();

    /**
     * Matching Path
     *
     * @param path Path
     * @return PermissionDetails/List object
     */
    public List<PermissionDetails> match(String path) {
        final List<PermissionDetails> result = new ArrayList<>();
        final PathContainer container = PathContainer.parsePath(path);
        for (final PathPattern pattern : cache.keySet()) {
            if (pattern.matches(container)) {
                result.addAll(cache.get(pattern));
            }
        }
        result.sort(Comparator.comparingInt(PermissionDetails::getWeight).reversed());
        return result;
    }

    /**
     * Cache register path
     *
     * @param model PermissionDetails object
     */
    public void register(PermissionDetails model) {
        if (model != null
                && model.getGid() != null
                && model.getUid() != null
                && model.getPath() != null
                && !model.getPath().isEmpty()) {
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

    /**
     * Cache unregister path
     *
     * @param path Path
     */
    @SuppressWarnings("ALL")
    public void unregister(PathPattern path) {
        cache.remove(path);
    }

    /**
     * Delete expired version permission data
     *
     * @param version Version
     */
    public void deleteExpiredVersionData(long version) {
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
