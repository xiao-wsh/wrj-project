package com.md.basePlatform; // Spring Boot 主应用根包：整个项目的代码都从这个包开始组织

import org.mybatis.spring.annotation.MapperScan; // 导入 MyBatis 的 Mapper 扫描注解，自动发现 Mapper 接口
import org.springframework.boot.SpringApplication; // 导入 Spring Boot 应用启动引导类，提供 run 方法
import org.springframework.boot.autoconfigure.SpringBootApplication; // 导入组合注解，一键启用自动配置与组件扫描

/**
 * 项目启动类（整个项目的入口，最重要的类）
 * <p>
 * 作用：启动 SpringBoot 项目 <br>
 * 原理：main 方法调用 SpringApplication.run()，Spring 会：<br>
 * 1. 创建 ApplicationContext（应用上下文/容器）<br>
 * 2. 扫描当前包及子包下的所有组件（@Controller、@Service、@Component 等）<br>
 * 3. 启动内嵌的 Tomcat 服务器并监听 8081 端口 <br>
 * 4. 加载所有配置并完成依赖注入
 * </p>
 */
@SpringBootApplication // 等价于 @Configuration + @EnableAutoConfiguration + @ComponentScan（默认扫当前包及子包）
@MapperScan("com.md.basePlatform.mapper.api") // 扫描 Mapper 接口包路径，将接口注册为 Spring Bean（接口上无需再写 @Mapper）
public class BasePlatformApplication { // 可执行 jar/war 的入口类，必须包含 main 方法

    /**
     * JVM 进程入口方法
     * <p>当你执行 java -jar 或 IDE 运行此类时，JVM 先找的就是这个 main 方法</p>
     * @param args 命令行参数数组，可在启动时传入 --server.port=9090 等覆盖配置
     */
    public static void main(String[] args) { // JVM 进程入口方法签名：public static void main(String[] args)
        SpringApplication.run(BasePlatformApplication.class, args); // 启动 Spring 应用上下文：传入本类.class 作为配置源 + 命令行参数
    }
}
