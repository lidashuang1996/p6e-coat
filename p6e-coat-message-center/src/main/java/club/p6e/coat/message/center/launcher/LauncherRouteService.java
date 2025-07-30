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
     * name
     *
     * @return name
     */
    String name();

    /**
     * 执行发射器路由服务
     *
     * @param configs  配置模型列表
     * @param launcher 发射器模型
     * @return 配置模型
     */
    ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs);

}
