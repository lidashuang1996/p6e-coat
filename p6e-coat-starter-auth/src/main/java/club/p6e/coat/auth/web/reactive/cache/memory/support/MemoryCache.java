package club.p6e.coat.auth.web.reactive.cache.memory.support;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Memory Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class MemoryCache implements Cache {

    @Override
    public String type() {
        return "MEMORY_TYPE";
    }

    @SuppressWarnings("ALL")
    public Map<String, String> getData(ReactiveMemoryTemplate template, String key) {
        final Map map = template.get(key, Map.class);
        if (map == null) {
            return new HashMap<>();
        } else {
            final long now = System.currentTimeMillis();
            final Map<String, String> result = (Map<String, String>) map;
            for (final String k : result.keySet()) {
                try {
                    if (result.get(k) == null || now > Long.parseLong(result.get(k))) {
                        result.remove(k);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            return result;
        }
    }

    public Mono<String> delVerificationCode(ReactiveMemoryTemplate template, String key) {
        return Mono.just(template.del(key)).map(l -> key);
    }

    public Mono<List<String>> getVerificationCode(ReactiveMemoryTemplate template, String key, long expiration) {
        final Map<String, String> data = getData(template, key);
        template.set(key, data, expiration);
        return data.isEmpty() ? Mono.empty() : Mono.just(new ArrayList<>(data.keySet()));
    }

    public Mono<String> setVerificationCode(ReactiveMemoryTemplate template, String key, String value, long expiration) {
        final Map<String, String> data = getData(template, key);
        data.put(value, String.valueOf(System.currentTimeMillis() + expiration * 1000L));
        return Mono.just(template.set(key, data, expiration)).map(b -> key);
    }

}
