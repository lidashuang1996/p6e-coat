package club.p6e.coat.message.center.log;

import club.p6e.coat.message.center.launcher.LauncherTemplateModel;

import java.util.List;
import java.util.Map;

/**
 * Log Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LogService {

    /**
     * Create
     *
     * @param recipients Recipient List Object
     * @param message    Launcher Template Model Object
     * @return Log Object
     */
    Map<String, List<String>> create(List<String> recipients, LauncherTemplateModel message);

    /**
     * Update
     *
     * @param list   Log Object
     * @param result Result
     */
    void update(Map<String, List<String>> list, String result);

}
