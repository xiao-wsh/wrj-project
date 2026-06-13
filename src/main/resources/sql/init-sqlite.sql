-- ===================== SQLite 完整初始化脚本（含扩展表） =====================
-- 作用：除无人机主表外，预留 Shiro 权限框架所需的用户、角色、关联表
-- 使用方法：可手动用 sqlite3 工具执行，或在代码中读取后通过 JDBC Statement 批量执行

-- ===================== 1. 无人机主表 =====================
CREATE TABLE IF NOT EXISTS drone ( -- 创建无人机表（与 schema.sql 结构完全一致）
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 主键：自增整数
    drone_code VARCHAR(64) NOT NULL UNIQUE, -- 业务编码：唯一且非空
    name VARCHAR(100) NOT NULL, -- 名称：非空
    model VARCHAR(100) NOT NULL, -- 型号：非空
    manufacturer VARCHAR(100), -- 厂商：可空
    max_payload INTEGER, -- 最大载重（kg）：可空
    endurance_minutes INTEGER, -- 续航（分钟）：可空
    cruise_speed INTEGER, -- 巡航速度（km/h）：可空
    status VARCHAR(32) DEFAULT 'READY', -- 状态：默认就绪
    ai_generated INTEGER DEFAULT 0, -- AI 标记：默认 0
    deleted INTEGER DEFAULT 0, -- 软删标记：默认 0
    created_at TEXT DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP -- 更新时间
); -- 无人机表结束

-- ===================== 2. 系统用户表（Shiro 认证用） =====================
CREATE TABLE IF NOT EXISTS sys_user ( -- 创建系统用户表：存储登录账号信息
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 主键：用户 ID
    username VARCHAR(64) NOT NULL UNIQUE, -- 用户名：登录时使用的唯一账号名
    password VARCHAR(128) NOT NULL -- 密码：存储加密后的密文（生产环境必须加密，不可存明文）
); -- 用户表结束

-- ===================== 3. 角色定义表（Shiro 授权用） =====================
CREATE TABLE IF NOT EXISTS sys_role ( -- 创建角色表：定义系统中有哪些角色（如 admin、user）
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 主键：角色 ID
    role_code VARCHAR(64) NOT NULL UNIQUE, -- 角色编码：程序内部使用的标识（如 admin），不可重复
    role_name VARCHAR(100) NOT NULL -- 角色名称：展示给人看的名称（如 "管理员"）
); -- 角色表结束

-- ===================== 4. 用户-角色关联表（多对多中间表） =====================
CREATE TABLE IF NOT EXISTS sys_user_role ( -- 创建用户角色关联表：一个用户可以有多个角色，一个角色也可以分配给多个用户
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 主键：关联记录 ID
    user_id INTEGER NOT NULL, -- 用户 ID：外键，关联 sys_user 表的 id
    role_id INTEGER NOT NULL -- 角色 ID：外键，关联 sys_role 表的 id
); -- 关联表结束
