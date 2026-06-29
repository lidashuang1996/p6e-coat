package club.p6e.coat.permission.matcher;

import club.p6e.coat.permission.PermissionDetails;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Permission Path Matcher Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@ConditionalOnMissingBean(PermissionPathMatcher.class)
public class PermissionPathMatcherImpl implements PermissionPathMatcher {

    /**
     * Model Object
     */
    private final Model cache = new Model();

    /**
     * Reentrant Lock Object
     */
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public List<PermissionDetails> match(String path) {
        if (path == null || path.isEmpty()) {
            return List.of();
        }
        Model temporary = cache;
        final String[] paths = path.toLowerCase().trim().split("/");
        for (final String item : paths) {
            Model model = temporary.getData().get(item);
            if (model == null) {
                model = temporary.getData().get("*");
                if (model == null) {
                    return List.of();
                }
            }
            temporary = model;
        }
        return List.copyOf(temporary.getPermissions());
    }

    @Override
    public void register(PermissionDetails permission) {
        if (permission != null
                && permission.getGid() != null
                && permission.getUid() != null
                && permission.getMark() != null
                && permission.getPath() != null
                && permission.getMethod() != null
                && permission.getWeight() != null
                && permission.getVersion() != null
        ) {
            try {
                lock.lock();
                Model temporary = cache;
                final String path = permission.getPath();
                final String[] paths = path.toLowerCase().trim().split("/");
                for (final String item : paths) {
                    temporary = temporary.getData().computeIfAbsent(item, _ -> new Model());
                }
                temporary.getPermissions().add(permission);
                temporary.getPermissions().sort(Comparator.comparingInt(PermissionDetails::getWeight).reversed());
                log.info("[ PERMISSION PATH MATCHER REGISTER ] {}({}) >>> {}", path, permission.getMethod(), permission);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void cleanExpiredVersionData(long version) {
        try {
            lock.lock();
            cleanExpiredVersionData(version, cache);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clean Expired Version Data
     *
     * @param version Version Object
     * @param model   Model Object
     */
    private void cleanExpiredVersionData(long version, Model model) {
        for (final String key : model.getData().keySet()) {
            cleanExpiredVersionData(version, model.getData().get(key));
        }
        model.getPermissions().removeIf(p -> p.getVersion() < version);
    }

    /**
     * Permission Path Matcher Model
     */
    @Data
    private static class Model implements Serializable {

        /**
         * Path Data Object
         */
        private Map<String, Model> data = new ConcurrentHashMap<>();

        /**
         * Permission List Data Object
         */
        private List<PermissionDetails> permissions = new CopyOnWriteArrayList<>();

    }

}
