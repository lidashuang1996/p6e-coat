package club.p6e.coat.message.center.repository;

import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.launcher.LauncherModel;
import club.p6e.coat.message.center.template.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Source Repository
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(DataSourceRepository.class)
public class DataSourceRepository {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRepository.class);

    /**
     * Jdbc Template Object
     */
    private final JdbcTemplate template;

    /**
     * Construct Initialization
     *
     * @param template Jdbc Template Object
     */
    public DataSourceRepository(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Data Object Blob To Bytes
     *
     * @param blob Blob Object
     * @return Bytes Object
     */
    protected static byte[] blobToBytes(Blob blob) {
        if (blob == null) {
            return null;
        }
        try (
                InputStream inputStream = blob.getBinaryStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            int read;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        } catch (SQLException | IOException e) {
            LOGGER.error("[ DATA SOURCE ] BLOB TO BYTES ERROR", e);
            return null;
        }
    }

    /**
     * Get Config Table Name
     *
     * @return Config Table Name
     */
    protected String getConfigTableName() {
        return "ss_message_center_config";
    }

    /**
     * Get Template Table Name
     *
     * @return Template Table Name
     */
    protected String getTemplateTableName() {
        return "ss_message_center_template";
    }

    /**
     * Get Launcher Table Name
     *
     * @return Launcher Table Name
     */
    protected String getLauncherTableName() {
        return "ss_message_center_launcher";
    }

    /**
     * Get Launcher Config Mapper Table Name
     *
     * @return Launcher Config Mapper Table Name
     */
    protected String getLauncherConfigMapperTableName() {
        return "ss_message_center_launcher_config_mapper";
    }

    /**
     * Get Dictionary Table Name
     *
     * @return Dictionary Table Name
     */
    protected String getDictionaryTableName() {
        return "ss_message_center_dictionary";
    }

    /**
     * Get Log Table Name
     *
     * @return Log Table Name
     */
    protected String getLogTableName() {
        return "ss_message_center_log";
    }

    /**
     * Get Config Model Object
     *
     * @param configId Config ID
     * @return Config Model Object
     */
    @SuppressWarnings("ALL")
    public ConfigModel getConfigData(int configId) {
        return template.query(TemplateParser.execute("""
                    SELECT
                        _MCC_.id,
                        _MCC_.rule,
                        _MCC_.type,
                        _MCC_.enable,
                        _MCC_.name,
                        _MCC_.content,
                        _MCC_.description,
                        _MCC_.parser,
                        _MCC_.parser_source
                    FROM
                        @{TABLE} AS _MCC_
                    WHERE
                        _MCC_.id = ?
                    ;
                """, "TABLE", getConfigTableName()
        ), new Object[]{configId}, (ResultSetExtractor<ConfigModel>) rs -> {
            final Integer id = rs.getInt("id");
            final String rule = rs.getString("rule");
            final String type = rs.getString("type");
            final Integer enable = rs.getInt("enable");
            final String name = rs.getString("name");
            final String content = rs.getString("content");
            final String description = rs.getString("description");
            final String parser = rs.getString("parser");
            final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
            return new ConfigModel() {
                @Override
                public int id() {
                    return id;
                }

                @Override
                public String rule() {
                    return rule;
                }

                @Override
                public MessageCenterType type() {
                    return MessageCenterType.valueOf(type.toLowerCase());
                }

                @Override
                public boolean enable() {
                    return "1".equals(String.valueOf(enable));
                }

                @Override
                public String name() {
                    return name;
                }

                @Override
                public String content() {
                    return content;
                }

                @Override
                public String description() {
                    return description;
                }

                @Override
                public String parser() {
                    return parser;
                }

                @Override
                public byte[] parserSource() {
                    return parserSource;
                }

            };
        });
    }

    /**
     * Update Config Content Data
     *
     * @param configId Config ID
     * @param content  Config Content
     */
    @SuppressWarnings("ALL")
    public void updateConfigContent(int configId, String content) {
        template.update(TemplateParser.execute("""
                    UPDATE
                        @{TABLE} AS _MCC_
                    SET
                        _MCC_.content = ?
                    WHERE
                        _MCC_.id = ?
                    ;
                """, "TABLE", getConfigTableName()
        ), content, configId);
    }

    /**
     * Get Template Model Object
     *
     * @param templateKey      Template Key
     * @param templateLanguage Template Language
     * @return Template Model Object
     */
    @SuppressWarnings("ALL")
    public TemplateModel getTemplateData(String templateKey, String templateLanguage) {
        return template.query(TemplateParser.execute("""
                    SELECT
                        _MCT_.id,
                        _MCT_.key,
                        _MCT_.type,
                        _MCT_.name,
                        _MCT_.language,
                        _MCT_.title,
                        _MCT_.content,
                        _MCT_.description,
                        _MCT_.parser,
                        _MCT_.parser_source
                    FROM
                        @{TABLE} AS _MCT_
                    WHERE
                        _MCT_.key = ?
                        AND 
                        _MCT_.language = ?
                    ;
                """, "TABLE", getTemplateTableName()
        ), new Object[]{templateKey, templateLanguage}, (ResultSetExtractor<TemplateModel>) rs -> {
            final Integer id = rs.getInt("id");
            final String key = rs.getString("key");
            final String type = rs.getString("type");
            final String name = rs.getString("name");
            final String language = rs.getString("language");
            final String title = rs.getString("title");
            final String content = rs.getString("content");
            final String description = rs.getString("description");
            final String parser = rs.getString("parser");
            final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
            return new TemplateModel() {

                @Override
                public Integer id() {
                    return id;
                }

                @Override
                public String key() {
                    return key;
                }

                @Override
                public String type() {
                    return type;
                }

                @Override
                public String name() {
                    return name;
                }

                @Override
                public String language() {
                    return language;
                }

                @Override
                public String title() {
                    return title;
                }

                @Override
                public String content() {
                    return content;
                }

                @Override
                public String description() {
                    return description;
                }

                @Override
                public String parser() {
                    return parser;
                }

                @Override
                public byte[] parserSource() {
                    return parserSource;
                }
            };
        });
    }

    /**
     * Get Launcher Model Object
     *
     * @param launcherId Launcher ID
     * @return Launcher Model Object
     */
    @SuppressWarnings("ALL")
    public LauncherModel getLauncherData(int launcherId) {
        return template.query(TemplateParser.execute("""
                    SELECT
                        _MCL_.id,
                        _MCL_.enable,
                        _MCL_.type,
                        _MCL_.name,
                        _MCL_.template,
                        _MCL_.route,
                        _MCL_.route_source,
                        _MCL_.parser,
                        _MCL_.parser_source
                    FROM
                        @{TABLE} AS _MCL_
                    WHERE
                        _MCL_.id = ?
                    ;
                """, "TABLE", getLauncherTableName()
        ), new Object[]{launcherId}, (ResultSetExtractor<LauncherModel>) rs -> {
            final List<LauncherModel.ConfigMapperModel> configs = getLauncherConfigMappers(launcherId);
            final Integer id = rs.getInt("id");
            final Integer enable = rs.getInt("enable");
            final String type = rs.getString("type");
            final String name = rs.getString("name");
            final String template = rs.getString("template");
            final String route = rs.getString("route");
            final byte[] routeSource = blobToBytes(rs.getBlob("route_source"));
            final String parser = rs.getString("parser");
            final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
            final String description = rs.getString("description");
            return new LauncherModel() {

                @Override
                public Integer id() {
                    return id;
                }

                @Override
                public boolean enable() {
                    return "1".equals(String.valueOf(enable));
                }

                @Override
                public MessageCenterType type() {
                    return MessageCenterType.valueOf(type.toUpperCase());
                }

                @Override
                public String name() {
                    return name;
                }

                @Override
                public String template() {
                    return template;
                }

                @Override
                public String description() {
                    return description;
                }

                @Override
                public String route() {
                    return route;
                }

                @Override
                public byte[] routeSource() {
                    return routeSource;
                }

                @Override
                public String parser() {
                    return parser;
                }

                @Override
                public byte[] parserSource() {
                    return parserSource;
                }

                @Override
                public List<ConfigMapperModel> configs() {
                    return configs;
                }
            };
        });
    }

    /**
     * Get Launcher Mapper Config Model Object
     *
     * @param launcherId Launcher ID
     * @return Launcher Mapper Config Model Object
     */
    @SuppressWarnings("ALL")
    public List<LauncherModel.ConfigMapperModel> getLauncherConfigMappers(int launcherId) {
        return template.query(TemplateParser.execute("""
                    SELECT
                        _MCLC_.lid,
                        _MCLC_.cid,
                        _MCLC_.attribute
                    FROM
                        @{TABLE} AS _MCLC_
                    WHERE
                        _MCLC_.lid = ?
                    ;
                """, "TABLE", getLauncherConfigMapperTableName()
        ), new Object[]{launcherId}, rs -> {
            final List<LauncherModel.ConfigMapperModel> result = new ArrayList<>();
            while (rs.next()) {
                final Integer cid = rs.getInt("cid");
                final String attribute = rs.getString("attribute");
                result.add(new LauncherModel.ConfigMapperModel() {
                    @Override
                    public Integer id() {
                        return cid;
                    }

                    @Override
                    public String attribute() {
                        return attribute;
                    }
                });
            }
            return result;
        });
    }

    /**
     * Get Dictionary Data List
     *
     * @return Dictionary Data List
     */
    @SuppressWarnings("ALL")
    public List<Map<String, Object>> getDictionaryData() {
        return template.query(TemplateParser.execute("""
                    SELECT
                        _MCD_.id,
                        _MCD_.key,
                        _MCD_.value,
                        _MCD_.language
                    FROM
                        @{TABLE} AS _MCD_
                    ;
                """, "TABLE", getDictionaryTableName()
        ), (ResultSetExtractor<List<Map<String, Object>>>) rs -> {
            final List<Map<String, Object>> result = new ArrayList<>();
            while (rs.next()) {
                final Integer id = rs.getInt("id");
                final String key = rs.getString("key");
                final String value = rs.getString("value");
                final String language = rs.getString("language");
                result.add(new HashMap<>() {{
                    put("id", id);
                    put("key", key);
                    put("value", value);
                    put("language", language);
                }});
            }
            return result;
        });
    }

    /**
     * Create Log Data
     *
     * @param no       Log Num
     * @param parent   Log Parent Num
     * @param params   Request Params
     * @param lid      Launcher ID
     * @param tid      Template ID
     * @param cid      Config ID
     * @param dateTime Date Time Object
     */
    @SuppressWarnings("ALL")
    public void createLog(String no, String parent, String params, int lid, int tid, int cid, LocalDateTime dateTime, String operator) {
        template.update(TemplateParser.execute("""
                    INSET INTO @{TABLE} (
                            no,
                            parent,
                            params,
                            lid,
                            tid,
                            cid,
                            date_time,
                            result,
                            result_date_time,
                            creator,
                            modifier,
                            creation_date_time,
                            modification_date_time,
                            version
                        )
                    VALUES (
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?
                    )
                    ;
                """, "TABLE", getLogTableName()
        ), no, parent, params, lid, tid, cid, dateTime, null, null, operator, operator, LocalDateTime.now(), LocalDateTime.now(), 0);
    }

    /**
     * Update Log Data
     *
     * @param no             Log Num
     * @param result         Log Result
     * @param resultDateTime Log Result Date Time Object
     */
    @SuppressWarnings("ALL")
    public void updateLog(String no, String result, LocalDateTime resultDateTime) {
        template.update(TemplateParser.execute("""
                    UPDATE
                        @{TABLE} AS _MCLOG_
                    SET
                        _MCLOG_.result = ?,
                        _MCLOG_.result_date_time = ?
                    WHERE
                        _MCLOG_.no = ?
                    ;
                """, "TABLE", getLogTableName()
        ), result, resultDateTime, no);
    }

}
