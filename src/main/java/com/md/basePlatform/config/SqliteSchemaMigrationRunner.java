package com.md.basePlatform.config; // Spring 配置/启动相关组件包

import org.springframework.beans.factory.annotation.Value; // 从配置文件注入属性值
import org.springframework.stereotype.Component; // 声明为通用 Spring 组件 Bean

import javax.annotation.PostConstruct; // 容器完成依赖注入后回调的方法标记
import javax.sql.DataSource; // JDBC 数据源接口
import java.sql.Connection; // JDBC 连接
import java.sql.ResultSet; // 查询结果集
import java.sql.Statement; // 静态 SQL 执行器
import java.util.HashSet; // 存放已存在列名的集合实现
import java.util.Locale; // 用于大小写不敏感比较
import java.util.Set; // 列名集合接口

@Component // 随容器启动而创建单例
public class SqliteSchemaMigrationRunner { // SQLite 启动迁移执行器

    private final DataSource dataSource; // 当前环境注入的数据源

    @Value("${spring.datasource.url:}") // 读取 spring.datasource.url，缺省为空串
    private String datasourceUrl; // 保存 JDBC URL 用于判断是否 SQLite

    public SqliteSchemaMigrationRunner(DataSource dataSource) { // 构造器只注入数据源
        this.dataSource = dataSource; // 赋值给 final 字段
    }

    @PostConstruct // Bean 属性设置完成后自动调用一次 migrate
    public void migrate() { // 执行迁移主流程
        if (datasourceUrl == null || !datasourceUrl.toLowerCase(Locale.ROOT).contains("sqlite")) { // 非 SQLite 则直接跳过
            return; // 提前结束，避免误改其他数据库
        }

        try (Connection connection = dataSource.getConnection(); // 从池或驱动获取连接，try-with-resources 自动关闭
                Statement statement = connection.createStatement()) { // 创建用于执行 DDL/DML 的语句对象
            if (!tableExists(statement, "drone")) { // 若业务表尚未创建则无需迁移
                return; // 交由 schema.sql 初始化即可
            }

            Set<String> columns = new HashSet<String>(); // 准备收集当前表已有列名（小写）
            try (ResultSet rs = statement.executeQuery("PRAGMA table_info(drone)")) { // SQLite 专用：查询表结构
                while (rs.next()) { // 遍历每一列元数据行
                    columns.add(rs.getString("name").toLowerCase(Locale.ROOT)); // 将列名统一小写后放入集合便于比对
                }
            }

            addColumnIfMissing(statement, columns, "drone_code", "TEXT"); // 若缺业务编码列则追加
            addColumnIfMissing(statement, columns, "manufacturer", "TEXT"); // 若缺厂商列则追加
            addColumnIfMissing(statement, columns, "max_payload", "INTEGER"); // 若缺最大载重则追加
            addColumnIfMissing(statement, columns, "endurance_minutes", "INTEGER"); // 若缺续航列则追加
            addColumnIfMissing(statement, columns, "cruise_speed", "INTEGER"); // 若缺速度列则追加
            addColumnIfMissing(statement, columns, "ai_generated", "INTEGER DEFAULT 0"); // 若缺 AI 标记列则追加
            addColumnIfMissing(statement, columns, "updated_at", "TEXT"); // 若缺更新时间列则追加
            addColumnIfMissing(statement, columns, "deleted", "INTEGER DEFAULT 0"); // 若缺软删标记列则追加

            statement.executeUpdate("UPDATE drone SET deleted = 0 WHERE deleted IS NULL"); // 历史数据 deleted 空值修正为 0
            statement.executeUpdate("UPDATE drone SET ai_generated = 0 WHERE ai_generated IS NULL"); // ai_generated
                                                                                                     // 空值修正
            statement.executeUpdate("UPDATE drone SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL"); // 补默认时间
            statement.executeUpdate(
                    "UPDATE drone SET drone_code = 'D' || id WHERE drone_code IS NULL OR TRIM(drone_code) = ''"); // 空编码用主键拼出临时编码
        } catch (Exception ex) { // 任意 SQL 异常包装后抛出，阻止应用在半迁移状态下继续
            throw new RuntimeException("SQLite schema migration failed: " + ex.getMessage(), ex); // 附带原因链
        }
    }

    private void addColumnIfMissing(Statement statement, Set<String> columns, String columnName, String columnType) // 按名补列
            throws Exception { // 声明向上抛出 SQLException 等检查型异常
        if (columns.contains(columnName)) { // 集合中已有该列名（小写）则跳过
            return; // 避免重复 ALTER 报错
        }
        statement.executeUpdate("ALTER TABLE drone ADD COLUMN " + columnName + " " + columnType); // 动态拼接 DDL 追加列
    }

    private boolean tableExists(Statement statement, String tableName) throws Exception { // 判断指定表是否已存在
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'"; // 查询 sqlite 系统表
        try (ResultSet rs = statement.executeQuery(sql)) { // 执行存在性查询
            return rs.next(); // 有下一行说明表存在
        }
    }
}
