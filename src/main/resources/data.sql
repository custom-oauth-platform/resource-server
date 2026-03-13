-- DB 생성
CREATE DATABASE IF NOT EXISTS oauth_resource_db;
USE oauth_resource_db;

-- 테이블 생성
CREATE TABLE resource_member_profile (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         user_id VARCHAR(50) UNIQUE NOT NULL,
                                         name VARCHAR(100) NOT NULL,
                                         email VARCHAR(100),
                                         gender ENUM('MALE', 'FEMALE', 'OTHER'),
                                         birthdate DATE
);

-- 데이터 삽입 (auth_member 테이블의 user_id와 1:1 매칭)
INSERT INTO resource_member_profile (user_id, name, email, gender, birthdate) VALUES
                                                                                  ('yeong', '민영', 'yeong@example.com', 'FEMALE', '2003-07-24'),
                                                                                  ('ironman', '토니스타크', 'tony@stark.com', 'MALE', '1970-05-29'),
                                                                                  ('spidey', '피터파커', 'peter@stark.com', 'MALE', '2001-08-10');