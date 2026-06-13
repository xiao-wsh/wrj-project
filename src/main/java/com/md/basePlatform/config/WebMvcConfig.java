package com.md.basePlatform.config; // 配置包

import com.md.basePlatform.interceptor.AccessTraceInterceptor; // 访问追踪拦截器
import com.md.basePlatform.interceptor.RequestLogInterceptor; // 请求日志拦截器
import org.springframework.context.annotation.Configuration; // 配置类注解
import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // 拦截器注册表
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // MVC 扩展点接口

/**
 * Spring MVC 扩展配置：注册全局拦截器（访问追踪、请求耗时日志），静态资源路径排除拦截。
 */
@Configuration // 声明为配置组件
public class WebMvcConfig implements WebMvcConfigurer { // 实现接口以覆写 addInterceptors

    private final RequestLogInterceptor requestLogInterceptor; // 请求耗时日志拦截器依赖

    private final AccessTraceInterceptor accessTraceInterceptor; // traceId 拦截器依赖

    public WebMvcConfig(RequestLogInterceptor requestLogInterceptor, // 构造器参数一
                        AccessTraceInterceptor accessTraceInterceptor) { // 构造器参数二
        this.requestLogInterceptor = requestLogInterceptor; // 保存日志拦截器
        this.accessTraceInterceptor = accessTraceInterceptor; // 保存追踪拦截器
    }

    @Override // 实现 WebMvcConfigurer 方法
    public void addInterceptors(InterceptorRegistry registry) { // 向 Spring MVC 注册拦截器链
        registry.addInterceptor(accessTraceInterceptor) // 先注册追踪拦截器，保证后续可读到 traceId
                .addPathPatterns("/**") // 拦截所有业务路径
                .excludePathPatterns("/css/**", "/js/**", "/images/**"); // 静态资源不打日志
        registry.addInterceptor(requestLogInterceptor) // 再注册请求日志拦截器
                .addPathPatterns("/**") // 同样全局拦截业务
                .excludePathPatterns("/css/**", "/js/**", "/images/**"); // 同样排除静态目录
    }
}
