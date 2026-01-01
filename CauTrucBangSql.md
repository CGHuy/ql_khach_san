CREATE DATABASE IF NOT EXISTS db_ql_khach_san
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE db_ql_khach_san;

-- 1. LOẠI PHÒNG
CREATE TABLE room_type (
type_id INT PRIMARY KEY AUTO_INCREMENT,
type_name VARCHAR(50) NOT NULL,
price DECIMAL(12,2) NOT NULL,
description VARCHAR(255)
);

-- 2. PHÒNG
CREATE TABLE room (
room_id INT PRIMARY KEY AUTO_INCREMENT,
room_number VARCHAR(10) NOT NULL UNIQUE,
type_id INT NOT NULL,
status ENUM('Trống', 'Đã đặt', 'Đã thuê', 'Đang dọn') DEFAULT 'Trống',

    FOREIGN KEY (type_id) REFERENCES room_type(type_id)

);

-- 3. KHÁCH HÀNG
CREATE TABLE customer (
customer_id INT PRIMARY KEY AUTO_INCREMENT,
full_name VARCHAR(100) NOT NULL,
phone VARCHAR(20) UNIQUE,
cccd VARCHAR(20) UNIQUE, -- Thêm UNIQUE cho CCCD/ID
address VARCHAR(255)
);

-- 4. NHÂN VIÊN
CREATE TABLE employee (
employee_id INT PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(50) UNIQUE,
password VARCHAR(255), -- Dùng cho Hashing
full_name VARCHAR(100),
role VARCHAR(20) DEFAULT 'Nhân viên' -- Nhân viên, Quản lý
);

-- 5. ĐẶT PHÒNG
CREATE TABLE reservation (
reservation_id INT PRIMARY KEY AUTO_INCREMENT,
customer_id INT,
room_id INT,
booking_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- Thời gian đặt
checkin_date DATETIME, -- Dự kiến
checkout_date DATETIME, -- Dự kiến
status ENUM('Đã đặt', 'Đã hủy', 'Đã nhận phòng') DEFAULT 'Đã đặt',

    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id)

);

-- 6. LẦN LƯU TRÚ/NHẬN PHÒNG (CHECK-IN)
CREATE TABLE checkin (
checkin_id INT PRIMARY KEY AUTO_INCREMENT,
reservation_id INT UNIQUE, -- Chỉ 1 lần lưu trú cho 1 lần đặt
checkin_time DATETIME NOT NULL, -- Thời gian nhận phòng thực tế
checkout_time DATETIME, -- Thời gian trả phòng thực tế (NULL khi chưa check-out)

    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id)

);

-- 7. DỊCH VỤ
CREATE TABLE service (
service_id INT PRIMARY KEY AUTO_INCREMENT,
service_name VARCHAR(100) NOT NULL,
price DECIMAL(12,2)
);

-- 8. SỬ DỤNG DỊCH VỤ
CREATE TABLE service_usage (
usage_id INT PRIMARY KEY AUTO_INCREMENT,
checkin_id INT,
service_id INT,
quantity INT DEFAULT 1,
created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (checkin_id) REFERENCES checkin(checkin_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id)

);

-- 9. HÓA ĐƠN
CREATE TABLE invoice (
invoice_id INT PRIMARY KEY AUTO_INCREMENT,
checkin_id INT UNIQUE,
employee_id INT,
room_fee DECIMAL(12,2) NOT NULL,
service_fee DECIMAL(12,2) NOT NULL,
total_amount DECIMAL(12,2),
created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (checkin_id) REFERENCES checkin(checkin_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)

);

-- 10. THỐNG KÊ
CREATE TABLE statistic (
statistic_id INT PRIMARY KEY AUTO_INCREMENT,
stat_date DATE NOT NULL,
stat_period VARCHAR(20) NOT NULL, -- 'day', 'month', 'year'
revenue DECIMAL(18,2) DEFAULT 0,
room_revenue DECIMAL(18,2) DEFAULT 0,
service_revenue DECIMAL(18,2) DEFAULT 0,
customer_count INT DEFAULT 0,
room_rented_count INT DEFAULT 0,
service_count INT DEFAULT 0,
note VARCHAR(255),
created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
