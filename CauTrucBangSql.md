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

-- 2. TẦNG
CREATE TABLE floor (
floor_id INT PRIMARY KEY AUTO_INCREMENT,
floor_number INT NOT NULL UNIQUE,
description VARCHAR(255)
);

-- 3. PHÒNG
CREATE TABLE room (
room_id INT PRIMARY KEY AUTO_INCREMENT,
room_number VARCHAR(10) NOT NULL UNIQUE,
type_id INT NOT NULL,
floor_id INT NOT NULL,
status ENUM('Trống', 'Đã đặt', 'Đã thuê', 'Đang dọn') DEFAULT 'Trống',

    FOREIGN KEY (type_id) REFERENCES room_type(type_id),
    FOREIGN KEY (floor_id) REFERENCES floor(floor_id)

);

-- 4. KHÁCH HÀNG
CREATE TABLE customer (
customer_id INT PRIMARY KEY AUTO_INCREMENT,
full_name VARCHAR(100) NOT NULL,
phone VARCHAR(20) UNIQUE,
cccd VARCHAR(20) UNIQUE, -- Thêm UNIQUE cho CCCD/ID
address VARCHAR(255)
);

-- 5. NHÂN VIÊN
CREATE TABLE employee (
employee_id INT PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(50) UNIQUE,
password VARCHAR(255), -- Dùng cho Hashing
full_name VARCHAR(100),
role VARCHAR(20) DEFAULT 'Nhân viên' -- Nhân viên, Quản lý
);

-- 6. ĐẶT PHÒNG
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

-- 7. LẦN LƯU TRÚ/NHẬN PHÒNG (CHECK-IN)
CREATE TABLE checkin (
checkin_id INT PRIMARY KEY AUTO_INCREMENT,
reservation_id INT UNIQUE, -- Chỉ 1 lần lưu trú cho 1 lần đặt
checkin_time DATETIME NOT NULL, -- Thời gian nhận phòng thực tế
checkout_time DATETIME, -- Thời gian trả phòng thực tế (NULL khi chưa check-out)

    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id)

);

-- 8. DỊCH VỤ
CREATE TABLE service (
service_id INT PRIMARY KEY AUTO_INCREMENT,
service_name VARCHAR(100) NOT NULL,
price DECIMAL(12,2)
);

-- 9. SỬ DỤNG DỊCH VỤ
CREATE TABLE service_usage (
usage_id INT PRIMARY KEY AUTO_INCREMENT,
checkin_id INT,
service_id INT,
quantity INT DEFAULT 1,
created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (checkin_id) REFERENCES checkin(checkin_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id)

);

-- 10. HÓA ĐƠN
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
-- =========================
-- 1. LOẠI PHÒNG
-- =========================
INSERT INTO room_type (type_name, price, description) VALUES
('Phòng đơn', 300000, '1 giường đơn'),
('Phòng đôi', 500000, '2 giường đơn'),
('Phòng gia đình', 700000, '3–4 người'),
('Phòng VIP', 1000000, 'Cao cấp'),
('Suite', 1500000, 'Hạng sang'),
('Dorm', 200000, 'Phòng tập thể');

-- =========================
-- 2. TẦNG
-- =========================
INSERT INTO floor (floor_number, description) VALUES
(1, 'Tầng lễ tân'),
(2, 'Tầng tiêu chuẩn'),
(3, 'Tầng tiêu chuẩn'),
(4, 'Tầng cao cấp'),
(5, 'Tầng VIP');

-- =========================
-- 3. PHÒNG
-- =========================
INSERT INTO room (room_number, type_id, floor_id, status) VALUES
('101', 1, 1, 'Trống'),
('102', 1, 1, 'Trống'),
('103', 6, 1, 'Đang dọn'),
('104', 6, 1, 'Trống'),

('201', 2, 2, 'Trống'),
('202', 2, 2, 'Đã đặt'),
('203', 3, 2, 'Trống'),
('204', 1, 2, 'Đã thuê'),
('205', 1, 2, 'Trống'),

('301', 2, 3, 'Trống'),
('302', 3, 3, 'Trống'),
('303', 3, 3, 'Đã thuê'),
('304', 2, 3, 'Đã đặt'),

('401', 4, 4, 'Trống'),
('402', 4, 4, 'Đã đặt'),
('403', 5, 4, 'Trống'),
('404', 4, 4, 'Trống'),

('501', 5, 5, 'Đã thuê'),
('502', 4, 5, 'Trống'),
('503', 5, 5, 'Trống');

-- =========================
-- 4. KHÁCH HÀNG
-- =========================
INSERT INTO customer (full_name, phone, cccd, address) VALUES
('Nguyễn Văn An', '0901000001', '001001001001', 'Hà Nội'),
('Trần Thị Bình', '0901000002', '001001001002', 'TP HCM'),
('Lê Văn Công', '0901000003', '001001001003', 'Đà Nẵng'),
('Phạm Thị D', '0901000004', '001001001004', 'Hải Phòng'),
('Hoàng Văn E', '0901000005', '001001001005', 'Cần Thơ'),
('Đỗ Thị F', '0901000006', '001001001006', 'Bình Dương'),
('Vũ Văn G', '0901000007', '001001001007', 'Nha Trang'),
('Bùi Thị H', '0901000008', '001001001008', 'Huế'),
('Ngô Văn I', '0901000009', '001001001009', 'Quảng Ninh'),
('Phan Thị K', '0901000010', '001001001010', 'Vũng Tàu');

-- =========================
-- 5. NHÂN VIÊN
-- =========================
INSERT INTO employee (username, password, full_name, role) VALUES
('admin', '123456', 'Quản lý khách sạn', 'Quản lý'),
('lt01', '123456', 'Lễ tân ca sáng', 'Nhân viên'),
('lt02', '123456', 'Lễ tân ca chiều', 'Nhân viên'),
('kt01', '123456', 'Kế toán', 'Nhân viên'),
('bv01', '123456', 'Bảo vệ', 'Nhân viên');

-- =========================
-- 6. ĐẶT PHÒNG
-- =========================
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status) VALUES
-- ===== GIỮA NĂM 2025 (06–07) =====
(1, 1, '2025-06-02 14:00', '2025-06-04 12:00', 'Đã nhận phòng'),
(2, 2, '2025-06-05 14:00', '2025-06-07 12:00', 'Đã nhận phòng'),
(3, 3, '2025-06-08 14:00', '2025-06-10 12:00', 'Đã hủy'),
(4, 4, '2025-06-12 14:00', '2025-06-14 12:00', 'Đã nhận phòng'),
(5, 5, '2025-06-15 14:00', '2025-06-18 12:00', 'Đã đặt'),
(6, 6, '2025-06-20 14:00', '2025-06-22 12:00', 'Đã nhận phòng'),
(7, 7, '2025-06-25 14:00', '2025-06-27 12:00', 'Đã hủy'),
(8, 8, '2025-07-01 14:00', '2025-07-03 12:00', 'Đã nhận phòng'),
(9, 9, '2025-07-05 14:00', '2025-07-08 12:00', 'Đã đặt'),
(10, 10, '2025-07-10 14:00', '2025-07-12 12:00', 'Đã nhận phòng'),

-- ===== CUỐI NĂM 2025 (08–12) =====
(1, 11, '2025-08-02 14:00', '2025-08-04 12:00', 'Đã nhận phòng'),
(2, 12, '2025-08-08 14:00', '2025-08-10 12:00', 'Đã đặt'),
(3, 13, '2025-08-15 14:00', '2025-08-18 12:00', 'Đã nhận phòng'),
(4, 14, '2025-09-01 14:00', '2025-09-03 12:00', 'Đã hủy'),
(5, 15, '2025-09-10 14:00', '2025-09-13 12:00', 'Đã nhận phòng'),
(6, 16, '2025-10-05 14:00', '2025-10-07 12:00', 'Đã nhận phòng'),
(7, 17, '2025-10-12 14:00', '2025-10-14 12:00', 'Đã đặt'),
(8, 18, '2025-11-01 14:00', '2025-11-03 12:00', 'Đã nhận phòng'),
(9, 19, '2025-11-10 14:00', '2025-11-12 12:00', 'Đã hủy'),
(10, 20, '2025-11-20 14:00', '2025-11-23 12:00', 'Đã nhận phòng'),

-- ===== THÁNG 12 NĂM 2025 =====
(1, 1, '2025-12-02 14:00', '2025-12-05 12:00', 'Đã nhận phòng'),
(2, 2, '2025-12-08 14:00', '2025-12-10 12:00', 'Đã đặt'),
(3, 3, '2025-12-15 14:00', '2025-12-18 12:00', 'Đã nhận phòng'),
(4, 4, '2025-12-20 14:00', '2025-12-22 12:00', 'Đã hủy'),

-- ===== ĐẦU NĂM 2026 (01) =====
(5, 5, '2026-01-03 14:00', '2026-01-05 12:00', 'Đã nhận phòng'),
(6, 6, '2026-01-07 14:00', '2026-01-09 12:00', 'Đã đặt'),
(7, 7, '2026-01-12 14:00', '2026-01-15 12:00', 'Đã nhận phòng'),
(8, 8, '2026-01-18 14:00', '2026-01-20 12:00', 'Đã hủy'),
(9, 9, '2026-01-22 14:00', '2026-01-25 12:00', 'Đã nhận phòng'),
(10, 10, '2026-01-27 14:00', '2026-01-29 12:00', 'Đã nhận phòng');

-- =========================
-- 7. LẦN LƯU TRÚ/NHẬN PHÒNG (CHECK-IN)
-- =========================
INSERT INTO checkin (reservation_id, checkin_time, checkout_time) VALUES
-- Chỉ các đơn đặt phòng với trạng thái 'Đã nhận phòng'
(1, '2025-06-02 14:10', '2025-06-04 11:50'),
(2, '2025-06-05 14:05', '2025-06-07 11:55'),
(4, '2025-06-12 14:15', '2025-06-14 11:45'),
(6, '2025-06-20 14:00', '2025-06-22 11:50'),
(8, '2025-07-01 14:10', '2025-07-03 11:55'),
(10, '2025-07-10 14:05', '2025-07-12 11:50'),
(11, '2025-08-02 14:00', '2025-08-04 11:55'),
(13, '2025-08-15 14:10', '2025-08-18 11:50'),
(15, '2025-09-10 14:05', '2025-09-13 11:45'),
(16, '2025-10-05 14:00', '2025-10-07 11:50'),
(18, '2025-11-01 14:10', '2025-11-03 11:55'),
(20, '2025-11-20 14:05', '2025-11-23 11:50'),
(21, '2025-12-02 14:10', '2025-12-05 11:55'),
(23, '2025-12-15 14:05', '2025-12-18 11:50'),
(25, '2026-01-03 14:10', '2026-01-05 11:55'),
(27, '2026-01-12 14:05', '2026-01-15 11:50'),
(29, '2026-01-22 14:10', '2026-01-25 11:55'),
(30, '2026-01-27 14:10', '2026-01-29 11:55');

-- =========================
-- 8. DỊCH VỤ
-- =========================
INSERT INTO service (service_name, price) VALUES
('Ăn sáng', 100000),
('Giặt ủi', 50000),
('Thuê xe máy', 150000),
('Spa', 300000),
('Đưa đón sân bay', 400000),
('Mini bar', 80000);

-- =========================
-- 9. SỬ DỤNG DỊCH VỤ
-- =========================
INSERT INTO service_usage (checkin_id, service_id, quantity) VALUES
-- checkin_id 1: Ăn sáng 2 lần, Giặt ủi 1 lần
(1, 1, 2),
(1, 2, 1),
-- checkin_id 2: Thuê xe máy 1, Mini bar 2
(2, 3, 1),
(2, 6, 2),
-- checkin_id 3 (reservation 4): Ăn sáng 2 lần, Đưa đón sân bay 1 lần
(3, 1, 2),
(3, 5, 1),
-- checkin_id 4 (reservation 6): Mini bar 3 lần
(4, 6, 3),
-- checkin_id 5 (reservation 8): Giặt ủi 2 lần, Ăn sáng 2 lần
(5, 2, 2),
(5, 1, 2),
-- checkin_id 6 (reservation 10): Spa 1 lần
(6, 4, 1),
-- checkin_id 7 (reservation 11): Ăn sáng 2 lần, Thuê xe máy 1 lần
(7, 1, 2),
(7, 3, 1),
-- checkin_id 8 (reservation 13): Mini bar 2 lần
(8, 6, 2),
-- checkin_id 9 (reservation 15): Đưa đón sân bay 1 lần
(9, 5, 1),
-- checkin_id 10 (reservation 16): Giặt ủi 2 lần, Ăn sáng 2 lần
(10, 2, 2),
(10, 1, 2),
-- checkin_id 11 (reservation 18): Ăn sáng 2 lần
(11, 1, 2),
-- checkin_id 12 (reservation 20): Spa 1 lần, Mini bar 2 lần
(12, 4, 1),
(12, 6, 2),
-- checkin_id 13 (reservation 21): Giặt ủi 1 lần, Ăn sáng 2 lần
(13, 2, 1),
(13, 1, 2),
-- checkin_id 14 (reservation 23): Thuê xe máy 1 lần
(14, 3, 1),
-- checkin_id 15 (reservation 25): Ăn sáng 2 lần
(15, 1, 2),
-- checkin_id 16 (reservation 27): Spa 1 lần
(16, 4, 1),
-- checkin_id 17 (reservation 29): Giặt ủi 2 lần, Mini bar 1 lần
(17, 2, 2),
(17, 6, 1),
-- checkin_id 18 (reservation 30): Ăn sáng 2 lần, Đưa đón sân bay 1 lần
(18, 1, 2),
(18, 5, 1);

-- =========================
-- 10. HÓA ĐƠN
-- =========================
-- Tính toán: room_fee = giá phòng × số đêm, service_fee = tổng chi phí dịch vụ
INSERT INTO invoice (
checkin_id,
employee_id,
room_fee,
service_fee,
total_amount,
created_at
) VALUES
-- checkin 1: Room 1 (type 1 - 300k) × 2 đêm + service (100k×2 + 50k×1)
(1, 2, 600000, 250000, 850000, '2025-06-04 12:10'),
-- checkin 2: Room 2 (type 2 - 500k) × 2 đêm + service (150k×1 + 80k×2)
(2, 3, 1000000, 310000, 1310000, '2025-06-07 12:15'),
-- checkin 3: Room 4 (type 6 - 200k) × 2 đêm + service (100k×2 + 400k×1)
(3, 2, 400000, 600000, 1000000, '2025-06-14 12:10'),
-- checkin 4: Room 6 (type 2 - 500k) × 2 đêm + service (80k×3)
(4, 4, 1000000, 240000, 1240000, '2025-06-22 12:05'),
-- checkin 5: Room 8 (type 1 - 300k) × 2 đêm + service (50k×2 + 100k×2)
(5, 3, 600000, 300000, 900000, '2025-07-03 12:10'),
-- checkin 6: Room 10 (type 6 - 200k) × 2 đêm + service (300k×1)
(6, 2, 400000, 300000, 700000, '2025-07-12 12:05'),
-- checkin 7: Room 11 (type 2 - 500k) × 2 đêm + service (100k×2 + 150k×1)
(7, 3, 1000000, 350000, 1350000, '2025-08-04 12:10'),
-- checkin 8: Room 13 (type 3 - 700k) × 3 đêm + service (80k×2)
(8, 2, 2100000, 160000, 2260000, '2025-08-18 12:15'),
-- checkin 9: Room 15 (type 2 - 500k) × 3 đêm + service (400k×1)
(9, 4, 1500000, 400000, 1900000, '2025-09-13 12:10'),
-- checkin 10: Room 16 (type 4 - 1000k) × 2 đêm + service (50k×2 + 100k×2)
(10, 3, 2000000, 300000, 2300000, '2025-10-07 12:05'),
-- checkin 11: Room 18 (type 1 - 300k) × 2 đêm + service (100k×2)
(11, 2, 600000, 200000, 800000, '2025-11-03 12:10'),
-- checkin 12: Room 20 (type 3 - 700k) × 3 đêm + service (300k×1 + 80k×2)
(12, 4, 2100000, 460000, 2560000, '2025-11-23 12:15'),
-- checkin 13: Room 1 (type 1 - 300k) × 3 đêm + service (50k×1 + 100k×2)
(13, 3, 900000, 250000, 1150000, '2025-12-05 12:10'),
-- checkin 14: Room 3 (type 6 - 200k) × 3 đêm + service (150k×1)
(14, 2, 600000, 150000, 750000, '2025-12-18 12:05'),
-- checkin 15: Room 5 (type 2 - 500k) × 2 đêm + service (100k×2)
(15, 3, 1000000, 200000, 1200000, '2026-01-05 12:10'),
-- checkin 16: Room 7 (type 3 - 700k) × 3 đêm + service (300k×1)
(16, 2, 2100000, 300000, 2400000, '2026-01-15 12:15'),
-- checkin 17: Room 9 (type 3 - 700k) × 3 đêm + service (50k×2 + 80k×1)
(17, 4, 2100000, 180000, 2280000, '2026-01-25 12:10'),
-- checkin 18: Room 10 (type 6 - 200k) × 2 đêm + service (100k×2 + 400k×1)
(18, 3, 400000, 600000, 1000000, '2026-01-29 12:10');
