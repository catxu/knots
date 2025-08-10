-- 创建数据库
CREATE DATABASE IF NOT EXISTS knots_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE knots_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    open_id VARCHAR(100),
    nick_name VARCHAR(100),
    avatar_url VARCHAR(500),
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建绳结分类表
CREATE TABLE IF NOT EXISTS knot_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建绳结表
CREATE TABLE IF NOT EXISTS knots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    steps TEXT,
    cover_image VARCHAR(500),
    category_id BIGINT,
    difficulty_level INT DEFAULT 1,
    view_count INT DEFAULT 0,
    is_published BOOLEAN DEFAULT FALSE,
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES knot_categories(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 创建绳结图片表
CREATE TABLE IF NOT EXISTS knot_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    image_name VARCHAR(200),
    image_type VARCHAR(50),
    file_size BIGINT,
    sort_order INT DEFAULT 0,
    knot_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (knot_id) REFERENCES knots(id) ON DELETE CASCADE
);

-- 插入默认管理员用户
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN');

-- 插入默认分类
INSERT INTO knot_categories (name, description, sort_order) VALUES 
('基础结', '最基础的绳结类型，适合初学者', 1),
('实用结', '日常生活中常用的绳结', 2),
('装饰结', '用于装饰和美观的绳结', 3),
('专业结', '专业领域使用的绳结', 4),
('救援结', '救援和紧急情况使用的绳结', 5);

-- 插入示例绳结数据
INSERT INTO knots (name, description, steps, category_id, difficulty_level, is_published, created_by) VALUES 
('平结', '最基础的绳结，用于连接两根绳子', 
'[{"title":"步骤1","description":"将两根绳子交叉","images":[]},{"title":"步骤2","description":"将右绳从下方穿过左绳","images":[]},{"title":"步骤3","description":"将左绳从下方穿过右绳","images":[]},{"title":"步骤4","description":"拉紧完成","images":[]}]', 
1, 1, TRUE, 1),
('八字结', '防止绳子从孔洞中滑出的绳结', 
'[{"title":"步骤1","description":"将绳子对折","images":[]},{"title":"步骤2","description":"在对折处打一个环","images":[]},{"title":"步骤3","description":"将绳端从环中穿过","images":[]},{"title":"步骤4","description":"拉紧完成","images":[]}]', 
1, 2, TRUE, 1),
('双套结', '用于固定物体到柱子或杆子上的绳结', 
'[{"title":"步骤1","description":"将绳子绕柱子一圈","images":[]},{"title":"步骤2","description":"再绕一圈","images":[]},{"title":"步骤3","description":"将绳端从两圈之间穿过","images":[]},{"title":"步骤4","description":"拉紧完成","images":[]}]', 
2, 2, TRUE, 1);

-- 创建索引
CREATE INDEX idx_knots_category ON knots(category_id);
CREATE INDEX idx_knots_published ON knots(is_published);
CREATE INDEX idx_knots_views ON knots(view_count);
CREATE INDEX idx_knot_images_knot ON knot_images(knot_id);
CREATE INDEX idx_users_openid ON users(open_id);
