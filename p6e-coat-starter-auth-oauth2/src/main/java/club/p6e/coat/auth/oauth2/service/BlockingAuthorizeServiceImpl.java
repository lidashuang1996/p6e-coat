package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.BlockingVoucherCache;
import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.model.ClientModel;
import club.p6e.coat.auth.oauth2.repository.BlockingClientRepository;
import club.p6e.coat.auth.oauth2.validator.BlockingRequestParameterValidator;
import club.p6e.coat.common.exception.*;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Blocking Authorize Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingAuthorizeService.class,
        ignored = BlockingAuthorizeServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.BlockingAuthorizeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingAuthorizeServiceImpl implements BlockingAuthorizeService {

    /**
     * Code Mode
     */
    private static final String CODE_MODE = "CODE";

    /**
     * Blocking Voucher Cache Object
     */
    private final BlockingVoucherCache cache;

    /**
     * Blocking Client Repository Object
     */
    private final BlockingClientRepository repository;

    /**
     * Constructor Initialization
     *
     * @param cache      Blocking Voucher Cache Object
     * @param repository Blocking Client Repository Object
     */
    public BlockingAuthorizeServiceImpl(BlockingVoucherCache cache, BlockingClientRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request) {
        final AuthorizeContext.Request content = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        final String scope = content.getScope();
        final String state = content.getState();
        final String clientId = content.getClientId();
        final String redirectUri = content.getRedirectUri();
        final String responseType = content.getResponseType();
        if (!CODE_MODE.equalsIgnoreCase(responseType)) {
            throw new OAuth2ParameterException(
                    this.getClass(),
                    "fun IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "request parameter response_type<" + responseType + "> not support"
            );
        }
        final ClientModel client = repository.findByAppId(clientId);
        if (client == null) {
            throw new OAuth2ClientException(
                    this.getClass(),
                    "fun IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "[" + CODE_MODE + "] client_id<" + clientId + "> not match"
            );
        }
        if (client.getEnable() != 1) {
            throw new OAuth2ClientException(
                    this.getClass(),
                    "fun IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "[" + CODE_MODE + "] client not enabled"
            );
        }
        if (!VerificationUtil.validateStringBelongCommaSeparatedString(client.getType(), CODE_MODE)) {
            throw new OAuth2ClientException(
                    this.getClass(),
                    "fun IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "[" + CODE_MODE + "] client type<" + CODE_MODE + "> not support"
            );
        }
        if (!VerificationUtil.validateStringBelongCommaSeparatedString(client.getScope(), scope)) {
            throw new OAuth2ScopeException(
                    this.getClass(),
                    "fun IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "[" + CODE_MODE + "] scope<" + scope + "> not match"
            );
        }
        if (!VerificationUtil.validateStringBelongCommaSeparatedString(client.getRedirectUri(), redirectUri)) {
            throw new OAuth2RedirectUriException(
                    this.getClass(),
                    "fun IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "[" + CODE_MODE + "] redirect_uri<" + redirectUri + "> not match"
            );
        }
        final Properties.Page page = Properties.getInstance().getLogin().getPage();
        final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        cache.set(voucher, new HashMap<>() {{
            put("scope", scope);
            put("state", state);
            put("clientId", clientId);
            put("clientName", client.getClientName());
            put("clientAvatar", client.getClientAvatar());
            put("clientDescription", client.getClientDescription());
            put("reconfirm", String.valueOf(client.getReconfirm()));
            put("redirectUri", redirectUri);
            put("responseType", responseType);
            put("type", "OAUTH2");
            put("time", String.valueOf(System.currentTimeMillis()));
        }});
        return new IndexContext.Dto().setType(page.getType()).setContent(TemplateParser.execute(page.getContent(), "VOUCHER", voucher));
    }

}
