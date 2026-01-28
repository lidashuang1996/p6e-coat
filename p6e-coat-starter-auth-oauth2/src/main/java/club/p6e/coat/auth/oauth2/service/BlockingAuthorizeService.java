package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface BlockingAuthorizeService {
    IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request validate);


}
