package club.p6e.coat.permission.matcher;

import club.p6e.coat.permission.PermissionDetails;
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
 * Permission Path Matcher Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(PermissionPathMatcher.class)
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
     * Cache Object -> Key ( Path Pattern Parser Object ) / Value ( Permission Details Object )
     */
    private final Map<PathPattern, List<PermissionDetails>> cache = new ConcurrentHashMap<>();

    @Override
    public List<PermissionDetails> match(String path) {
        final List<PermissionDetails> result = new ArrayList<>();
        final PathContainer container = PathContainer.parsePath(path);
        for (final PathPattern pattern : this.cache.keySet()) {
            if (pattern.matches(container)) {
                result.addAll(this.cache.get(pattern));
            }
        }
        // permission list is sorted in desc order of weight
        result.sort(Comparator.comparingInt(PermissionDetails::getWeight).reversed());
        return result;
    }

    @Override
    public void register(PermissionDetails model) {
        if (model != null
                && model.getGid() != null && model.getUid() != null
                && model.getMark() != null && model.getPath() != null
                && model.getMethod() != null && model.getWeight() != null && model.getVersion() != null) {
            synchronized (this) {
                final String path = model.getPath();
                for (final PathPattern pattern : this.cache.keySet()) {
                    if (pattern.getPatternString().equalsIgnoreCase(path)) {
                        final String mark = model.getGid() + "_" + model.getUid();
                        final List<PermissionDetails> list = this.cache.get(pattern);
                        list.removeIf(i -> mark.equalsIgnoreCase((i.getGid() + "_" + i.getUid())));
                        LOGGER.info("[ PERMISSION PATH MATCHER REGISTER (ADD/REPLACE) ] {}({}) >>> {}", path, model.getMethod(), model);
                        this.cache.get(parser.parse(path)).add(model);
                        return;
                    }
                }
                LOGGER.info("[ PERMISSION PATH MATCHER REGISTER (ADD) ] {}({}) >>> {}", path, model.getMethod(), model);
                this.cache.put(parser.parse(path), new ArrayList<>(List.of(model)));
            }
        }
    }

    @Override
    public void cleanExpiredVersionData(long version) {
        synchronized (this) {
            for (final PathPattern key : this.cache.keySet()) {
                final List<PermissionDetails> list = this.cache.get(key);
                if (list != null && !list.isEmpty()) {
                    list.removeIf(i -> i.getVersion() == null || i.getVersion() < version);
                    if (list.isEmpty()) {
                        this.cache.remove(key);
                    }
                }
            }
        }
    }

}
