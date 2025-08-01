package club.p6e.coat.resource.task;

/**
 * 文件分片清除策略服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileSliceCleanTaskStrategyService {

    /**
     * 时间策略
     */
    public String cron();

    /**
     * 执行文件清除
     */
    public void execute();

}
