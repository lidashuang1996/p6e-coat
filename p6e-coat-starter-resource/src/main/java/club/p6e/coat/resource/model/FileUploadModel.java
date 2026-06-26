package club.p6e.coat.resource.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * File Upload Model
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class FileUploadModel implements Serializable {

    private Integer id;
    private String name;
    private Long size;
    private String source;
    private String owner;
    private String storageType;
    private String storageLocation;
    private Integer lock;
    private String creator;
    private String modifier;
    private LocalDateTime creationDateTime;
    private LocalDateTime modificationDateTime;
    private Integer version;

}
