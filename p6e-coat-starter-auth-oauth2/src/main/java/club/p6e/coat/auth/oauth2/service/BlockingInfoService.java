package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.InfoContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface BlockingInfoService {

    Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request);
    Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request);


}
