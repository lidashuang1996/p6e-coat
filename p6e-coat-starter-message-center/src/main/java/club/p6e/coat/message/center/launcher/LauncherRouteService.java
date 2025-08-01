package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigModel;

import java.util.List;

/**
 * Launcher Route Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherRouteService {

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Execute Launcher Route
     *
     * @param launcher Launcher Model Object
     * @param configs  Config Model List Object
     * @return Config Model Object
     */
    ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs);

}
