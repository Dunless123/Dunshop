package com.example.schoolmarket.aspect;

import com.example.schoolmarket.entity.OperationLog;
import com.example.schoolmarket.service.OperationLogService;
import com.example.schoolmarket.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.example.schoolmarket.controller..*.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        if (request == null) {
            return joinPoint.proceed();
        }
        
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        if ("GET".equalsIgnoreCase(method)) {
            return joinPoint.proceed();
        }
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        String username = "系统用户";
        
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = jwtUtil.parseToken(token);
                if (claims != null) {
                    Object usernameObj = claims.get("username");
                    if (usernameObj != null && !usernameObj.toString().isEmpty()) {
                        username = usernameObj.toString();
                    } else {
                        String subject = claims.getSubject();
                        if (subject != null && !subject.isEmpty()) {
                            try {
                                Long.parseLong(subject);
                            } catch (NumberFormatException e) {
                                username = subject;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        
        if ("POST".equalsIgnoreCase(method) && uri.endsWith("/login")) {
            try {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String[] paramNames = signature.getParameterNames();
                Object[] paramValues = joinPoint.getArgs();
                
                for (int i = 0; i < paramNames.length; i++) {
                    if ("params".equals(paramNames[i]) && paramValues[i] != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> paramMap = (Map<String, Object>) paramValues[i];
                        Object loginUsername = paramMap.get("username");
                        if (loginUsername != null) {
                            username = loginUsername.toString();
                        } else {
                            Object loginStudentId = paramMap.get("studentId");
                            if (loginStudentId != null) {
                                username = loginStudentId.toString();
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
        
        Object result = joinPoint.proceed();
        
        try {
            OperationLog log = new OperationLog();
            log.setUsername(username);
            log.setType(getOperationType(method));
            log.setContent("路径: " + uri + " | 方法: " + className + "." + methodName);
            log.setIp(getClientIp(request));
            log.setCreateTime(LocalDateTime.now());
            
            operationLogService.save(log);
        } catch (Exception e) {
            logger.warn("Failed to save operation log", e);
        }
        
        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    private String getOperationType(String method) {
        return switch (method.toUpperCase()) {
            case "POST" -> "新增";
            case "PUT" -> "修改";
            case "DELETE" -> "删除";
            default -> "其他";
        };
    }
}