package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Launcher Default Route Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherDefaultRouteService implements LauncherRouteService {

    /**
     * Router Name
     */
    private static final String DEFAULT_ROUTER_NAME = "LAUNCHER_DEFAULT_ROUTER";

    @Override
    public String name() {
        return DEFAULT_ROUTER_NAME;
    }

    @Override
    public ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs) {
        if (configs == null || configs.isEmpty()) {
            return null;
        } else {
            final List<ConfigModel> result = configs
                    .stream()
                    .filter(ConfigModel::enable)
                    .sorted(Comparator.comparing(ConfigModel::id))
                    .toList();
            if (result.isEmpty()) {
                return null;
            } else if (result.size() == 1) {
                return result.get(0);
            } else {
                return execute(result);
            }
        }
    }

    /**
     * Execute Config Model Object
     *
     * @param configs Config Model List Object
     * @return Config Model Object
     */
    protected ConfigModel execute(List<ConfigModel> configs) {
        return configs.get(0);
    }

}
