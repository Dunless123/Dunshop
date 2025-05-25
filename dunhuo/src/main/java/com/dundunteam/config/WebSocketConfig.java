package com.dundunteam.config;

import com.dundunteam.im.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket 全局配置类
 */
@Configuration
@EnableWebSocket // 启用WebSocket支持
public class WebSocketConfig implements WebSocketConfigurer {

    // 依赖注入身份验证拦截器
    private final AuthInterceptor authInterceptor;
    // 依赖注入消息处理器
    private final ChatMessageHandler messageHandler;

    @Autowired
    public WebSocketConfig(AuthInterceptor authInterceptor,
                           ChatMessageHandler messageHandler) {
        this.authInterceptor = authInterceptor;
        this.messageHandler = messageHandler;
    }

    /**
     * 注册WebSocket处理器
     * @param registry WebSocket处理器注册表
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/ws/chat") // 指定处理路径
                .addInterceptors(authInterceptor)       // 添加身份验证拦截器
                .setAllowedOrigins("*");                // 允许跨域
    }

    /**
     * 配置WebSocket容器参数
     */
    @Bean
    public ServletServerContainerFactoryBean webSocketContainer() {
        ServletServerContainerFactoryBean factory = new ServletServerContainerFactoryBean();
        factory.setMaxTextMessageBufferSize(8192);    // 文本消息缓冲区大小（字节）
        factory.setMaxBinaryMessageBufferSize(8192);  // 二进制消息缓冲区大小
        factory.setMaxSessionIdleTimeout(600000L);    // 会话最大空闲时间（毫秒）
       // factory.setCompressionEnabled(true);          // 启用消息压缩
        return factory;
    }
}