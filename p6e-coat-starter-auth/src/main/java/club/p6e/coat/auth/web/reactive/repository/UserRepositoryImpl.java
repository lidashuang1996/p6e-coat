package club.p6e.coat.auth.web.reactive.repository;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.TransformationUtil;
import io.r2dbc.spi.Readable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = UserRepository.class,
        ignored = UserRepositoryImpl.class
)
public class UserRepositoryImpl implements UserRepository {

    private static final String USER_TABLE = "p6e_user";
    private static final String USER_AUTH_TABLE = "p6e_user_auth";

    @SuppressWarnings("ALL")
    private static final String BASE_SELECT_SQL = """
                SELECT "id", "status", "enabled", "internal", "administrator", "account", "phone",
                 "mailbox", "name", "nickname", "language", "avatar", "description" FROM "@{TABLE}"
            """;

    private final DatabaseClient client;

    public UserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    private User convertReadableToUser(Readable readable) {
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

    @Override
    public Mono<User> findById(Integer id) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"id\" = :ID ; ", "TABLE", USER_TABLE))
                .bind("ID", id)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> findByAccount(String account) {
        System.out.println("CCCCCCCCC >>> findByAccount  :::::: " + account);
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"account\" = :ACCOUNT ; ", "TABLE", USER_TABLE))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> {
                    System.out.println("bbbbbbbbbbb ?11111 ");
                    System.out.println("uuu ::" + u);
                    return findPasswordById(Integer.valueOf(u.id())).map(u::password);
                });
    }

    @Override
    public Mono<User> findByPhone(String phone) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"phone\" = :PHONE ; ", "TABLE", USER_TABLE))
                .bind("PHONE", phone)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> findByMailbox(String mailbox) {
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"mailbox\" = :MAILBOX ; ", "TABLE", USER_TABLE))
                .bind("MAILBOX", mailbox)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password));
    }

    @Override
    public Mono<User> findByPhoneOrMailbox(String account) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return client.sql(TemplateParser.execute(BASE_SELECT_SQL
                        + " WHERE \"phone\" = :ACCOUNT OR \"mailbox\" = :ACCOUNT ; ", "TABLE", USER_TABLE))
                .bind("ACCOUNT", account)
                .map(this::convertReadableToUser)
                .first()
                .flatMap(u -> findPasswordById(Integer.valueOf(u.id())).map(u::password))
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        System.out.println(" xx >>> " + throwable);
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public Mono<User> create(User user) {
        return null;
    }

    private Mono<String> findPasswordById(Integer id) {
        return client
                .sql(TemplateParser.execute("SELECT \"id\", \"password\" FROM "
                        + "\"@{TABLE}\" WHERE \"id\" = :ID ; ", "TABLE", USER_AUTH_TABLE))
                .bind("ID", id)
                .map(readable -> {
                    System.out.println("OOOOOOOPPPPPPPPP");
                    System.out.println(readable.get("password") + "?>>>>>>>>>");
                    return TransformationUtil.objectToString(readable.get("password"));
                })
                .first();
    }

}
