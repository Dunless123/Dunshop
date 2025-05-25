package com.dundunteam.im;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import com.dundunteam.service.WxAuthService;

import java.util.Map;

@Component
public class AuthInterceptor implements HandshakeInterceptor {

    private final WxAuthService wxAuthService;

    public AuthInterceptor(WxAuthService wxAuthService) {
        this.wxAuthService = wxAuthService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        // 从请求参数获取微信临时code
        HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
        String code = req.getParameter("code");

        // 换取openId
        String openId = wxAuthService.getOpenIdByCode(code);
        if (openId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        // 绑定openId到会话属性
        attributes.put("openId", openId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后无操作
    }
}