-- ===================== MySQL 建表脚本 =====================
-- 作用：当激活 mysql profile 时，Spring Boot 启动自动执行此脚本创建无人机表
-- 与 SQLite 版本的区别：主键用 BIGINT AUTO_INCREMENT、字段用 INT/DATETIME、增加唯一索引

-- 创建 drone 无人机表（MySQL 语法）
CREATE TABLE IF NOT EXISTS drone ( -- IF NOT EXISTS：表不存在才创建，避免重复执行报错
    id BIGINT NOT NULL AUTO_INCREMENT, -- id：主键，MySQL 用 BIGINT（大整数），AUTO_INCREMENT 表示自增
    drone_code VARCHAR(64) NOT NULL, -- drone_code：业务编码，不可为空
    name VARCHAR(100) NOT NULL, -- name：名称，不可为空
    model VARCHAR(100) NOT NULL, -- model：型号，不可为空
    manufacturer VARCHAR(100), -- manufacturer：厂商，可为空
    max_payload INT, -- max_payload：最大载重，MySQL 用 INT 类型
    endurance_minutes INT, -- endurance_minutes：续航分钟数
    cruise_speed INT, -- cruise_speed：巡航速度
    status VARCHAR(32) DEFAULT 'READY', -- status：状态，默认 READY
    ai_generated INT DEFAULT 0, -- ai_generated：AI 标记，默认 0
    deleted INT DEFAULT 0, -- deleted：软删标记，默认 0
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- created_at：创建时间，MySQL 用 DATETIME 类型
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- updated_at：更新时间，MySQL 支持 ON UPDATE 自动更新
    PRIMARY KEY (id), -- 主键约束：指定 id 列为主键（MySQL 中 AUTO_INCREMENT 列必须是主键）
    UNIQUE KEY uk_drone_code (drone_code) -- 唯一索引：drone_code 列上创建名为 uk_drone_code 的唯一约束，防止重复编码
); -- 建表结束
