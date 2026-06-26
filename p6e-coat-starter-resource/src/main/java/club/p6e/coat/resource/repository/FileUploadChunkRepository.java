package club.p6e.coat.resource.repository;

import club.p6e.coat.common.exception.DataBaseException;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.resource.model.FileUploadChunkModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * File Upload Chunk Repository
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(FileUploadChunkRepository.class)
public class FileUploadChunkRepository {

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initializers
     *
     * @param client Database Client Object
     */
    public FileUploadChunkRepository(DatabaseClient client) {
        this.client = client;
    }

    /**
     * Get File Upload Chunk Table Name
     *
     * @return File Upload Chunk Table Name
     */
    public String getFileUploadChunkTable() {
        return "p6e_file_upload_chunk";
    }

    /**
     * Create
     *
     * @param model File Upload Chunk Model Object
     * @return File Upload Chunk Model Object
     */
    @SuppressWarnings("ALL")
    public Mono<FileUploadChunkModel> create(FileUploadChunkModel model) {
        if (model == null) {
            return Mono.error(new DataBaseException(
                    this.getClass(),
                    "fun create(FileUploadChunkModel model)",
                    "create(...) file upload chunk model object data is null"
            ));
        }
        if (model.getFid() == null || model.getName() == null || model.getSize() == null) {
            return Mono.error(new DataBaseException(
                    this.getClass(),
                    "fun create(FileUploadChunkModel model)",
                    "create(...) file upload chunk model [ fid/name/size ] data is null"
            ));
        }
        final LocalDateTime now = LocalDateTime.now();
        model.setId(null);
        model.setVersion(0);
        model.setCreationDateTime(now);
        model.setModificationDateTime(now);
        if (model.getCreator() == null) {
            model.setCreator("sys");
        }
        if (model.getModifier() == null) {
            model.setModifier("sys");
        }
        return client
                .sql(TemplateParser.execute("""
                        INSERT INTO @{TABLE} (
                            fid_,
                            name_,
                            size_,
                            creator_,
                            modifier_,
                            creation_date_time_,
                            modification_date_time_,
                            version_
                        ) VALUES (
                            :FID,
                            :NAME,
                            :SIZE,
                            :CREATOR,
                            :MODIFIER,
                            :CREATOR_DATE_TIME,
                            :MODIFICATION_DATE_TIME,
                            :VERSION
                        ) RETURNING id_ AS id_
                        ;""", Map.of("TABLE", getFileUploadChunkTable())))
                .bind("FID", model.getFid())
                .bind("NAME", model.getName())
                .bind("SIZE", model.getSize())
                .bind("CREATOR", model.getCreator())
                .bind("MODIFIER", model.getModifier())
                .bind("CREATOR_DATE_TIME", model.getCreationDateTime())
                .bind("MODIFICATION_DATE_TIME", model.getModificationDateTime())
                .bind("VERSION", model.getVersion())
                .fetch()
                .first()
                .map(row -> model.setId(TransformationUtil.objectToInteger(row.get("id_"))))
                .switchIfEmpty(Mono.error(new DataBaseException(
                        this.getClass(),
                        "fun create(FileUploadChunkModel model)",
                        "create(...) create data exception"
                )));
    }

}
