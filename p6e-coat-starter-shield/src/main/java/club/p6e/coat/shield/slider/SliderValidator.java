package club.p6e.coat.shield.slider;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.shield.Validator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SliderValidator implements Validator {

    @Override
    public String name() {
        return "SLIDER";
    }

    @Override
    public Mono<ResultContext> execute(ServerWebExchange exchange, String token) {
        return null;
    }

}
