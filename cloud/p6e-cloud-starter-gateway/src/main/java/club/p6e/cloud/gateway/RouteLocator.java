package club.p6e.cloud.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Route Locator
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(RouteLocator.class)
public class RouteLocator implements RouteDefinitionRepository {

    /**
     * Application Event Publisher Object
     */
    private final ApplicationEventPublisher publisher;

    /**
     * Route Definition List Object
     */
    private final List<RouteDefinition> routeDefinitions = new ArrayList<>();

    /**
     * Constructor Initialization
     *
     * @param publisher Application Event Publisher Object
     */
    public RouteLocator(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Refresh Route Definitions
     *
     * @param routeDefinitions Route DefinitionList Object
     */
    public synchronized void refresh(List<RouteDefinition> routeDefinitions) {
        this.routeDefinitions.clear();
        this.routeDefinitions.addAll(routeDefinitions);
        if (!this.routeDefinitions.isEmpty()) {
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
        }
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> mr) {
        return mr.map(routeDefinitions::add).then();
    }

    @Override
    public Mono<Void> delete(Mono<String> ms) {
        return ms.map(r -> {
            for (final RouteDefinition item : routeDefinitions) {
                if (item.getId().equalsIgnoreCase(r)) {
                    routeDefinitions.remove(item);
                    break;
                }
            }
            return r;
        }).then();
    }

}
