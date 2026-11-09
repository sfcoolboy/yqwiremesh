-- YQMetals Database Schema
-- Pre-create the database manually (or via your DBA tool):
--   CREATE DATABASE yqmetals CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  display_name VARCHAR(100),
  employee_number VARCHAR(50),
  work_email VARCHAR(150),
  role ENUM('ADMIN','SALES') NOT NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT NULL,
  name_en VARCHAR(100) NOT NULL,
  type VARCHAR(20) NOT NULL DEFAULT 'PRODUCT',
  sort INT NOT NULL DEFAULT 0,
  description TEXT,
  specs TEXT NULL,
  image_url VARCHAR(500),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  FOREIGN KEY (parent_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description TEXT,
  specs TEXT NULL,
  image_url VARCHAR(500),
  category_id BIGINT NOT NULL,
  sort INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS news (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(500),
  content MEDIUMTEXT,
  cover_image_url VARCHAR(500),
  source VARCHAR(200) NULL COMMENT 'News source, e.g. the customer name or the trade platform that referred the article.',
  category ENUM('COMPANY_NEWS','INDUSTRY_NEWS','EXHIBITION') NOT NULL DEFAULT 'COMPANY_NEWS',
  view_count INT NOT NULL DEFAULT 0,
  publish_date DATETIME NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inquiry (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  company VARCHAR(200),
  country VARCHAR(100),
  email VARCHAR(200) NOT NULL,
  phone VARCHAR(50),
  category_id BIGINT,
  message TEXT NOT NULL,
  status ENUM('NEW','ASSIGNED','IN_PROGRESS','CLOSED') NOT NULL DEFAULT 'NEW',
  assigned_to BIGINT,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  FOREIGN KEY (category_id) REFERENCES category(id),
  FOREIGN KEY (assigned_to) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS param (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  unit VARCHAR(20),
  enabled TINYINT(1) NOT NULL DEFAULT 1,
  assigned_category_ids TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS factory_image (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  image_url VARCHAR(500) NOT NULL,
  caption VARCHAR(200),
  sort INT NOT NULL DEFAULT 0,
  type VARCHAR(50),
  page VARCHAR(50),
  show_on_factory TINYINT(1) NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  action VARCHAR(50) NOT NULL,
  target VARCHAR(120),
  detail VARCHAR(500),
  ip VARCHAR(64),
  user_agent VARCHAR(500),
  success TINYINT(1) NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sys_setting (
  setting_key VARCHAR(100) NOT NULL PRIMARY KEY,
  setting_value TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed data -----------------------------------------------------------------

-- The default admin password is "admin123" (BCrypt).
INSERT INTO sys_user (id, username, password, display_name, role, active) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Administrator', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE username=VALUES(username), password=VALUES(password);

-- Product categories. Keep only the two active product lines.
INSERT INTO category (id, parent_id, name_en, type, sort) VALUES
(1, NULL, 'Steel Grating', 'PRODUCT', 1),
(2, NULL, 'Galvanized Steel Wire', 'PRODUCT', 2)
ON DUPLICATE KEY UPDATE name_en=VALUES(name_en), type=VALUES(type), sort=VALUES(sort);

-- News categories.
INSERT INTO category (id, parent_id, name_en, type, sort) VALUES
(14, NULL, 'Company News', 'NEWS', 1),
(9, NULL, 'Industry News', 'NEWS', 2),
(10, NULL, 'Exhibition', 'NEWS', 3)
ON DUPLICATE KEY UPDATE name_en=VALUES(name_en), type=VALUES(type), sort=VALUES(sort);

--- End of schema.sql
