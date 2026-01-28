package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.BlockingVoucherCache;
import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.model.ClientModel;
import club.p6e.coat.auth.oauth2.repository.BlockingRepository;
import club.p6e.coat.common.error.Oauth2ClientException;
import club.p6e.coat.common.error.Oauth2ParameterException;
import club.p6e.coat.common.error.Oauth2RedirectUriException;
import club.p6e.coat.common.error.Oauth2ScopeException;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class BlockingAuthorizeServiceImpl implements BlockingAuthorizeService {

    private static final String CODE_MODE = "CODE";

    private final BlockingRepository repository;

    /**
     * Blocking Voucher Cache Object
     */
    private final BlockingVoucherCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Blocking Voucher Cache Object
     */
    public BlockingAuthorizeServiceImpl(BlockingVoucherCache cache) {
        this.cache = cache;
    }

    @Override
    public IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request) {
        final String scope = request.getScope();
        final String state = request.getState();
        final String clientId = request.getClientId();
        final String redirectUri = request.getRedirectUri();
        final String responseType = request.getResponseType();
        if (!CODE_MODE.equalsIgnoreCase(responseType)) {
            throw new Oauth2ParameterException(this.getClass(), "", "");
        }
        final ClientModel client = repository.findClientByAppId(clientId);
        if (client == null) {
            throw new Oauth2ClientException(this.getClass(), "", "");
        }
        if (client.getEnable() != 1) {
            throw new Oauth2ClientException(this.getClass(), "", "");
        }
        if (!VerificationUtil.validationOAuth2Scope(client.getScope(), scope)) {
            throw new Oauth2ScopeException(this.getClass(), "", "");
        }
        if (!VerificationUtil.validationOAuth2RedirectUri(client.getRedirectUri(), redirectUri)) {
            throw new Oauth2RedirectUriException(this.getClass(), "", "");
        }
        final Properties.Page page = Properties.getInstance().getLogin().getPage();
        final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        cache.set(voucher, new HashMap<>() {{
            put("scope", scope);
            put("state", state);
            put("clientId", clientId);
            put("redirectUri", redirectUri);
            put("responseType", responseType);
            put("type", "OAUTH2");
            put("time", String.valueOf(System.currentTimeMillis()));
        }});
        return new IndexContext.Dto().setType(page.getType()).setContent(TemplateParser.execute(page.getContent(), "VOUCHER", voucher));
    }

}
