//package club.p6e.coat.auth.web.reactive.aspect;
//
//import club.p6e.coat.common.utils.CopyUtil;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author lidashuang
// * @version 1.0
// */
//public class OAuth2LoginAspect extends Aspect {
//
//    @Override
//    public int getOrder() {
//        return -1000;
//    }
//
//    @Override
//    Mono<Object> before(Object[] o) {
//        return Mono.just(o);
//    }
//
//    @Override
//    Mono<Object> after(Object[] o) {
//        ServerWebExchange tmp = null;
//        for (final Object item : o) {
//            if (item instanceof ServerWebExchange) {
//                tmp = (ServerWebExchange) item;
//                break;
//            }
//        }
//        if (tmp != null) {
//            final String path = tmp.getRequest().getPath().value();
//            if (path.startsWith("/oauth/authorize")) {
//                final Map<String, Object> result = CopyUtil.objectToMap(o[o.length - 1], new HashMap<>());
//                o[o.length - 1] = result;
//                result.put("oauth2", "1");
//                result.put("oauth2_callback", "callback");
//                return Mono.just(o[o.length - 1]);
//            }
//        }
//        return Mono.just(o[o.length - 1]);
//    }
//
//}
