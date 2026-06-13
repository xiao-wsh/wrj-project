-- ===================== MySQL 完整初始化脚本（含 RBAC 权限表） =====================
-- 作用：一键创建无人机业务表 + Shiro 权限框架所需的用户/角色/关联表
-- 前置条件：先手动创建 baseplatform 数据库：CREATE DATABASE baseplatform DEFAULT CHARSET utf8mb4;
-- 注意：MySQL 版本需要 5.7 以上（因为用了 DATETIME 的 CURRENT_TIMESTAMP 特性）

-- ===================== 1. 无人机主表 =====================
CREATE TABLE IF NOT EXISTS drone ( -- 创建无人机表
    id BIGINT PRIMARY KEY AUTO_INCREMENT, -- 主键：MySQL 用 BIGINT 自增，PRIMARY KEY 与 AUTO_INCREMENT 内联写法
    drone_code VARCHAR(64) NOT NULL UNIQUE, -- 业务编码：UNIQUE 直接跟在列定义后，等价于单独的 UNIQUE KEY
    name VARCHAR(100) NOT NULL, -- 名称
    model VARCHAR(100) NOT NULL, -- 型号
    manufacturer VARCHAR(100), -- 厂商
    max_payload INT, -- 最大载重
    endurance_minutes INT, -- 续航
    cruise_speed INT, -- 巡航速度
    status VARCHAR(32) DEFAULT 'READY', -- 状态
    ai_generated TINYINT DEFAULT 0, -- AI 标记：MySQL 用 TINYINT（占 1 字节，0~255）
    deleted TINYINT DEFAULT 0, -- 软删标记：TINYINT 比 INT 省空间
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 创建时间：DATETIME 类型
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间：ON UPDATE 表示行被修改时自动更新为当前时间
); -- 无人机表结束

-- ===================== 2. 系统用户表 =====================
CREATE TABLE IF NOT EXISTS sys_user ( -- 用户表：存储登录凭据
    id BIGINT PRIMARY KEY AUTO_INCREMENT, -- 主键
    username VARCHAR(64) NOT NULL UNIQUE, -- 登录用户名
    password VARCHAR(128) NOT NULL -- 加密后的密码（如 BCrypt 结果通常 60 字符，128 给足余量）
); -- 用户表结束

-- ===================== 3. 角色定义表 =====================
CREATE TABLE IF NOT EXISTS sys_role ( -- 角色表：定义权限角色
    id BIGINT PRIMARY KEY AUTO_INCREMENT, -- 主键
    role_code VARCHAR(64) NOT NULL UNIQUE, -- 角色编码（如 admin、user）
    role_name VARCHAR(100) NOT NULL -- 角色显示名（如 "系统管理员"）
); -- 角色表结束

-- ===================== 4. 用户-角色关联表 =====================
CREATE TABLE IF NOT EXISTS sys_user_role ( -- 用户角色关联：实现多对多关系（用户 ↔ 角色）
    id BIGINT PRIMARY KEY AUTO_INCREMENT, -- 主键
    user_id BIGINT NOT NULL, -- 用户外键：关联 sys_user.id
    role_id BIGINT NOT NULL -- 角色外键：关联 sys_role.id
); -- 关联表结束
