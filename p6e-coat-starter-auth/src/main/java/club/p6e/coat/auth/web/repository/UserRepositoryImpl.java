package club.p6e.coat.auth.web.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.user.SimpleUserModel;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User Repository Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(UserRepository.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class UserRepositoryImpl implements UserRepository {

    /**
     * Base User Select SQL
     */
    private static final String BASE_USER_SELECT_SQL = """
            SELECT
                _user.id,
                _user.status,
                _user.enabled,
                _user.internal,
                _user.administrator,
                _user.account,
                _user.phone,
                _user.mailbox,
                _user.name,
                _user.nickname,
                _user.language,
                _user.avatar,
                _user.description
            FROM
                @{TABLE} AS _user
            """;

    /**
     * Jdbc Template Object
     */
    private final JdbcTemplate template;

    /**
     * Transaction Manager Object
     */
    private final TransactionTemplate transactionTemplate;

    /**
     * Constructor Initialization
     *
     * @param template           Jdbc Template Object
     * @param transactionManager Transaction Manager Object
     */
    public UserRepositoryImpl(JdbcTemplate template, PlatformTransactionManager transactionManager) {
        this.template = template;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Covert Result Set To User Object
     *
     * @param rs Result Set Object
     * @return User Object
     * @throws SQLException Result Set SQL Exception
     */
    private User convertResultSetToUser(ResultSet rs) throws SQLException {
        return SpringUtil.getBean(UserBuilder.class).create(new HashMap<>() {{
            put("id", TransformationUtil.objectToString(rs.getLong("id")));
            put("status", TransformationUtil.objectToString(rs.getInt("status")));
            put("enabled", TransformationUtil.objectToString(rs.getInt("enabled")));
            put("internal", TransformationUtil.objectToString(rs.getInt("internal")));
            put("administrator", TransformationUtil.objectToString(rs.getInt("administrator")));
            put("account", TransformationUtil.objectToString(rs.getString("account")));
            put("phone", TransformationUtil.objectToString(rs.getString("phone")));
            put("mailbox", TransformationUtil.objectToString(rs.getString("mailbox")));
            put("name", TransformationUtil.objectToString(rs.getString("name")));
            put("nickname", TransformationUtil.objectToString(rs.getString("nickname")));
            put("language", TransformationUtil.objectToString(rs.getString("language")));
            put("avatar", TransformationUtil.objectToString(rs.getString("avatar")));
            put("description", TransformationUtil.objectToString(rs.getString("description")));
        }});
    }

    /**
     * Get User Table Name
     *
     * @return User Table Name
     */
    protected String getUserTableName() {
        return "p6e_user";
    }

    /**
     * Get User Auth Table Name
     *
     * @return User Auth Table Name
     */
    protected String getUserAuthTableName() {
        return "p6e_user_auth";
    }

    @SuppressWarnings("ALL")
    protected String findPasswordById(Integer id) {
        final List<String> list = template.query(TemplateParser.execute("SELECT _user_auth.id, _user_auth.password FROM @{TABLE} AS _user_auth WHERE _user_auth.id = :ID ; ", "TABLE", getUserAuthTableName()), new Object[]{id}, (ResultSetExtractor<List<String>>) rs -> List.of(rs.getString(2)));
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @SuppressWarnings("ALL")
    @Override
    public User findById(Integer id) {
        final List<User> list = template.query(TemplateParser.execute(BASE_USER_SELECT_SQL + " WHERE _user.id = ? ORDER BY _user.id ASC LIMIT 1 ; ", "TABLE", getUserTableName()), new Object[]{id}, new BeanPropertyRowMapper<>(User.class));
        return (list == null || list.isEmpty()) ? null : list.get(0).password(findPasswordById(id));
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByAccount(String account) {
        final List<User> list = template.query(TemplateParser.execute(BASE_USER_SELECT_SQL + " WHERE _user.account = ? ORDER BY _user.id ASC LIMIT 1 ; ", "TABLE", getUserTableName()), new Object[]{account}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0).password(findPasswordById(Integer.valueOf(list.get(0).id())));
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByPhone(String phone) {
        final List<User> list = template.query(TemplateParser.execute(BASE_USER_SELECT_SQL + " WHERE _user.phone = ? ORDER BY _user.id ASC LIMIT 1 ; ", "TABLE", getUserTableName()), new Object[]{phone}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0).password(findPasswordById(Integer.valueOf(list.get(0).id())));
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByMailbox(String mailbox) {
        final List<User> list = template.query(TemplateParser.execute(BASE_USER_SELECT_SQL + " WHERE _user.mailbox = ? ORDER BY _user.id ASC LIMIT 1 ; ", "TABLE", getUserTableName()), new Object[]{mailbox}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0).password(findPasswordById(Integer.valueOf(list.get(0).id())));
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByPhoneOrMailbox(String account) {
        final List<User> list = template.query(TemplateParser.execute(BASE_USER_SELECT_SQL + " WHERE ( _user.mailbox = ? OR _user.phone = ? ) ORDER BY _user.id ASC LIMIT 1 ; ", "TABLE", getUserTableName()), new Object[]{account, account}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0).password(findPasswordById(Integer.valueOf(list.get(0).id())));
    }

    @SuppressWarnings("ALL")
    @Override
    public User create(User user) {
        if (user instanceof SimpleUserModel sum) {
            return transactionTemplate.execute((TransactionCallback<User>) status -> {
                try {
                    template.update(connection -> {
                        final PreparedStatement ps = connection.prepareStatement(TemplateParser.execute("""
                                INSERT INTO @{TABLE} (
                                    status ,
                                    enabled ,
                                    internal ,
                                    administrator ,
                                    account ,
                                    phone ,
                                    mailbox ,
                                    name ,
                                    nickname ,
                                    language ,
                                    avatar ,
                                    description ,
                                    creator ,
                                    modifier ,
                                    creation_date_time ,
                                    modification_date_time ,
                                    version ,
                                    is_deleted
                                ) VALUES (
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? 
                                )  RETURNING id ;
                                """, "TABLE", getUserTableName()));
                        ps.setInt(1, sum.getStatus() == null ? 1 : sum.getStatus());
                        ps.setInt(2, sum.getEnabled() == null ? 1 : sum.getEnabled());
                        ps.setInt(3, sum.getInternal() == null ? 0 : sum.getInternal());
                        ps.setInt(4, sum.getAdministrator() == null ? 0 : sum.getAdministrator());
                        ps.setString(5, sum.getAccount());
                        ps.setString(6, sum.getPhone());
                        ps.setString(7, sum.getMailbox());
                        ps.setString(8, sum.getName());
                        ps.setString(9, sum.getNickname());
                        ps.setString(10, sum.getLanguage() == null ? "zh-cn" : sum.getLanguage());
                        ps.setString(11, sum.getAvatar());
                        ps.setString(12, sum.getDescription());
                        ps.setString(13, "sys");
                        ps.setString(14, "sys");
                        ps.setTime(15, Time.valueOf(LocalDateTime.now().toLocalTime()));
                        ps.setTime(16, Time.valueOf(LocalDateTime.now().toLocalTime()));
                        ps.setInt(17, 0);
                        ps.setInt(18, 0);
                        return ps;
                    });
                    template.update(connection -> {
                        final PreparedStatement ps = connection.prepareStatement(TemplateParser.execute("""
                                INSERT INTO @{TABLE} (
                                    id ,
                                    account ,
                                    phone ,
                                    mailbox ,
                                    password ,
                                    creator ,
                                    modifier ,
                                    creation_date_time ,
                                    modification_date_time ,
                                    version
                                ) VALUES (
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ? ,
                                    ?
                                )  RETURNING id ;
                                """, "TABLE", getUserAuthTableName()));
                        ps.setInt(1, sum.getId());
                        ps.setString(2, sum.getAccount());
                        ps.setString(3, sum.getPhone());
                        ps.setString(4, sum.getMailbox());
                        ps.setString(5, sum.getPassword());
                        ps.setString(6, "sys");
                        ps.setString(7, "sys");
                        ps.setTime(8, Time.valueOf(LocalDateTime.now().toLocalTime()));
                        ps.setTime(9, Time.valueOf(LocalDateTime.now().toLocalTime()));
                        ps.setInt(10, 0);
                        return ps;
                    });
                    return findById(Integer.valueOf(sum.id()));
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            });
        }
        return null;
    }

    @SuppressWarnings("ALL")
    @Override
    public User updatePassword(Integer uid, String password) {
        return transactionTemplate.execute(status -> {
            try {
                final int result = template.update(TemplateParser.execute("UPDATE @{TABLE} AS _user_auth SET password = ? WHERE _user_auth.id = ? ; ", "TABLE", getUserAuthTableName()), password, uid);
                return result > 0 ? findById(uid) : null;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
        });
    }


}
