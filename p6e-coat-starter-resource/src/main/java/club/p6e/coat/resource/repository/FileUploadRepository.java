package club.p6e.coat.resource.repository;

import club.p6e.coat.common.exception.DataBaseException;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.resource.model.FileUploadModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * File Upload Repository
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(FileUploadRepository.class)
public class FileUploadRepository {

    /**
     * Database Client Object
     */
    private final DatabaseClient client;

    /**
     * Constructor Initializers
     *
     * @param client Database Client Object
     */
    public FileUploadRepository(DatabaseClient client) {
        this.client = client;
    }

    /**
     * Get File Upload Table Name
     *
     * @return File Upload Table Name
     */
    public String getFileUploadTable() {
        return "p6e_file_upload";
    }

    /**
     * Acquire Lock
     *
     * @param id  ID
     * @param uid UID
     * @return Lock Value
     */
    @SuppressWarnings("ALL")
    public Mono<Long> acquireLock(int id, String uid) {
        return select(id)
                .flatMap(m -> {
                    m.setModifier(uid);
                    m.setModificationDateTime(LocalDateTime.now());
                    return client
                            .sql(TemplateParser.execute("""
                                    UPDATE
                                        @{TABLE}
                                    SET
                                        lock_ = lock_ + 1,
                                        modifier_ = :MODIFIER,
                                        modification_date_time_ = :MODIFICATION_DATE_TIME
                                    WHERE
                                        id_ = :ID
                                    RETURNING lock_
                                    ;""", Map.of("TABLE", getFileUploadTable())
                            ))
                            .bind("ID", id)
                            .bind("MODIFIER", uid)
                            .bind("MODIFICATION_DATE_TIME", m.getModificationDateTime())
                            .fetch()
                            .rowsUpdated()
                            .switchIfEmpty(Mono.error(new DataBaseException(
                                    this.getClass(),
                                    "fun acquireLock(int id)",
                                    "acquireLock(...) update data exception"
                            )));
                });
    }

    /**
     * Release Lock
     *
     * @param id  ID
     * @param uid UID
     * @return Lock Value
     */
    @SuppressWarnings("ALL")
    public Mono<Long> releaseLock(int id, String uid) {
        return select(id)
                .flatMap(m -> {
                    m.setModifier(uid);
                    m.setModificationDateTime(LocalDateTime.now());
                    return client
                            .sql(TemplateParser.execute("""
                                    UPDATE
                                        @{TABLE}
                                    SET
                                        lock_ = lock_ - 1,
                                        modifier_ = :MODIFIER,
                                        modification_date_time_ = :MODIFICATION_DATE_TIME
                                    WHERE
                                        id_ = :ID
                                    RETURNING lock_
                                    ;""", Map.of("TABLE", getFileUploadTable())
                            ))
                            .bind("ID", id)
                            .bind("MODIFIER", uid)
                            .bind("MODIFICATION_DATE_TIME", m.getModificationDateTime())
                            .fetch()
                            .rowsUpdated()
                            .switchIfEmpty(Mono.error(new DataBaseException(
                                    this.getClass(),
                                    "fun releaseLock(int id)",
                                    "releaseLock(...) update data exception"
                            )));
                });
    }

    /**
     * Close Lock
     *
     * @param id  ID
     * @param uid UID
     * @return Long Delete Rows Count
     */
    @SuppressWarnings("ALL")
    public Mono<Long> closeLock(int id, String uid) {
        return select(id)
                .flatMap(m -> {
                    m.setModificationDateTime(LocalDateTime.now());
                    if (m.getLock() == -1) {
                        return Mono.error(new DataBaseException(
                                this.getClass(),
                                "fun closeLock(int id)",
                                "closeLock(...) it is already in a closed state and cannot be closed again"
                        ));
                    } else if (m.getLock() > 0) {
                        return Mono.error(new DataBaseException(
                                this.getClass(),
                                "fun closeLock(int id)",
                                "closeLock(...) there are upload sharding requests and cannot be closed"
                        ));
                    } else if (m.getLock() == 0) {
                        return client
                                .sql(TemplateParser.execute("""
                                        UPDATE
                                            @{TABLE}
                                        SET
                                            lock_ = :LOCK,
                                            version_ = :NEW_VERSION,
                                            modification_date_time_ = :MODIFICATION_DATE_TIME
                                        WHERE
                                            id_ = :ID
                                            AND version_ = :OLD_VERSION
                                        ;""", Map.of("TABLE", getFileUploadTable())
                                ))
                                .bind("ID", id)
                                .bind("LOCK", -1)
                                .bind("OLD_VERSION", m.getVersion())
                                .bind("NEW_VERSION", (m.getVersion() + 1))
                                .bind("MODIFICATION_DATE_TIME", m.getModificationDateTime())
                                .fetch()
                                .rowsUpdated()
                                .switchIfEmpty(Mono.error(new DataBaseException(
                                        this.getClass(),
                                        "fun closeLock(int id)",
                                        "closeLock(...) update data exception"
                                )));
                    } else {
                        return Mono.just(0L);
                    }
                });
    }

    /**
     * Create
     *
     * @param model File Upload Model Object
     * @return File Upload Model Object
     */
    @SuppressWarnings("ALL")
    public Mono<FileUploadModel> create(FileUploadModel model) {
        if (model == null) {
            return Mono.error(new DataBaseException(
                    this.getClass(),
                    "fun create(FileUploadModel model)",
                    "create(...) file upload model object data is null"
            ));
        }
        if (model.getName() == null || model.getSource() == null) {
            return Mono.error(new DataBaseException(
                    this.getClass(),
                    "fun create(FileUploadModel model)",
                    "create(...) file upload model [ name/source ] data is null"
            ));
        }
        if (model.getSize() == null) {
            model.setSize(0L);
        }
        if (model.getOwner() == null) {
            model.setOwner("sys");
        }
        if (model.getCreator() == null) {
            model.setCreator("sys");
        }
        if (model.getModifier() == null) {
            model.setModifier("sys");
        }
        if (model.getStorageType() == null) {
            model.setStorageType("");
        }
        if (model.getStorageLocation() == null) {
            model.setStorageLocation("");
        }
        final LocalDateTime now = LocalDateTime.now();
        model.setId(null);
        model.setLock(0);
        model.setVersion(0);
        model.setCreationDateTime(now);
        model.setModificationDateTime(now);
        return client
                .sql(TemplateParser.execute("""
                        INSERT INTO @{TABLE} (
                            name_,
                            size_,
                            source_,
                            owner_,
                            storage_type_,
                            storage_location_,
                            lock_,
                            creator_,
                            modifier_,
                            creation_date_time_,
                            modification_date_time_,
                            version_
                        )
                        VALUES (
                            :NAME,
                            :SIZE,
                            :SOURCE,
                            :OWNER,
                            :STORAGE_TYPE,
                            :STORAGE_LOCATION,
                            :LOCK,
                            :CREATOR,
                            :MODIFIER,
                            :CREATION_DATE_TIME,
                            :MODIFICATION_DATE_TIME,
                            :VERSION
                        ) RETURNING id_
                        ;""", Map.of("TABLE", getFileUploadTable())))
                .bind("NAME", model.getName())
                .bind("SIZE", model.getSize())
                .bind("SOURCE", model.getSource())
                .bind("OWNER", model.getOwner())
                .bind("STORAGE_TYPE", model.getStorageType())
                .bind("STORAGE_LOCATION", model.getStorageLocation())
                .bind("LOCK", model.getLock())
                .bind("CREATOR", model.getCreator())
                .bind("MODIFIER", model.getModifier())
                .bind("CREATION_DATE_TIME", model.getCreationDateTime())
                .bind("MODIFICATION_DATE_TIME", model.getModificationDateTime())
                .bind("VERSION", model.getVersion())
                .fetch()
                .first()
                .map(row -> model.setId(TransformationUtil.objectToInteger(row.get("id_"))))
                .switchIfEmpty(Mono.error(new DataBaseException(
                        this.getClass(),
                        "fun create(FileUploadModel model)",
                        "create(...) create data exception"
                )));
    }

    /**
     * Update Data by Model
     *
     * @param model File Upload Model Object
     * @return File Upload Model Object
     */
    @SuppressWarnings("ALL")
    public Mono<FileUploadModel> update(FileUploadModel model) {
        if (model == null) {
            return Mono.error(new DataBaseException(
                    this.getClass(),
                    "fun update(FileUploadModel model)",
                    "update(...) file upload model object data is null."
            ));
        }
        if (model.getId() == null) {
            return Mono.error(new DataBaseException(
                    this.getClass(),
                    "fun update(FileUploadModel model)",
                    "update(...) file upload model [id] data is null."
            ));
        }
        return select(model.getId())
                .flatMap(m -> {
                    final List<Map<String, Object>> data = new ArrayList<>();
                    model.setVersion(m.getVersion());
                    model.setModificationDateTime(LocalDateTime.now());
                    data.add(Map.of(
                            "field", "modification_date_time_",
                            "variable", "MODIFICATION_DATE_TIME",
                            "value", model.getModificationDateTime()
                    ));
                    if (model.getName() != null) {
                        data.add(Map.of(
                                "field", "name_",
                                "variable", "NAME",
                                "value", model.getName()
                        ));
                    }
                    if (model.getSize() != null) {
                        data.add(Map.of(
                                "field", "size_",
                                "variable", "SIZE",
                                "value", model.getSize()
                        ));
                    }
                    if (model.getSource() != null) {
                        data.add(Map.of(
                                "field", "source_",
                                "variable", "SOURCE",
                                "value", model.getSource()
                        ));
                    }
                    if (model.getStorageType() != null) {
                        data.add(Map.of(
                                "field", "storage_type_",
                                "variable", "STORAGE_TYPE",
                                "value", model.getStorageType()
                        ));
                    }
                    if (model.getStorageLocation() != null) {
                        data.add(Map.of(
                                "field", "storage_location_",
                                "variable", "STORAGE_LOCATION",
                                "value", model.getStorageLocation()
                        ));
                    }
                    if (model.getOwner() != null) {
                        data.add(Map.of(
                                "field", "owner_",
                                "variable", "OWNER",
                                "value", model.getOwner()
                        ));
                    }
                    if (model.getModifier() != null) {
                        data.add(Map.of(
                                "field", "modifier_",
                                "variable", "MODIFIER",
                                "value", model.getModifier()
                        ));
                    }
                    final StringBuilder content = new StringBuilder();
                    for (final Map<String, Object> item : data) {
                        content.append(",")
                                .append("\r\n")
                                .append(item.get("field"))
                                .append(" ")
                                .append("=")
                                .append(" ")
                                .append(":")
                                .append(item.get("variable"));
                    }
                    final String sql = TemplateParser.execute("""
                            UPDATE
                                @{TABLE}
                            SET
                                version_ = :NEW_VERSION
                                @{CONTENT}
                            WHERE
                                id_ = :ID
                                AND version_ = :OLD_VERSION
                            ;""", Map.of("TABLE", getFileUploadTable(), "CONTENT", content.toString()));
                    DatabaseClient.GenericExecuteSpec spec = client.sql(sql);
                    for (final Map<String, Object> item : data) {
                        spec = spec.bind(item.get("variable").toString(), item.get("value"));
                    }
                    spec = spec.bind("ID", model.getId());
                    spec = spec.bind("OLD_VERSION", model.getVersion());
                    spec = spec.bind("NEW_VERSION", model.getVersion() + 1);
                    spec = spec.bind("MODIFICATION_DATE_TIME", model.getModificationDateTime());
                    return spec
                            .fetch()
                            .rowsUpdated()
                            .map(_ -> {
                                model.setId(m.getId());
                                model.setName(m.getName());
                                model.setSize(m.getSize());
                                model.setSource(m.getSource());
                                model.setOwner(m.getOwner());
                                model.setStorageType(m.getStorageType());
                                model.setStorageLocation(m.getStorageLocation());
                                model.setLock(m.getLock());
                                model.setCreator(m.getCreator());
                                model.setModifier(m.getModifier());
                                model.setCreationDateTime(m.getCreationDateTime());
                                model.setModificationDateTime(model.getModificationDateTime());
                                model.setVersion(model.getVersion() + 1);
                                return model;
                            })
                            .switchIfEmpty((Mono.error(new DataBaseException(
                                    this.getClass(),
                                    "fun update(FileUploadModel model)",
                                    "update(...) update data exception"
                            ))));
                });
    }

    /**
     * Delete
     *
     * @param id ID
     * @return Long Delete Rows Count
     */
    @SuppressWarnings("ALL")
    public Mono<Long> delete(Integer id) {
        return client
                .sql(TemplateParser.execute("""
                        DELETE FROM    
                            @{TABLE}
                        WHERE
                            id = :ID    
                        ;""", Map.of("TABLE", getFileUploadTable())))
                .bind("ID", id)
                .fetch()
                .rowsUpdated()
                .switchIfEmpty(Mono.error(new DataBaseException(
                        this.getClass(),
                        "fun delete(int id)",
                        "delete(...) delete data exception"
                )));
    }

    /**
     * Select
     *
     * @param id ID
     * @return File Upload Model Object
     */
    @SuppressWarnings("ALL")
    public Mono<FileUploadModel> select(Integer id) {
        return client
                .sql(TemplateParser.execute("""
                            SELECT
                                id_,
                                name_,
                                size_,
                                source_,
                                owner_,
                                storage_type_,
                                storage_location_,
                                lock_,
                                creator_,
                                modifier_,
                                creation_date_time_,
                                modification_date_time_,
                                version_
                            FROM
                                @{TABLE}
                            WHERE
                                id_ = :ID
                        ;""", Map.of("TABLE", getFileUploadTable())))
                .bind("ID", id)
                .fetch()
                .first()
                .map(row -> {
                    final FileUploadModel model = new FileUploadModel();
                    model.setId(TransformationUtil.objectToInteger(row.get("id_")));
                    model.setName(TransformationUtil.objectToString(row.get("name_")));
                    model.setSize(TransformationUtil.objectToLong(row.get("size_")));
                    model.setSource(TransformationUtil.objectToString(row.get("source_")));
                    model.setOwner(TransformationUtil.objectToString(row.get("owner_")));
                    model.setStorageType(TransformationUtil.objectToString(row.get("storage_type_")));
                    model.setStorageLocation(TransformationUtil.objectToString(row.get("storage_location_")));
                    model.setLock(TransformationUtil.objectToInteger(row.get("lock_")));
                    model.setCreator(TransformationUtil.objectToString(row.get("creator_")));
                    model.setModifier(TransformationUtil.objectToString(row.get("modifier_")));
                    model.setCreationDateTime(TransformationUtil.objectToLocalDateTime(row.get("creation_date_time_")));
                    model.setModificationDateTime(TransformationUtil.objectToLocalDateTime(row.get("modification_date_time_")));
                    model.setVersion(TransformationUtil.objectToInteger(row.get("version_")));
                    return model;
                })
                .switchIfEmpty(Mono.error(new DataBaseException(
                        this.getClass(),
                        "fun select(int id)",
                        "select(...) select data exception"
                )));
    }

}
