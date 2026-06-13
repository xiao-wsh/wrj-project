package com.md.basePlatform.interceptor; // 拦截器包

import org.slf4j.Logger; // SLF4J 日志接口
import org.slf4j.LoggerFactory; // 获取 Logger 工厂
import org.springframework.stereotype.Component; // Spring 组件
import org.springframework.web.servlet.HandlerInterceptor; // MVC 拦截器契约

import javax.servlet.http.HttpServletRequest; // HTTP 请求对象
import javax.servlet.http.HttpServletResponse; // HTTP 响应对象

/**
 * 需求5 专属拦截器
 * 单独放在 interceptor 独立包中
 * 功能：拦截所有网页请求，自动打印请求信息、访问耗时
 */
@Component // 由容器扫描为 Bean 并注入 WebMvcConfig
public class RequestLogInterceptor implements HandlerInterceptor { // 实现标准 MVC 拦截器接口

    private static final Logger log = LoggerFactory.getLogger(RequestLogInterceptor.class); // 绑定本类类别名日志器
    private static final String START_TIME = "request_start_time"; // 请求域属性键：开始时间戳

    @Override // 控制器执行前回调
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) { // handler 为即将执行的处理器对象
        request.setAttribute(START_TIME, System.currentTimeMillis()); // 记录进入时间点，供 afterCompletion 计算耗时

        Object traceId = request.getAttribute(AccessTraceInterceptor.TRACE_ID); // 读取前置拦截器写入的 traceId（可能为 null）
        log.info("请求开始，traceId={}, 请求方式={}, 访问地址={}, 请求参数={}", // 结构化日志模板
                traceId, request.getMethod(), request.getRequestURI(), request.getQueryString()); // 依次填充占位符

        return true; // 返回 true 表示继续过滤器链与控制器处理
    }

    @Override // 整个请求处理完成（含视图渲染）后回调
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) { // ex 可能为 null
        Object start = request.getAttribute(START_TIME); // 取出 preHandle 写入的开始时间
        long cost = start == null ? -1L : System.currentTimeMillis() - (Long) start; // 计算耗时毫秒；缺开始时间则记 -1

        Object traceId = request.getAttribute(AccessTraceInterceptor.TRACE_ID); // 再次读取 traceId 关联结束日志
        log.info("请求结束，traceId={}, 请求方式={}, 访问地址={}, 响应状态={}, 耗时毫秒={}", // 结束日志模板
                traceId, request.getMethod(), request.getRequestURI(), response.getStatus(), cost); // 带上 HTTP 状态与耗时
    }
}
