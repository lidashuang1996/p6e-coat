package club.p6e.coat.resource;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("ALL")
public class FileUser implements Serializable {

    /**
     * ID
     */
    private int id;

    /**
     * Permissions
     */
    private Map<String, String> permissions;

    /**
     * Get ID
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get Permissions
     *
     * @return Permissions Map Object
     */
    public Map<String, String> getPermissions() {
        return permissions;
    }

}
