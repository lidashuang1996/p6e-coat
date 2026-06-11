package club.p6e.coat.environment;

import lombok.Getter;

import java.io.Serializable;

/**
 * Environment Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class EnvironmentContext implements Serializable {

    /**
     * Environment User Object
     */
    @Getter
    private EnvironmentUser user;

    /**
     * Environment Project Object
     */
    @Getter
    private EnvironmentProject project;

    /**
     * Environment Organization Object
     */
    @Getter
    private EnvironmentOrganization organization;

    /**
     * Environment Permission Object
     */
    @Getter
    private EnvironmentPermission permission;

    /**
     * Constructor Initialization
     */
    public EnvironmentContext() {
    }

    /**
     * Constructor Initialization
     *
     * @param organization Environment Organization Object
     * @param project      Environment Project Object
     */
    public EnvironmentContext(EnvironmentOrganization organization, EnvironmentProject project) {
        this.organization = organization;
        this.project = project;
    }

    /**
     * Constructor Initialization
     *
     * @param organization Environment Organization Object
     * @param project      Environment Project Object
     * @param user         Environment User Object
     */
    public EnvironmentContext(EnvironmentOrganization organization, EnvironmentProject project, EnvironmentUser user) {
        this.user = user;
        this.project = project;
        this.organization = organization;
    }

    /**
     * Constructor Initialization
     *
     * @param organization Environment Organization Object
     * @param project      Environment Project Object
     * @param user         Environment User Object
     * @param permission   Environment Permission Object
     */
    public EnvironmentContext(EnvironmentOrganization organization, EnvironmentProject project, EnvironmentUser user, EnvironmentPermission permission) {
        this.user = user;
        this.project = project;
        this.permission = permission;
        this.organization = organization;
    }

    /**
     * Is Auth
     *
     * @return boolean Is Auth Result
     */
    public boolean isAuth() {
        return this.user != null && this.user.id() > 0;
    }

}
