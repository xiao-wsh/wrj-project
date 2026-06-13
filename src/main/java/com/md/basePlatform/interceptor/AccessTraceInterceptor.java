package com.md.basePlatform.interceptor; // 拦截器包

import org.slf4j.Logger; // 日志接口
import org.slf4j.LoggerFactory; // 日志工厂
import org.springframework.stereotype.Component; // Spring 组件
import org.springframework.web.servlet.HandlerInterceptor; // MVC 拦截器接口

import javax.servlet.http.HttpServletRequest; // 请求
import javax.servlet.http.HttpServletResponse; // 响应
import java.util.UUID; // 生成全局唯一 traceId

/**
 * 辅助拦截器：为请求生成 traceId，便于与 {@link RequestLogInterceptor} 的日志关联。
 */
@Component // 注册为 Bean
public class AccessTraceInterceptor implements HandlerInterceptor { // 实现拦截器

    private static final Logger log = LoggerFactory.getLogger(AccessTraceInterceptor.class); // 本类日志器
    public static final String TRACE_ID = "REQUEST_TRACE_ID"; // 请求属性中存放 traceId 的键名（与日志拦截器共享）

    @Override // 请求进入控制器前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) { // handler 当前可忽略
        String traceId = UUID.randomUUID().toString().replace("-", ""); // 生成无连字符 UUID 作为追踪号
        request.setAttribute(TRACE_ID, traceId); // 写入 request 作用域，后续拦截器与业务可读
        log.info("traceId={}, clientIp={}, uri={}", traceId, request.getRemoteAddr(), request.getRequestURI()); // 打一条入口追踪日志
        return true; // 放行继续处理
    }
}
