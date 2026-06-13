package com.md.basePlatform.config; // 配置包

import org.apache.ibatis.mapping.DatabaseIdProvider; // MyBatis 多数据库标识提供者接口
import org.apache.ibatis.mapping.VendorDatabaseIdProvider; // 基于 JDBC 元数据的默认实现
import org.springframework.context.annotation.Bean; // Bean 工厂方法标记
import org.springframework.context.annotation.Configuration; // 配置类标记

import java.util.Properties; // 存放厂商名到 databaseId 的映射表

@Configuration // 声明配置类
public class MybatisDatabaseIdConfig { // MyBatis 多库识别配置

    @Bean // 向容器注册 DatabaseIdProvider Bean
    public DatabaseIdProvider databaseIdProvider() { // 无参工厂方法
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider(); // 创建基于厂商名的提供者实例
        Properties properties = new Properties(); // 新建属性表
        properties.setProperty("SQLite", "sqlite"); // JDBC 元数据为 SQLite 时映射 id 为 sqlite
        properties.setProperty("MySQL", "mysql"); // JDBC 元数据为 MySQL 时映射 id 为 mysql
        provider.setProperties(properties); // 将映射表注入提供者
        return provider; // 返回供 SqlSessionFactory 使用
    }
}
