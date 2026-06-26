package club.p6e.coat.resource.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * File Upload Chunk Model
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class FileUploadChunkModel implements Serializable {

    private Integer id;
    private Integer fid;
    private String name;
    private Long size;
    private String creator;
    private String modifier;
    private LocalDateTime creationDateTime;
    private LocalDateTime modificationDateTime;
    private Integer version;

}
