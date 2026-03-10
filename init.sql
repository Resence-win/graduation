-- =====================================================
-- 校园一卡通系统数据库初始化脚本
-- 数据库系统：OpenGauss
-- 功能：创建校园一卡通系统所需的全部数据表
-- 作者：毕业设计
-- =====================================================


-- =====================================================
-- 1 删除已有表（防止重复执行脚本报错）
-- CASCADE 表示同时删除依赖该表的外键
-- =====================================================

DROP TABLE IF EXISTS operation_log CASCADE;
DROP TABLE IF EXISTS loss_record CASCADE;
DROP TABLE IF EXISTS recharge_record CASCADE;
DROP TABLE IF EXISTS consume_record CASCADE;
DROP TABLE IF EXISTS merchant CASCADE;
DROP TABLE IF EXISTS merchant_type CASCADE;
DROP TABLE IF EXISTS account CASCADE;
DROP TABLE IF EXISTS campus_card CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS teacher CASCADE;
DROP TABLE IF EXISTS admin_user CASCADE;


-- =====================================================
-- 2 管理员表
-- 用于系统后台管理人员登录系统
-- =====================================================

CREATE TABLE admin_user (
    id SERIAL PRIMARY KEY,                -- 管理员ID（主键，自增）
    username VARCHAR(50) UNIQUE NOT NULL, -- 登录用户名
    password VARCHAR(100) NOT NULL,       -- 登录密码
    name VARCHAR(50),                     -- 管理员姓名
    role VARCHAR(20) DEFAULT 'admin',     -- 管理员角色
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 创建时间
);

COMMENT ON TABLE admin_user IS '系统管理员表';
COMMENT ON COLUMN admin_user.id IS '管理员ID';
COMMENT ON COLUMN admin_user.username IS '登录用户名';
COMMENT ON COLUMN admin_user.password IS '登录密码';
COMMENT ON COLUMN admin_user.name IS '管理员姓名';
COMMENT ON COLUMN admin_user.role IS '管理员角色';
COMMENT ON COLUMN admin_user.create_time IS '账号创建时间';



-- =====================================================
-- 3 学生信息表
-- 存储学生的基本信息
-- =====================================================

CREATE TABLE student (
    id SERIAL PRIMARY KEY,                    -- 学生ID
    student_no VARCHAR(20) UNIQUE NOT NULL,   -- 学号
    name VARCHAR(50) NOT NULL,                -- 学生姓名
    gender VARCHAR(10),                       -- 性别
    college VARCHAR(100),                     -- 学院
    major VARCHAR(100),                       -- 专业
    class_name VARCHAR(50),                   -- 班级
    phone VARCHAR(20),                        -- 联系电话
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 创建时间
);

COMMENT ON TABLE student IS '学生信息表';
COMMENT ON COLUMN student.student_no IS '学生学号';
COMMENT ON COLUMN student.name IS '学生姓名';
COMMENT ON COLUMN student.gender IS '性别';
COMMENT ON COLUMN student.college IS '学院';
COMMENT ON COLUMN student.major IS '专业';
COMMENT ON COLUMN student.class_name IS '班级';
COMMENT ON COLUMN student.phone IS '联系电话';



-- =====================================================
-- 4 教师信息表
-- 存储教师基本信息
-- =====================================================

CREATE TABLE teacher (
    id SERIAL PRIMARY KEY,                   -- 教师ID
    teacher_no VARCHAR(20) UNIQUE NOT NULL,  -- 教工号
    name VARCHAR(50) NOT NULL,               -- 教师姓名
    college VARCHAR(100),                    -- 所属学院
    phone VARCHAR(20),                       -- 联系电话
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 创建时间
);

COMMENT ON TABLE teacher IS '教师信息表';
COMMENT ON COLUMN teacher.teacher_no IS '教师工号';
COMMENT ON COLUMN teacher.name IS '教师姓名';



-- =====================================================
-- 5 校园卡表
-- 存储所有校园卡信息
-- =====================================================

CREATE TABLE campus_card (
    id SERIAL PRIMARY KEY,               -- 校园卡ID
    card_no VARCHAR(30) UNIQUE NOT NULL, -- 校园卡卡号
    owner_type VARCHAR(20) NOT NULL,     -- 持卡人类型（student / teacher）
    owner_id INT NOT NULL,               -- 持卡人ID（对应学生或教师ID）
    status VARCHAR(20) DEFAULT 'normal', -- 卡状态（normal正常 / lost挂失 / disabled停用）
    issue_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 发卡时间
);

COMMENT ON TABLE campus_card IS '校园卡信息表';
COMMENT ON COLUMN campus_card.card_no IS '校园卡卡号';
COMMENT ON COLUMN campus_card.owner_type IS '持卡人类型';
COMMENT ON COLUMN campus_card.owner_id IS '持卡人ID';
COMMENT ON COLUMN campus_card.status IS '卡状态';



-- =====================================================
-- 6 账户表
-- 每张校园卡对应一个账户余额
-- =====================================================

CREATE TABLE account (
    id SERIAL PRIMARY KEY,      -- 账户ID
    card_id INT UNIQUE NOT NULL,-- 对应校园卡ID
    balance NUMERIC(10,2) DEFAULT 0, -- 当前余额
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 更新时间

    CONSTRAINT fk_account_card
        FOREIGN KEY (card_id)
        REFERENCES campus_card(id)
        ON DELETE CASCADE
);

COMMENT ON TABLE account IS '校园卡账户余额表';
COMMENT ON COLUMN account.balance IS '账户余额';



-- =====================================================
-- 7 商户类型表
-- 例如：食堂、超市、打印店等
-- =====================================================

CREATE TABLE merchant_type (
    id SERIAL PRIMARY KEY,      -- 商户类型ID
    type_name VARCHAR(50) NOT NULL, -- 类型名称
    description VARCHAR(200)    -- 类型描述
);

COMMENT ON TABLE merchant_type IS '商户类型表';



-- =====================================================
-- 8 商户表
-- 校园内消费商家
-- =====================================================

CREATE TABLE merchant (
    id SERIAL PRIMARY KEY,      -- 商户ID
    name VARCHAR(100) NOT NULL, -- 商户名称
    type_id INT,                -- 商户类型ID
    location VARCHAR(200),      -- 商户位置
    logo VARCHAR(255),          -- 商户Logo
    status VARCHAR(20) DEFAULT 'open', -- 营业状态
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_merchant_type
        FOREIGN KEY (type_id)
        REFERENCES merchant_type(id)
);

COMMENT ON TABLE merchant IS '校园商户表';



-- =====================================================
-- 9 消费记录表
-- 记录校园卡消费流水
-- =====================================================

CREATE TABLE consume_record (
    id SERIAL PRIMARY KEY,     -- 消费记录ID
    card_id INT NOT NULL,      -- 校园卡ID
    merchant_id INT NOT NULL,  -- 商户ID
    amount NUMERIC(10,2) NOT NULL, -- 消费金额
    balance_after NUMERIC(10,2),   -- 消费后余额
    consume_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 消费时间

    CONSTRAINT fk_consume_card
        FOREIGN KEY (card_id)
        REFERENCES campus_card(id),

    CONSTRAINT fk_consume_merchant
        FOREIGN KEY (merchant_id)
        REFERENCES merchant(id)
);

COMMENT ON TABLE consume_record IS '消费记录表';



-- =====================================================
-- 10 充值记录表
-- 记录用户充值记录
-- =====================================================

CREATE TABLE recharge_record (
    id SERIAL PRIMARY KEY,      -- 充值记录ID
    card_id INT NOT NULL,       -- 校园卡ID
    amount NUMERIC(10,2) NOT NULL, -- 充值金额
    recharge_type VARCHAR(20),  -- 充值方式（cash / online）
    operator_id INT,            -- 操作员ID
    recharge_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recharge_card
        FOREIGN KEY (card_id)
        REFERENCES campus_card(id)
);

COMMENT ON TABLE recharge_record IS '充值记录表';



-- =====================================================
-- 11 挂失记录表
-- 校园卡挂失与恢复记录
-- =====================================================

CREATE TABLE loss_record (
    id SERIAL PRIMARY KEY,   -- 挂失记录ID
    card_id INT NOT NULL,    -- 校园卡ID
    loss_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 挂失时间
    recover_time TIMESTAMP,  -- 解挂时间
    status VARCHAR(20) DEFAULT 'lost', -- 状态

    CONSTRAINT fk_loss_card
        FOREIGN KEY (card_id)
        REFERENCES campus_card(id)
);

COMMENT ON TABLE loss_record IS '校园卡挂失记录表';



-- =====================================================
-- 12 系统操作日志表
-- 记录管理员操作日志
-- =====================================================

CREATE TABLE operation_log (
    id SERIAL PRIMARY KEY,     -- 日志ID
    operator_id INT,           -- 操作人ID
    operator_type VARCHAR(20), -- 操作人类型（admin/merchant）
    operation VARCHAR(200),    -- 操作内容
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE operation_log IS '系统操作日志表';



-- =====================================================
-- 13 索引优化
-- 提高查询效率
-- =====================================================

CREATE INDEX idx_card_no ON campus_card(card_no);
CREATE INDEX idx_student_no ON student(student_no);
CREATE INDEX idx_teacher_no ON teacher(teacher_no);
CREATE INDEX idx_consume_card ON consume_record(card_id);
CREATE INDEX idx_consume_time ON consume_record(consume_time);
CREATE INDEX idx_recharge_card ON recharge_record(card_id);



-- =====================================================
-- 14 初始化测试数据
-- =====================================================

INSERT INTO admin_user (username, password, name)
VALUES ('admin', '123456', '系统管理员');

INSERT INTO merchant_type (type_name, description)
VALUES
('食堂', '校园食堂'),
('超市', '校园超市'),
('打印店', '打印复印服务');