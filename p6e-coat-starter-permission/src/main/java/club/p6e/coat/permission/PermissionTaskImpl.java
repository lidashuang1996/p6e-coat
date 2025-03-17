package club.p6e.coat.permission;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

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

    @Override
    public long interval() {
        return 3600L;
    }

    /**
     * 权限版本号只有在更新的时候才更新
     * 我这里没有使用缓存进行判断
     * 如果需要实现这个功能，我建议你采用全局缓存的方式进行实现
     * 每当权限相关信息需要更新的时候就更新版本号，这里建议你更新的版本号为当前的时间戳（秒）
     * -------------
     * 我现在默认每次都更新版本号
     * -------------
     * 更新的频率为上面 interval() 方法的返回值
     */
    @Override
    public long version() {
        return (long) Math.floor(System.currentTimeMillis() / 1000D);
    }

}
