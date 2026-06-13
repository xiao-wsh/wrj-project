package com.md.basePlatform; // 与主应用同包的测试默认扫描根

import org.junit.jupiter.api.Test; // JUnit5 测试方法标记
import org.springframework.boot.test.context.SpringBootTest; // 集成测试：加载完整 Spring 上下文

/**
 * Spring Boot 集成测试：验证应用上下文能够正常启动与装配。
 */
@SpringBootTest // 使用默认配置启动 ApplicationContext
class BasePlatformApplicationTests { // 基础冒烟测试类

    @Test // 标识为一个独立测试用例
    void contextLoads() { // 方法体为空：若上下文启动失败则本用例失败
        // 无断言：Spring Boot 测试框架在启动 ApplicationContext 时已做主要校验
    }
}
