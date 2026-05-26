package club.p6e.coat.environment;

import lombok.Getter;

import java.io.Serializable;

public class EnvironmentContext implements Serializable {

    @Getter
    private  EnvironmentProject project;

    @Getter
    private  EnvironmentOrganization organization;

    @Getter
    private  EnvironmentUser user;

    @Getter
    private  EnvironmentPermission permission;

    public EnvironmentContext() {
    }

    public EnvironmentContext(EnvironmentOrganization organization, EnvironmentProject project, EnvironmentUser user, EnvironmentPermission permission) {
        this.organization = organization;
        this.project = project;
        this.user = user;
        this.permission = permission;
    }

    public boolean isAuth() {
        return this.user != null && this.user.id() > 0;
    }

}
