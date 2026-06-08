package club.p6e.coat.permission.matcher;

import club.p6e.coat.permission.PermissionDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Permission Path Matcher Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@ConditionalOnMissingBean(PermissionPathMatcher.class)
public class PermissionPathMatcherImpl implements PermissionPathMatcher {

    private ConcurrentLinkedQueue<Model> tree = new ConcurrentLinkedQueue<>();

    @Override
    public List<PermissionDetails> match(String path) {
        if (path == null || path.isEmpty()) {
            return new ArrayList<>();
        }
        final String[] paths = path.trim().split("/");
        for (final String item : paths) {
            tree.peek().
        }


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
                        log.info("[ PERMISSION PATH MATCHER REGISTER (ADD/REPLACE) ] {}({}) >>> {}", path, model.getMethod(), model);
                        this.cache.get(parser.parse(path)).add(model);
                        return;
                    }
                }
                log.info("[ PERMISSION PATH MATCHER REGISTER (ADD) ] {}({}) >>> {}", path, model.getMethod(), model);
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

    private static class Model implements Serializable {
        private Map<String, List<PermissionDetails>> data = new ConcurrentHashMap<>();
    }

}
