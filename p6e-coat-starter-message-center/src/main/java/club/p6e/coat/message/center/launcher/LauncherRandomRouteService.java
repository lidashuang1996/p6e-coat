package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Launcher Random Route Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherRandomRouteService extends LauncherDefaultRouteService implements LauncherRouteService {

    /**
     * Router Name
     */
    private static final String DEFAULT_ROUTER_NAME = "LAUNCHER_RANDOM_ROUTER";

    @Override
    public String name() {
        return DEFAULT_ROUTER_NAME;
    }

    @Override
    public ConfigModel execute(List<ConfigModel> configs) {
        return configs.get(ThreadLocalRandom.current().nextInt(configs.size()));
    }

}
