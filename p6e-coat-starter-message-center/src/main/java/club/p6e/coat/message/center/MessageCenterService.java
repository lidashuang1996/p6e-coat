package club.p6e.coat.message.center;

import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherStartingModel;

/**
 * Message Center Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MessageCenterService {

    /**
     * Clean Cache Data
     */
    void cleanCacheData();

    /**
     * Execute Push Message
     *
     * @param starting Launcher Starting Model
     * @return Launcher Result Model
     */
    LauncherResultModel execute(LauncherStartingModel starting);

}
