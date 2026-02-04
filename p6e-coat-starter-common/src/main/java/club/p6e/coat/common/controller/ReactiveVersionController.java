package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reactive Version Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/__version__")
@Component(value = "club.p6e.coat.common.controller.ReactiveVersionController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveVersionController {

    @RequestMapping("")
    public Mono<Void> def1(ServerWebExchange exchange) {
        return def2(exchange);
    }

    @RequestMapping("/")
    public Mono<Void> def2(ServerWebExchange exchange) {
        final ServerHttpResponse response = exchange.getResponse();
        final DataBufferFactory dataBufferFactory = response.bufferFactory();
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8");
        return response.writeWith(Mono.just(dataBufferFactory.wrap(version().getBytes(StandardCharsets.UTF_8))));
    }

    @SuppressWarnings("ALL")
    private String version() {
        final StringBuilder content = new StringBuilder();
        try (final InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream("version")) {
            if (inputStream != null) {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

}
