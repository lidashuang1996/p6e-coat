package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Launcher Polling Route Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherPollingRouteService extends LauncherDefaultRouteService implements LauncherRouteService {

    /**
     * Router Name
     */
    private static final String DEFAULT_ROUTER_NAME = "LAUNCHER_POLLING_ROUTER";

    /**
     * Index Number
     */
    protected final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String name() {
        return DEFAULT_ROUTER_NAME;
    }

    @Override
    public ConfigModel execute(List<ConfigModel> configs) {
        return configs.get((index.getAndIncrement() % configs.size()));
    }

}
