package club.p6e.coat.message.center.launcher;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.config.ConfigModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Launcher Weight Polling Route Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherWeightPollingRouteService
        extends LauncherPollingRouteService implements LauncherRouteService {

    /**
     * Router Name
     */
    private static final String DEFAULT_ROUTER_NAME = "LAUNCHER_WEIGHT_POLLING_ROUTER";

    /**
     * Get Launcher Config Weight Number
     *
     * @param config   Config Model Object
     * @param launcher Launcher Model Object
     * @return Weight Number
     */
    private static Integer getWeightAttributeData(LauncherModel launcher, ConfigModel config) {
        try {
            for (final LauncherModel.ConfigMapperModel item : launcher.configs()) {
                if (item.id() == config.id()) {
                    final Map<String, String> content = JsonUtil.fromJsonToMap(item.attribute(), String.class, String.class);
                    if (content != null && content.get("weight") != null) {
                        return Integer.valueOf(content.get("weight"));
                    }
                }
            }
        } catch (Exception e) {
            //ignore exception
        }
        return null;
    }

    @Override
    public String name() {
        return DEFAULT_ROUTER_NAME;
    }

    @Override
    public ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs) {
        final List<String> wl = new ArrayList<>();
        final Map<String, ConfigModel> wd = new HashMap<>();
        for (final ConfigModel item : configs) {
            final Integer wn = getWeightAttributeData(launcher, item);
            if (wn != null) {
                wl.add(item.id() + "@" + wn);
                wd.put(String.valueOf(item.id()), item);
            }
        }
        if (wl.isEmpty() || wd.isEmpty()) {
            return null;
        } else if (wl.size() == 1 || wd.size() == 1) {
            return wd.get(new ArrayList<>(wd.keySet()).get(0));
        } else {
            int total = 0;
            for (final String item : wl) {
                total += Integer.parseInt(item.substring(item.lastIndexOf("@") + 1));
            }
            int rt = 0;
            final int ri = index.getAndIncrement() % total;
            for (final String item : wl) {
                final int im = item.lastIndexOf("@");
                final int ix = Integer.parseInt(item.substring((im + 1)));
                if (ri < rt + ix) {
                    return wd.get(item.substring(0, im));
                } else {
                    rt = rt + ix;
                }
            }
            return null;
        }
    }

}
