package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Register Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = RegisterService.class,
        ignored = RegisterServiceImpl.class
)
@Component("club.p6e.coat.auth.service.RegisterServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class RegisterServiceImpl implements RegisterService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository User Repository Object
     */
    public RegisterServiceImpl(PasswordEncryptor encryptor, BlockingUserRepository repository) {
        this.encryptor = encryptor;
        this.repository = repository;
    }

    @Override
    public RegisterContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.Request param
    ) {
        param.setPassword(encryptor.execute(param.getPassword()));
        final String account = TransformationUtil.objectToString(httpServletRequest.getAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT));
        final User user = switch (Properties.getInstance().getMode()) {
            case PHONE -> executePhoneMode(account, param);
            case MAILBOX -> executeMailboxMode(account, param);
            case ACCOUNT -> executeAccountMode(account, param);
            case PHONE_OR_MAILBOX -> executePhoneOrMailboxMode(account, param);
        };
        final RegisterContext.Dto result = new RegisterContext.Dto();
        result.getData().putAll(user.toMap());
        return result;
    }

    /**
     * Execute Account Register
     *
     * @param account Account
     * @param param   Register Context Request Object
     * @return User Object
     */
    protected User executeAccountMode(String account, RegisterContext.Request param) {
        User user = repository.findByAccount(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executeAccountMode(String account, RegisterContext.Request param)",
                    "register create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = repository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executeAccountMode(String account, RegisterContext.Request param)",
                        "register create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

    /**
     * Execute Phone Register
     *
     * @param account Account
     * @param param   Register Context Request Object
     * @return User Object
     */
    private User executePhoneMode(String account, RegisterContext.Request param) {
        User user = repository.findByPhone(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executePhoneMode(String account, RegisterContext.Request param)",
                    "register create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = repository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executePhoneMode(String account, RegisterContext.Request param)",
                        "register create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

    /**
     * Execute Mailbox Register
     *
     * @param account Account
     * @param param   Register Context Request Object
     * @return User Object
     */
    private User executeMailboxMode(String account, RegisterContext.Request param) {
        User user = repository.findByMailbox(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executeMailboxMode(String account, RegisterContext.Request param)",
                    "register create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = repository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executeMailboxMode(String account, RegisterContext.Request param)",
                        "register create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

    /**
     * Execute Phone Or Mailbox Register
     *
     * @param account Account
     * @param param   Register Context Request Object
     * @return User Object
     */
    protected User executePhoneOrMailboxMode(String account, RegisterContext.Request param) {
        User user = repository.findByPhoneOrMailbox(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                    "register create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = repository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                        "register create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

}
