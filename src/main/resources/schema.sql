-- ===================== SQLite 建表脚本 =====================
-- 作用：Spring Boot 启动时自动执行，创建无人机主表（IF NOT EXISTS 保证幂等：重复执行不报错）
-- 执行时机：application-sqlite.yml 中配置了 initialization-mode: always + schema: classpath:schema.sql

-- 创建 drone 无人机表（如果表已存在则跳过，不会覆盖数据）
CREATE TABLE IF NOT EXISTS drone ( -- CREATE TABLE: 建表语句；IF NOT EXISTS: 幂等保护
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- id：主键，INTEGER 类型，自增（SQLite 中 INTEGER PRIMARY KEY 自动成为 rowid 别名）
    drone_code VARCHAR(64) NOT NULL UNIQUE, -- drone_code：业务编码，最长 64 字符，不能为空且全局唯一
    name VARCHAR(100) NOT NULL, -- name：无人机名称，最长 100 字符，不能为空
    model VARCHAR(100) NOT NULL, -- model：型号，最长 100 字符，不能为空
    manufacturer VARCHAR(100), -- manufacturer：生产厂商，最长 100 字符，可为空
    max_payload INTEGER, -- max_payload：最大载重（单位 kg），INTEGER 类型，可为空表示未填写
    endurance_minutes INTEGER, -- endurance_minutes：续航时间（单位分钟），可为空
    cruise_speed INTEGER, -- cruise_speed：巡航速度（单位 km/h），可为空
    status VARCHAR(32) DEFAULT 'READY', -- status：业务状态，默认值 'READY'（就绪状态）
    ai_generated INTEGER DEFAULT 0, -- ai_generated：AI 生成标记，1 表示由 AI 填参，0 表示手动填写，默认 0
    deleted INTEGER DEFAULT 0, -- deleted：软删除标记，1 表示已删除，0 表示正常，默认 0
    created_at TEXT DEFAULT CURRENT_TIMESTAMP, -- created_at：创建时间，TEXT 类型存时间字符串，默认当前时间戳
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP -- updated_at：更新时间，TEXT 类型，默认当前时间戳
); -- 建表语句结束分号
