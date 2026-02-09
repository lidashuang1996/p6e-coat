package club.p6e.coat.shield;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.exception.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("")
public class Controller {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private final Map<String, Generator> gm;

    private final Map<String, Validator> vm;

    /**
     * Construct Initialization
     *
     * @param generators Generator List Object
     * @param validators Validator List Object
     */
    public Controller(List<Generator> generators, List<Validator> validators) {
        final Map<String, Generator> generatorMap = new HashMap<>();
        final Map<String, Validator> validatorMap = new HashMap<>();
        generators.forEach(it -> generatorMap.put(it.name(), it));
        validators.forEach(it -> validatorMap.put(it.name(), it));
        this.gm = generatorMap;
        this.vm = validatorMap;
    }

    @PostMapping("/{name}")
    public Flux<DataBuffer> generate(@PathVariable String name, ServerWebExchange exchange) {
        final var generator = gm.get(name);
        if (generator == null) {
            return Flux.error(new ParameterException(Controller.class,
                    "fun generate(String name, ServerWebExchange exchange)", "Generator Not Found"));
        }
        return generator.execute(exchange);
    }

    @PostMapping("/{name}/validate/{token}")
    public Mono<ResultContext> validate(@PathVariable String name, @PathVariable String token, ServerWebExchange exchange) {
        final var validator = vm.get(name);
        if (validator == null) {
            return Mono.error(new ParameterException(Controller.class,
                    "fun validate(String name, String token, ServerWebExchange exchange)", "Validate Not Found"));
        }
        return validator.execute(exchange, token);
    }

}
