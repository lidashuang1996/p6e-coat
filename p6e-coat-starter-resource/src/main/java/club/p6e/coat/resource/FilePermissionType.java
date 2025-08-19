package club.p6e.coat.resource;

import lombok.Getter;

/**
 * File Permission Type
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public enum FilePermissionType {

    /**
     * Upload File Permission Type
     */
    UPLOAD("U"),
    /**
     * Resource File Permission Type
     */
    RESOURCE("R"),
    /**
     * Download File Permission Type
     */
    DOWNLOAD("D");

    /**
     * File Permission Mark
     */
    private final String mark;

    FilePermissionType(String mark) {
        this.mark = mark;
    }

}
