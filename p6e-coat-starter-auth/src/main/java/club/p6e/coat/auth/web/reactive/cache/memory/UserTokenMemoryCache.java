//package club.p6e.coat.auth.web.reactive.cache.memory;
//
//import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
//import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
//import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
//import club.p6e.coat.common.utils.JsonUtil;
//import reactor.core.publisher.Mono;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * User Token Memory Cache
// *
// * @author lidashuang
// * @version 1.0
// */
//public class UserTokenMemoryCache extends MemoryCache implements UserTokenCache {
//
//    /**
//     * Reactive Memory Template Object
//     */
//    private final ReactiveMemoryTemplate template;
//
//    /**
//     * Constructor Initialization
//     *
//     * @param template Reactive Memory Template Object
//     */
//    public UserTokenMemoryCache(ReactiveMemoryTemplate template) {
//        this.template = template;
//    }
//
//    @Override
//    public Mono<Model> set(String uid, String device, String token, String content, long expiration) {
//        final Model model = new Model().setUid(uid).setDevice(device).setToken(token);
//        final String json = JsonUtil.toJson(model);
//        if (json == null) {
//            return Mono.empty();
//        }
//        template.set(USER_CACHE_PREFIX + uid, content, expiration);
//        template.set(TOKEN_CACHE_PREFIX + token, json, expiration);
//        template.set(USER_TOKEN_CACHE_PREFIX + uid + DELIMITER + token, json, expiration);
//        return Mono.just(model);
//    }
//
//    @Override
//    public Mono<String> getUser(String uid) {
//        final String r = template.get(USER_CACHE_PREFIX + uid, String.class);
//        return r == null ? Mono.empty() : Mono.just(r);
//    }
//
//    @Override
//    public Mono<Model> getToken(String token) {
//        final String r = template.get(TOKEN_CACHE_PREFIX + token, String.class);
//        if (r == null) {
//            return Mono.empty();
//        } else {
//            final Model model = JsonUtil.fromJson(r, Model.class);
//            return model == null ? Mono.empty() : Mono.just(model);
//        }
//    }
//
//    @Override
//    public Mono<Model> cleanToken(String token) {
//        return getToken(token).flatMap(m -> {
//            template.del(TOKEN_CACHE_PREFIX + m.getToken());
//            template.del(USER_TOKEN_CACHE_PREFIX + m.getUid() + DELIMITER + m.getToken());
//            return Mono.just(m);
//        });
//    }
//
//    @Override
//    public Mono<List<String>> cleanUserAll(String uid) {
//        final List<String> l = new ArrayList<>();
//        final String r = template.get(USER_CACHE_PREFIX + uid, String.class);
//        if (r != null) {
//            template.names().forEach(n -> {
//                if (n.startsWith(USER_TOKEN_CACHE_PREFIX + uid + DELIMITER)) {
//                    l.add(n);
//                    template.del(n);
//                }
//            });
//        }
//        template.del(USER_CACHE_PREFIX + uid);
//        return Mono.just(l);
//    }
//
//}
