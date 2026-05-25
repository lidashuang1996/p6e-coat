package club.p6e.coat.environment;


import java.io.Serializable;

public class EnvironmentContext {

    private final Integer oid;
    private final Integer pid;

    private final User user;
    private final Permission permission;

//    EnvironmentContext(User user) {
//        this.user = user;
//    }
//
//    EnvironmentContext(User user, Permission permission) {
//        this.user = user;
//        this.permission = permission;
//    }

    EnvironmentContext(Integer oid, Integer pid, User user, Permission permission) {
        this.oid = oid;
        this.pid = pid;
        this.user = user;
        this.permission = permission;
    }

    public boolean isAuth() {
        return this.user != null && this.user.id() > 0;
    }

    public interface User extends Serializable {
        Integer id();
    }

    public interface Permission extends Serializable {
        Integer id();

        String mark();

        String config();
    }

}
