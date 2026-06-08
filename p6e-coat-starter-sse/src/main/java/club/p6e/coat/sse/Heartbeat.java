/*
 * [P2] 死代码标记: Heartbeat 类在完整代码库中从未被实例化/引用，
 * register() 与 unregister() 也无人调用。为减少维护负担已移除激活逻辑，
 * 保留此类仅作向后兼容参考。后续版本可彻底删除。
 */
package club.p6e.coat.sse;
@Deprecated
public class Heartbeat {
    private static final String DEPRECATED = "Heartbeat is no longer used. Remove in next major version.";
}