package club.p6e.coat.permission;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.permission.matcher.PermissionPathMatcherImpl;
import club.p6e.coat.permission.model.PermissionModel;
import club.p6e.coat.permission.web.reactive.task.PermissionTask;
import club.p6e.coat.permission.web.reactive.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Permission Task Actuator
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PermissionTaskActuator.class,
        ignored = PermissionTaskActuator.class
)
public final class PermissionTaskActuator {

    /**
     * Inject log object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionTaskActuator.class);

    /**
     * Version
     */
    private long version = -1;

    /**
     * PermissionTask object
     */
    private final PermissionTask task;

    /**
     * PermissionPathMatcher object
     */
    private final PermissionPathMatcherImpl matcher;

    /**
     * Constructor initializers
     *
     * @param task          PermissionTask object
     * @param matcher       PermissionPathMatcher object
     * @param taskScheduler ThreadPoolTaskScheduler object
     */
    public PermissionTaskActuator(
            PermissionTask task,
            TaskScheduler taskScheduler,
            PermissionPathMatcherImpl matcher
    ) {
        this.task = task;
        this.matcher = matcher;
        taskScheduler.scheduleWithFixedDelay(
                this::execute,
                Instant.now().plusSeconds(10),
                Duration.ofSeconds(task.interval())
        );
    }

    /**
     * Execute task
     */
    public synchronized void execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[TASK] ==> now: {}", now);
        LOGGER.info("[TASK] start executing permission update task.");
        final long currentVersion = task.version();
        if (version < currentVersion) {
            if (execute0(currentVersion)) {
                version = currentVersion;
                matcher.deleteExpiredVersionData(version);
            }
        } else {
            LOGGER.info("[TASK] The version number is the " +
                    "latest version, and there is no need to perform data update operations.");
        }
        LOGGER.info("[TASK] complete the task of executing permission updates.");
    }

    private boolean execute0(long version) {
        final List<PermissionDetails> list = execute1();
        LOGGER.info("[TASK] successfully read data, list data >>> [{}].", list.size());
        list.forEach(item -> matcher.register(item.setVersion(version)));
        return true;
    }

    private List<PermissionDetails> execute1() {
        int page = 1;
        List<PermissionModel> tmp;
        final List<PermissionModel> list = new ArrayList<>();
        final PermissionRepository repository = SpringUtil.getBean(PermissionRepository.class);
        do {
            tmp = repository.findPermissionList(page++, 20).block();
            if (tmp != null) {
                list.addAll(tmp);
            }
        } while (tmp != null && !tmp.isEmpty());
        return new ArrayList<>() {{
            for (PermissionModel item : list) {
                add(new PermissionDetails()
                        .setOid(item.getOid())
                        .setPid(item.getPid())
                        .setUid(item.getUid())
                        .setGid(item.getGid())
                        .setUrl(item.getUUrl())
                        .setMethod(item.getUMethod())
                        .setBaseUrl(item.getUBaseUrl())
                        .setMark(item.getGMark())
                        .setWeight(item.getGWeight())
                        .setConfig(item.getRConfig())
                        .setAttribute(item.getRAttribute())
                        .setPath(item.getUBaseUrl() + item.getUUrl())
                );
            }
        }};
    }

}
