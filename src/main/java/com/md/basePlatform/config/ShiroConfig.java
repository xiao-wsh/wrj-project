package com.md.basePlatform.config; // 配置类包

import org.apache.shiro.spring.web.ShiroFilterFactoryBean; // Spring 集成 Shiro 过滤器工厂
import org.apache.shiro.web.mgt.DefaultWebSecurityManager; // Web 环境默认安全管理器实现
import org.springframework.boot.web.servlet.FilterRegistrationBean; // Servlet 过滤器注册辅助类
import org.springframework.context.annotation.Bean; // 声明 Spring 工厂方法 Bean
import org.springframework.context.annotation.Configuration; // 标记为配置类
import org.springframework.core.Ordered; // 过滤器顺序常量

import javax.servlet.DispatcherType; // 请求分发类型枚举
import javax.servlet.Filter; // Servlet 过滤器接口
import java.util.EnumSet; // 枚举集合工具
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration // 本类中的 @Bean 方法由容器处理
public class ShiroConfig { // Shiro 相关 Bean 定义

    @Bean // 暴露安全管理器单例
    public DefaultWebSecurityManager securityManager() { // 无自定义 Realm 的最小可用配置
        return new DefaultWebSecurityManager(); // 创建默认实现返回给容器
    }

    @Bean // 暴露 Shiro 过滤器工厂
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) { // 注入上一步的安全管理器
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean(); // 新建工厂 Bean
        bean.setSecurityManager(securityManager); // 绑定安全管理器
        bean.setLoginUrl("/login"); // 未认证时跳转登录页 URL（当前全 anon 实际不会触发）
        Map<String, String> chain = new LinkedHashMap<String, String>(); // 新建有序 URL-过滤器表达式映射
        chain.put("/login", "anon"); // 登录页匿名可访问
        chain.put("/drones/**", "anon"); // 无人机模块匿名可访问
        chain.put("/**", "anon"); // 其余全部匿名（演示用）
        bean.setFilterChainDefinitionMap(chain); // 将链定义设置进工厂
        return bean; // 返回给 Spring 容器管理生命周期
    }

    @Bean // 将 Shiro 核心过滤器注册到 Servlet 容器
    public FilterRegistrationBean<Filter> shiroFilterRegistrationBean(ShiroFilterFactoryBean shiroFilterFactoryBean) // 注入工厂
            throws Exception { // getObject 可能抛检查型异常
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(); // 新建注册描述对象
        registration.setFilter((Filter) shiroFilterFactoryBean.getObject()); // 从工厂取出真正 Filter 实例
        registration.addUrlPatterns("/*"); // 拦截所有 URL
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class)); // 对所有分发类型生效
        registration.setOrder(Ordered.LOWEST_PRECEDENCE); // 放到过滤器链较后位置，减少与 Spring MVC 冲突
        registration.setName("shiroFilter"); // 在容器中命名，便于排查
        return registration; // 返回注册 Bean，由 Boot 完成注册
    }
}
