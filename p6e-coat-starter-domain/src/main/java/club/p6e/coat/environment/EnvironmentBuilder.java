package club.p6e.coat.environment;


public class EnvironmentBuilder {

    private final Integer oid;
    private final Integer pid;
    private final UserBuilder<?> ub;
    private final PermissionBuilder<?> up;

//    public EnvironmentBuilder(UserBuilder<?> ub) {
//        this(ub, null);
//    }
//
//    public EnvironmentBuilder(UserBuilder<?> ub, PermissionBuilder<?> up) {
//        this.ub = ub;
//        this.up = up;
//    }

    public EnvironmentBuilder(Integer oid, Integer pid, UserBuilder<?> ub, PermissionBuilder<?> up) {
        this.ub = ub;
        this.up = up;
        this.oid = oid;
        this.pid = pid;
    }

    public EnvironmentContext build() {
        return new EnvironmentContext(this.oid, this.pid, ub == null ? null : ub.build(), this.up == null ? null : this.up.build());
    }

    public interface UserBuilder<T extends EnvironmentContext.User> {
        T build();
    }

    public interface PermissionBuilder<T extends EnvironmentContext.Permission> {
        T build();
    }

}
