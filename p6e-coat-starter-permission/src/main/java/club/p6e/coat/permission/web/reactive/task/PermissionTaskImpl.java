package club.p6e.coat.permission.web.reactive.task;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.PermissionTaskActuator;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import club.p6e.coat.permission.web.reactive.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.security.auth.login.LoginContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * Permission Task Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PermissionTask.class,
        ignored = PermissionTaskImpl.class
)
public class PermissionTaskImpl implements PermissionTask {

    /**
     * Inject log object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionTaskImpl.class);

    /**
     * Version
     */
    private final LongAccumulator version = new LongAccumulator(1L);

    @Override
    public synchronized Mono<Long> execute() {
        final LocalDateTime now = LocalDateTime.now();
        LOGGER.info("[ PERMISSION TASK ] ==> now: {}", now);
        LOGGER.info("[ PERMISSION TASK ] start execute permission update task.");
        return execute0(version + 1).map(b -> {
            if (b) {
                SpringUtil.getBean(PermissionPathMatcher.class).cleanExpiredVersionData(version + 1);
                LOGGER.info("[ PERMISSION TASK ] complete the task of execute permission updates.");
            } else {
                return "111111111";
            }
        });
    }

    private Mono<Boolean> execute0(long version) {
        return execute1().map(list -> {
            LOGGER.info("[ PERMISSION TASK ] successfully read data, list data >>> [{}].", list.size());
            list.forEach(item -> SpringUtil.getBean(PermissionPathMatcher.class).register(item.setVersion(version)));
            return true;
        });
    }

    private Mono<List<PermissionDetails>> execute1() {
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
