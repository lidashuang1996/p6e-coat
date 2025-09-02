package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.repository.UserRepository;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Register Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(RegisterService.class)
public class RegisterServiceImpl implements RegisterService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * User Repository Object
     */
    private final UserRepository userRepository;

    /**
     * Constructor Initialization
     *
     * @param encryptor      Password Encryptor Object
     * @param userRepository User Repository Object
     */
    public RegisterServiceImpl(
            PasswordEncryptor encryptor,
            UserRepository userRepository
    ) {
        this.encryptor = encryptor;
        this.userRepository = userRepository;
    }

    @Override
    public RegisterContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.Request param
    ) {
        param.setPassword(encryptor.execute(param.getPassword()));
        final String account = TransformationUtil.objectToString(httpServletRequest.getAttribute(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT));
        final User user = switch (Properties.getInstance().getMode()) {
            case PHONE -> executePhoneMode(account, param);
            case MAILBOX -> executeMailboxMode(account, param);
            case ACCOUNT -> executeAccountMode(account, param);
            case PHONE_OR_MAILBOX -> executePhoneOrMailboxMode(account, param);
        };
        if (user == null) {
            return null;
        } else {
            final RegisterContext.Dto result = new RegisterContext.Dto();
            result.getData().putAll(user.toMap());
            return result;
        }
    }

    /**
     * Execute Account Register
     *
     * @return User Object
     */
    protected User executeAccountMode(String account, RegisterContext.Request param) {
        User user = userRepository.findByAccount(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executeAccountMode(String account, RegisterContext.Request param)",
                    "create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = userRepository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executeAccountMode(String account, RegisterContext.Request param)",
                        "create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

    /**
     * Execute Phone Register
     *
     * @return User Object
     */
    private User executePhoneMode(String account, RegisterContext.Request param) {
        User user = userRepository.findByPhone(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executePhoneMode(String account, RegisterContext.Request param)",
                    "create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = userRepository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executePhoneMode(String account, RegisterContext.Request param)",
                        "create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

    /**
     * Execute Mailbox Register
     *
     * @return User Object
     */
    private User executeMailboxMode(String account, RegisterContext.Request param) {
        User user = userRepository.findByMailbox(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executeMailboxMode(String account, RegisterContext.Request param)",
                    "create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = userRepository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executeMailboxMode(String account, RegisterContext.Request param)",
                        "create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

    /**
     * 执行手机号码/邮箱登录
     *
     * @return 结果对象
     */
    protected User executePhoneOrMailboxMode(String account, RegisterContext.Request param) {
        User user = userRepository.findByPhoneOrMailbox(account);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountExistException(
                    this.getClass(),
                    "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                    "create user account [ " + account + "/(exist) ] exception"
            );
        } else {
            user = userRepository.create(SpringUtil.getBean(UserBuilder.class).create(param.getData()));
            if (user == null) {
                throw GlobalExceptionContext.exceptionDataBaseException(
                        this.getClass(),
                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                        "create user account data exception"
                );
            } else {
                return user;
            }
        }
    }

}
