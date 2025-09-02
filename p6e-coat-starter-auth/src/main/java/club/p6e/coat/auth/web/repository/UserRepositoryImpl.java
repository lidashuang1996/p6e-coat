package club.p6e.coat.auth.web.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.user.SimpleUserModel;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private final JdbcTemplate template;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager transactionManager;
    private final PlatformTransactionManager transactionManager;

    public UserRepositoryImpl(DatabaseClient client) {
        this.template = client;
    }

    private User convertResultSetToUser(ResultSet rs) {
        return SpringUtil.getBean(UserBuilder.class).create(new HashMap<>() {{
            put("id", TransformationUtil.objectToString(readable.get("id")));
            put("status", TransformationUtil.objectToString(readable.get("status")));
            put("enabled", TransformationUtil.objectToString(readable.get("enabled")));
            put("internal", TransformationUtil.objectToString(readable.get("internal")));
            put("administrator", TransformationUtil.objectToString(readable.get("administrator")));
            put("account", TransformationUtil.objectToString(readable.get("account")));
            put("phone", TransformationUtil.objectToString(readable.get("phone")));
            put("mailbox", TransformationUtil.objectToString(readable.get("mailbox")));
            put("name", TransformationUtil.objectToString(readable.get("name")));
            put("nickname", TransformationUtil.objectToString(readable.get("nickname")));
            put("language", TransformationUtil.objectToString(readable.get("language")));
            put("avatar", TransformationUtil.objectToString(readable.get("avatar")));
            put("description", TransformationUtil.objectToString(readable.get("description")));
        }});
    }

    @SuppressWarnings("ALL")
    @Override
    public User findById(Integer id) {
        final List<User> list = template.query(TemplateParser.execute(
                BASE_USER_SELECT_SQL + " WHERE _user.id = ? ORDER BY _user.id ASC LIMIT 1 ; ",
                "TABLE", getUserTableName()
        ), new Object[]{id}, new BeanPropertyRowMapper<>(User.class));
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByAccount(String account) {
        final List<User> list = template.query(TemplateParser.execute(
                BASE_USER_SELECT_SQL + " WHERE _user.account = ? ORDER BY _user.id ASC LIMIT 1 ; ",
                "TABLE", getUserTableName()
        ), new Object[]{account}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByPhone(String phone) {
        final List<User> list = template.query(TemplateParser.execute(
                BASE_USER_SELECT_SQL + " WHERE _user.phone = ? ORDER BY _user.id ASC LIMIT 1 ; ",
                "TABLE", getUserTableName()
        ), new Object[]{phone}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByMailbox(String mailbox) {
        final List<User> list = template.query(TemplateParser.execute(
                BASE_USER_SELECT_SQL + " WHERE _user.mailbox = ? ORDER BY _user.id ASC LIMIT 1 ; ",
                "TABLE", getUserTableName()
        ), new Object[]{mailbox}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @SuppressWarnings("ALL")
    @Override
    public User findByPhoneOrMailbox(String account) {
        final List<User> list = template.query(TemplateParser.execute(
                BASE_USER_SELECT_SQL + " WHERE ( _user.mailbox = ? OR _user.phone = ? ) ORDER BY _user.id ASC LIMIT 1 ; ",
                "TABLE", getUserTableName()
        ), new Object[]{account, account}, rs -> {
            final List<User> result = new ArrayList<>();
            while (rs.next()) {
                result.add(convertResultSetToUser(rs));
            }
            return result;
        });
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @Override
    public User create(User user) {
        if (user instanceof SimpleUserModel sum) {
            return transactionTemplate.execute((TransactionCallback<User>) status -> {
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
                        """, "TABLE", getUserAuthTableName()
                    ));
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
                        """, "TABLE", getUserAuthTableName()
                    ));
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
                return sum;
            });
        }
        return null;
    }

    @Override
    public User updatePassword(Integer uid, String password) {
        return transactionTemplate.execute(status -> {
            try {
                final int result = template.update(TemplateParser.execute(
                        "UPDATE @{TABLE} AS _user_auth SET password = ? WHERE _user_auth.id = ? ; ",
                        "TABLE", getUserAuthTableName()
                ), password, uid);
                return result > 0 ? findById(uid) : null;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
        });
    }

    protected String getUserTableName() {
        return "p6e_user";
    }

    protected String getUserAuthTableName() {
        return "p6e_user_auth";
    }

}
