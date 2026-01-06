-- =============================================
-- DATABASE: QUẢN LÝ KHÁCH SẠN
-- Chuẩn MySQL - Import vào phpMyAdmin/XAMPP
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa database cũ nếu tồn tại
DROP DATABASE IF EXISTS db_ql_khach_san;

-- Tạo database mới
CREATE DATABASE db_ql_khach_san
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE db_ql_khach_san;

-- =============================================
-- 1. BẢNG LOẠI PHÒNG (room_type)
-- =============================================
DROP TABLE IF EXISTS `room_type`;
CREATE TABLE `room_type` (
    `type_id` INT PRIMARY KEY AUTO_INCREMENT,
    `type_name` VARCHAR(50) NOT NULL,
    `price` DECIMAL(12,2) NOT NULL,
    `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 2. BẢNG TẦNG (floor)
-- =============================================
DROP TABLE IF EXISTS `floor`;
CREATE TABLE `floor` (
    `floor_id` INT PRIMARY KEY AUTO_INCREMENT,
    `floor_number` INT NOT NULL UNIQUE,
    `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 3. BẢNG PHÒNG (room)
-- =============================================
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
    `room_id` INT PRIMARY KEY AUTO_INCREMENT,
    `room_number` VARCHAR(10) NOT NULL UNIQUE,
    `type_id` INT NOT NULL,
    `floor_id` INT NOT NULL,
    `status` ENUM('Trống', 'Đã đặt', 'Đã thuê', 'Đang dọn') DEFAULT 'Trống',
    FOREIGN KEY (`type_id`) REFERENCES `room_type`(`type_id`),
    FOREIGN KEY (`floor_id`) REFERENCES `floor`(`floor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 4. BẢNG KHÁCH HÀNG (customer)
-- =============================================
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
    `customer_id` INT PRIMARY KEY AUTO_INCREMENT,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) UNIQUE,
    `cccd` VARCHAR(20) UNIQUE,
    `address` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 5. BẢNG NHÂN VIÊN (employee)
-- =============================================
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
    `employee_id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) UNIQUE,
    `password` VARCHAR(255),
    `full_name` VARCHAR(100),
    `role` VARCHAR(20) DEFAULT 'Nhân viên'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 6. BẢNG ĐẶT PHÒNG (reservation)
-- =============================================
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
    `reservation_id` INT PRIMARY KEY AUTO_INCREMENT,
    `customer_id` INT,
    `room_id` INT,
    `booking_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `checkin_date` DATETIME,
    `checkout_date` DATETIME,
    `status` ENUM('Đã đặt', 'Đã hủy', 'Đã nhận phòng') DEFAULT 'Đã đặt',
    FOREIGN KEY (`customer_id`) REFERENCES `customer`(`customer_id`),
    FOREIGN KEY (`room_id`) REFERENCES `room`(`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 7. BẢNG CHECK-IN (checkin)
-- =============================================
DROP TABLE IF EXISTS `checkin`;
CREATE TABLE `checkin` (
    `checkin_id` INT PRIMARY KEY AUTO_INCREMENT,
    `reservation_id` INT UNIQUE,
    `checkin_time` DATETIME NOT NULL,
    `checkout_time` DATETIME,
    FOREIGN KEY (`reservation_id`) REFERENCES `reservation`(`reservation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 8. BẢNG DỊCH VỤ (service)
-- =============================================
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
    `service_id` INT PRIMARY KEY AUTO_INCREMENT,
    `service_name` VARCHAR(100) NOT NULL,
    `price` DECIMAL(12,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 9. BẢNG SỬ DỤNG DỊCH VỤ (service_usage)
-- =============================================
DROP TABLE IF EXISTS `service_usage`;
CREATE TABLE `service_usage` (
    `usage_id` INT PRIMARY KEY AUTO_INCREMENT,
    `checkin_id` INT,
    `service_id` INT,
    `quantity` INT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`checkin_id`) REFERENCES `checkin`(`checkin_id`),
    FOREIGN KEY (`service_id`) REFERENCES `service`(`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 10. BẢNG HÓA ĐƠN (invoice)
-- =============================================
DROP TABLE IF EXISTS `invoice`;
CREATE TABLE `invoice` (
    `invoice_id` INT PRIMARY KEY AUTO_INCREMENT,
    `checkin_id` INT UNIQUE,
    `employee_id` INT,
    `room_fee` DECIMAL(12,2) NOT NULL,
    `service_fee` DECIMAL(12,2) NOT NULL,
    `total_amount` DECIMAL(12,2),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`checkin_id`) REFERENCES `checkin`(`checkin_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- INSERT DỮ LIỆU MẪU
-- Tuân thủ nghiệp vụ:
--   1. Đặt phòng → reservation status='Đã đặt', room status='Đã đặt'
--   2. Nhận phòng → reservation status='Đã nhận phòng', tạo checkin, room status='Đã thuê'
--   3. Hủy phòng → reservation status='Đã hủy', room status='Trống'
--   4. Trả phòng → checkin có checkout_time, room status='Đang dọn' hoặc 'Trống'
--   5. Dịch vụ chỉ thêm khi có checkin
--   6. Invoice chỉ tạo khi đã checkout
-- Ngày hiện tại: 2026-01-05
-- =============================================

-- 1. LOẠI PHÒNG
INSERT INTO `room_type` (`type_name`, `price`, `description`) VALUES
('Phòng đơn', 300000.00, '1 giường đơn'),
('Phòng đôi', 500000.00, '2 giường đơn'),
('Phòng gia đình', 700000.00, '3-4 người'),
('Phòng VIP', 1000000.00, 'Cao cấp'),
('Suite', 1500000.00, 'Hạng sang'),
('Dorm', 200000.00, 'Phòng tập thể');

-- 2. TẦNG
INSERT INTO `floor` (`floor_number`, `description`) VALUES
(1, 'Tầng lễ tân'),
(2, 'Tầng tiêu chuẩn'),
(3, 'Tầng tiêu chuẩn'),
(4, 'Tầng cao cấp'),
(5, 'Tầng VIP');

-- 3. PHÒNG (trạng thái tại thời điểm 2026-01-05)
-- room 5: đang có reservation 25 checkin chưa checkout → Đã thuê
-- room 6: reservation 26 status='Đã đặt' → Đã đặt
-- Các phòng khác: quá khứ đã checkout → Trống
INSERT INTO `room` (`room_number`, `type_id`, `floor_id`, `status`) VALUES
('101', 1, 1, 'Trống'),
('102', 1, 1, 'Trống'),
('103', 6, 1, 'Trống'),
('104', 6, 1, 'Trống'),
('201', 2, 2, 'Đã thuê'),
('202', 2, 2, 'Đã đặt'),
('203', 3, 2, 'Trống'),
('204', 1, 2, 'Trống'),
('205', 1, 2, 'Trống'),
('301', 2, 3, 'Trống'),
('302', 3, 3, 'Trống'),
('303', 3, 3, 'Trống'),
('304', 2, 3, 'Trống'),
('401', 4, 4, 'Trống'),
('402', 4, 4, 'Trống'),
('403', 5, 4, 'Trống'),
('404', 4, 4, 'Trống'),
('501', 5, 5, 'Trống'),
('502', 4, 5, 'Trống'),
('503', 5, 5, 'Trống');

-- 4. KHÁCH HÀNG
INSERT INTO `customer` (`full_name`, `phone`, `cccd`, `address`) VALUES
('Nguyễn Văn A', '0901000001', '001001001001', 'Hà Nội'),
('Trần Thị B', '0901000002', '001001001002', 'TP HCM'),
('Lê Văn C', '0901000003', '001001001003', 'Đà Nẵng'),
('Phạm Thị D', '0901000004', '001001001004', 'Hải Phòng'),
('Hoàng Văn E', '0901000005', '001001001005', 'Cần Thơ'),
('Đỗ Thị F', '0901000006', '001001001006', 'Bình Dương'),
('Vũ Văn G', '0901000007', '001001001007', 'Nha Trang'),
('Bùi Thị H', '0901000008', '001001001008', 'Huế'),
('Ngô Văn I', '0901000009', '001001001009', 'Quảng Ninh'),
('Phan Thị K', '0901000010', '001001001010', 'Vũng Tàu');

-- 5. NHÂN VIÊN
INSERT INTO `employee` (`username`, `password`, `full_name`, `role`) VALUES
('admin', '123456', 'Quản lý khách sạn', 'Quản lý'),
('lt01', '123456', 'Lễ tân ca sáng', 'Nhân viên'),
('lt02', '123456', 'Lễ tân ca chiều', 'Nhân viên'),
('kt01', '123456', 'Kế toán', 'Nhân viên'),
('bv01', '123456', 'Bảo vệ', 'Nhân viên');

-- 6. ĐẶT PHÒNG
-- Trạng thái theo nghiệp vụ:
--   'Đã nhận phòng' → có checkin + checkout_time → có invoice
--   'Đã đặt' → chưa checkin, room = 'Đã đặt'
--   'Đã hủy' → không có checkin, room = 'Trống'
-- booking_date: ngày đặt phòng (trước checkin_date 1-7 ngày)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
-- Quá khứ đã hoàn tất (checkout xong)
(1, 1, '2025-05-28 10:00:00', '2025-06-02 14:00:00', '2025-06-04 12:00:00', 'Đã nhận phòng'),
(2, 2, '2025-05-30 09:00:00', '2025-06-05 14:00:00', '2025-06-07 12:00:00', 'Đã nhận phòng'),
(3, 3, '2025-06-01 11:00:00', '2025-06-08 14:00:00', '2025-06-10 12:00:00', 'Đã hủy'),
(4, 4, '2025-06-08 14:00:00', '2025-06-12 14:00:00', '2025-06-14 12:00:00', 'Đã nhận phòng'),
(6, 6, '2025-06-15 10:00:00', '2025-06-20 14:00:00', '2025-06-22 12:00:00', 'Đã nhận phòng'),
(7, 7, '2025-06-20 09:00:00', '2025-06-25 14:00:00', '2025-06-27 12:00:00', 'Đã hủy'),
(8, 8, '2025-06-25 15:00:00', '2025-07-01 14:00:00', '2025-07-03 12:00:00', 'Đã nhận phòng'),
(10, 10, '2025-07-05 10:00:00', '2025-07-10 14:00:00', '2025-07-12 12:00:00', 'Đã nhận phòng'),
(1, 11, '2025-07-28 11:00:00', '2025-08-02 14:00:00', '2025-08-04 12:00:00', 'Đã nhận phòng'),
(3, 13, '2025-08-10 09:00:00', '2025-08-15 14:00:00', '2025-08-18 12:00:00', 'Đã nhận phòng'),
(4, 14, '2025-08-25 14:00:00', '2025-09-01 14:00:00', '2025-09-03 12:00:00', 'Đã hủy'),
(5, 15, '2025-09-05 10:00:00', '2025-09-10 14:00:00', '2025-09-13 12:00:00', 'Đã nhận phòng'),
(6, 16, '2025-09-30 11:00:00', '2025-10-05 14:00:00', '2025-10-07 12:00:00', 'Đã nhận phòng'),
(8, 18, '2025-10-25 09:00:00', '2025-11-01 14:00:00', '2025-11-03 12:00:00', 'Đã nhận phòng'),
(9, 19, '2025-11-05 10:00:00', '2025-11-10 14:00:00', '2025-11-12 12:00:00', 'Đã hủy'),
(10, 20, '2025-11-15 14:00:00', '2025-11-20 14:00:00', '2025-11-23 12:00:00', 'Đã nhận phòng'),
(1, 1, '2025-11-28 10:00:00', '2025-12-02 14:00:00', '2025-12-05 12:00:00', 'Đã nhận phòng'),
(3, 3, '2025-12-10 09:00:00', '2025-12-15 14:00:00', '2025-12-18 12:00:00', 'Đã nhận phòng'),
(4, 4, '2025-12-15 11:00:00', '2025-12-20 14:00:00', '2025-12-22 12:00:00', 'Đã hủy'),
-- Hiện tại (2026-01-05): đang ở hoặc sắp đến
(5, 5, '2025-12-28 10:00:00', '2026-01-03 14:00:00', '2026-01-06 12:00:00', 'Đã nhận phòng'),
(6, 6, '2026-01-02 09:00:00', '2026-01-07 14:00:00', '2026-01-09 12:00:00', 'Đã đặt');

-- 7. CHECK-IN
-- Chỉ tạo cho reservation status='Đã nhận phòng'
-- checkin_id tự tăng: 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(1, '2025-06-02 14:10:00', '2025-06-04 11:50:00'),
(2, '2025-06-05 14:05:00', '2025-06-07 11:55:00'),
(4, '2025-06-12 14:15:00', '2025-06-14 11:45:00'),
(5, '2025-06-20 14:00:00', '2025-06-22 11:50:00'),
(7, '2025-07-01 14:10:00', '2025-07-03 11:55:00'),
(8, '2025-07-10 14:05:00', '2025-07-12 11:50:00'),
(9, '2025-08-02 14:00:00', '2025-08-04 11:55:00'),
(10, '2025-08-15 14:10:00', '2025-08-18 11:50:00'),
(12, '2025-09-10 14:05:00', '2025-09-13 11:45:00'),
(13, '2025-10-05 14:00:00', '2025-10-07 11:50:00'),
(14, '2025-11-01 14:10:00', '2025-11-03 11:55:00'),
(16, '2025-11-20 14:05:00', '2025-11-23 11:50:00'),
(17, '2025-12-02 14:10:00', '2025-12-05 11:55:00'),
(18, '2025-12-15 14:05:00', '2025-12-18 11:50:00'),
(20, '2026-01-03 14:10:00', NULL);

-- 8. DỊCH VỤ
INSERT INTO `service` (`service_name`, `price`) VALUES
('Ăn sáng', 100000.00),
('Giặt ủi', 50000.00),
('Thuê xe máy', 150000.00),
('Spa', 300000.00),
('Đưa đón sân bay', 400000.00),
('Mini bar', 80000.00);

-- 9. SỬ DỤNG DỊCH VỤ
-- Chỉ tham chiếu checkin_id tồn tại (1-15)
-- created_at: thời điểm sử dụng dịch vụ (trong khoảng checkin_time đến checkout_time)
INSERT INTO `service_usage` (`checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
-- checkin_id=1: 2025-06-02 14:10 -> 2025-06-04 11:50
(1, 1, 2, '2025-06-03 07:30:00'),
(1, 2, 1, '2025-06-03 10:00:00'),
-- checkin_id=2: 2025-06-05 14:05 -> 2025-06-07 11:55
(2, 3, 1, '2025-06-06 09:00:00'),
-- checkin_id=3: 2025-06-12 14:15 -> 2025-06-14 11:45
(3, 4, 1, '2025-06-13 15:00:00'),
-- checkin_id=4: 2025-06-20 14:00 -> 2025-06-22 11:50
(4, 6, 2, '2025-06-21 20:00:00'),
-- checkin_id=5: 2025-07-01 14:10 -> 2025-07-03 11:55
(5, 1, 2, '2025-07-02 07:30:00'),
(5, 5, 1, '2025-07-01 15:00:00'),
-- checkin_id=6: 2025-07-10 14:05 -> 2025-07-12 11:50
(6, 2, 1, '2025-07-11 09:00:00'),
-- checkin_id=7: 2025-08-02 14:00 -> 2025-08-04 11:55
(7, 1, 2, '2025-08-03 07:30:00'),
(7, 3, 1, '2025-08-03 10:00:00'),
-- checkin_id=8: 2025-08-15 14:10 -> 2025-08-18 11:50
(8, 6, 2, '2025-08-16 21:00:00'),
-- checkin_id=9: 2025-09-10 14:05 -> 2025-09-13 11:45
(9, 5, 1, '2025-09-10 15:00:00'),
-- checkin_id=10: 2025-10-05 14:00 -> 2025-10-07 11:50
(10, 2, 2, '2025-10-06 09:00:00'),
(10, 1, 2, '2025-10-06 07:30:00'),
-- checkin_id=11: 2025-11-01 14:10 -> 2025-11-03 11:55
(11, 1, 2, '2025-11-02 07:30:00'),
-- checkin_id=12: 2025-11-20 14:05 -> 2025-11-23 11:50
(12, 4, 1, '2025-11-21 16:00:00'),
(12, 6, 2, '2025-11-22 20:00:00'),
-- checkin_id=13: 2025-12-02 14:10 -> 2025-12-05 11:55
(13, 2, 1, '2025-12-03 09:00:00'),
(13, 1, 2, '2025-12-04 07:30:00'),
-- checkin_id=14: 2025-12-15 14:05 -> 2025-12-18 11:50
(14, 3, 1, '2025-12-16 10:00:00'),
-- checkin_id=15: 2026-01-03 14:10 -> NULL (đang ở)
(15, 1, 2, '2026-01-04 07:30:00');

-- 10. HÓA ĐƠN
-- Chỉ lập cho checkin đã có checkout_time (checkin_id 1-14)
-- room_fee = giá phòng × số đêm
-- service_fee = tổng tiền dịch vụ
-- total_amount = room_fee + service_fee
INSERT INTO `invoice` (`checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(1, 2, 600000.00, 250000.00, 850000.00, '2025-06-04 12:00:00'),
(2, 3, 1000000.00, 150000.00, 1150000.00, '2025-06-07 12:00:00'),
(3, 2, 400000.00, 300000.00, 700000.00, '2025-06-14 12:00:00'),
(4, 4, 1000000.00, 160000.00, 1160000.00, '2025-06-22 12:00:00'),
(5, 3, 600000.00, 600000.00, 1200000.00, '2025-07-03 12:00:00'),
(6, 2, 400000.00, 50000.00, 450000.00, '2025-07-12 12:00:00'),
(7, 3, 1000000.00, 350000.00, 1350000.00, '2025-08-04 12:00:00'),
(8, 2, 2100000.00, 160000.00, 2260000.00, '2025-08-18 12:00:00'),
(9, 4, 1500000.00, 400000.00, 1900000.00, '2025-09-13 12:00:00'),
(10, 3, 2000000.00, 300000.00, 2300000.00, '2025-10-07 12:00:00'),
(11, 2, 600000.00, 200000.00, 800000.00, '2025-11-03 12:00:00'),
(12, 4, 2100000.00, 460000.00, 2560000.00, '2025-11-23 12:00:00'),
(13, 3, 900000.00, 250000.00, 1150000.00, '2025-12-05 12:00:00'),
(14, 2, 600000.00, 150000.00, 750000.00, '2025-12-18 12:00:00');

-- =============================================
-- THÊM DỮ LIỆU MẪU CHO THÁNG 01/2026
-- Mục đích: tạo các hóa đơn trong tháng 1 để phục vụ thống kê doanh thu
-- Theo nghiệp vụ: reservation status='Đã nhận phòng' + checkin có checkout_time → có invoice
-- =============================================

-- 1) Khách: Ngô Văn I (customer_id=9) — Phòng 101 (room_id=1)
--    Thời gian: 2026-01-02 → 2026-01-04 (2 đêm)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(9, 1, '2025-12-28 10:00:00', '2026-01-02 14:00:00', '2026-01-04 12:00:00', 'Đã nhận phòng');
SET @res_id_2026_1 = LAST_INSERT_ID();

INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(@res_id_2026_1, '2026-01-02 14:15:00', '2026-01-04 11:50:00');
SET @checkin_id_2026_1 = LAST_INSERT_ID();

-- Dịch vụ: 2x Ăn sáng (service_id=1)
INSERT INTO `service_usage` (`checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
(@checkin_id_2026_1, 1, 2, '2026-01-03 07:30:00');

-- Hóa đơn: room_fee = 2 * 300000 = 600000; service_fee = 2 * 100000 = 200000
INSERT INTO `invoice` (`checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(@checkin_id_2026_1, 2, 600000.00, 200000.00, 800000.00, '2026-01-04 12:00:00');

-- 2) Khách: Phạm Thị D (customer_id=4) - Phòng 103 (room_id=3)
--    Thời gian: 2026-01-10 → 2026-01-11 (1 đêm)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(4, 3, '2026-01-05 11:00:00', '2026-01-10 14:00:00', '2026-01-11 12:00:00', 'Đã nhận phòng');
SET @res_id_2026_2 = LAST_INSERT_ID();

INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(@res_id_2026_2, '2026-01-10 14:05:00', '2026-01-11 11:45:00');
SET @checkin_id_2026_2 = LAST_INSERT_ID();

-- Dịch vụ: 1x Mini bar (service_id=6)
INSERT INTO `service_usage` (`checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
(@checkin_id_2026_2, 6, 1, '2026-01-10 20:00:00');

-- Hóa đơn: room_fee = 1 * 200000 = 200000; service_fee = 1 * 80000 = 80000
INSERT INTO `invoice` (`checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(@checkin_id_2026_2, 3, 200000.00, 80000.00, 280000.00, '2026-01-11 12:00:00');

-- ===== Thêm bộ dữ liệu bổ sung cho Tháng 01/2026 =====

-- A) Completed stay 1
-- Khách: Trần Thị B (customer_id=2) — Phòng 102 (room_id=2)
-- 2026-01-05 -> 2026-01-07 (2 đêm), dịch vụ: Spa (service_id=4)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(2, 2, '2026-01-01 09:00:00', '2026-01-05 14:00:00', '2026-01-07 12:00:00', 'Đã nhận phòng');
SET @res_jan_a = LAST_INSERT_ID();

INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(@res_jan_a, '2026-01-05 14:10:00', '2026-01-07 11:50:00');
SET @check_jan_a = LAST_INSERT_ID();

INSERT INTO `service_usage` (`checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
(@check_jan_a, 4, 1, '2026-01-06 15:00:00'); -- Spa x1

INSERT INTO `invoice` (`checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(@check_jan_a, 4, 600000.00, 300000.00, 900000.00, '2026-01-07 12:00:00');

-- B) Completed stay 2
-- Khách: Vũ Văn G (customer_id=7) — Phòng 302 (room_id=11)
-- 2026-01-12 -> 2026-01-13 (1 đêm), dịch vụ: Mini bar (service_id=6)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(7, 11, '2026-01-08 10:00:00', '2026-01-12 14:00:00', '2026-01-13 12:00:00', 'Đã nhận phòng');
SET @res_jan_b = LAST_INSERT_ID();

INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(@res_jan_b, '2026-01-12 14:05:00', '2026-01-13 11:45:00');
SET @check_jan_b = LAST_INSERT_ID();

INSERT INTO `service_usage` (`checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
(@check_jan_b, 6, 1, '2026-01-12 21:00:00'); -- Mini bar x1

INSERT INTO `invoice` (`checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(@check_jan_b, 2, 700000.00, 80000.00, 780000.00, '2026-01-13 12:00:00');

-- C) Completed stay 3
-- Khách: Nguyễn Văn A (customer_id=1) — Phòng 101 (room_id=1)
-- 2026-01-18 -> 2026-01-20 (2 đêm), dịch vụ: Giặt ủi (service_id=2 x1) + Ăn sáng (service_id=1 x2)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(1, 1, '2026-01-14 11:00:00', '2026-01-18 14:00:00', '2026-01-20 12:00:00', 'Đã nhận phòng');
SET @res_jan_c = LAST_INSERT_ID();

INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(@res_jan_c, '2026-01-18 14:10:00', '2026-01-20 11:55:00');
SET @check_jan_c = LAST_INSERT_ID();

INSERT INTO `service_usage` (`checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
(@check_jan_c, 2, 1, '2026-01-19 09:00:00'), -- Giặt ủi x1
(@check_jan_c, 1, 2, '2026-01-19 07:30:00'); -- Ăn sáng x2

INSERT INTO `invoice` (`checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(@check_jan_c, 1, 600000.00, 250000.00, 850000.00, '2026-01-20 12:00:00');

-- D) Ongoing checkin (chưa checkout) — không tạo invoice
-- Khách: Đỗ Thị F (customer_id=6) — Phòng 104 (room_id=4)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(6, 4, '2026-01-15 10:00:00', '2026-01-20 14:00:00', '2026-01-22 12:00:00', 'Đã nhận phòng');
SET @res_jan_d = LAST_INSERT_ID();

INSERT INTO `checkin` (`reservation_id`, `checkin_time`, `checkout_time`) VALUES
(@res_jan_d, '2026-01-20 14:05:00', NULL);
-- (chưa checkout -> không có invoice)

-- E) Future reservation (status='Đã đặt')
-- Khách: Lê Văn C (customer_id=3) — Phòng 202 (room_id=6)
INSERT INTO `reservation` (`customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(3, 6, '2026-01-05 09:00:00', '2026-02-01 14:00:00', '2026-02-03 12:00:00', 'Đã đặt');

-- =============================================
-- KẾT THÚC: Dữ liệu mẫu cho tháng 01/2026
-- =============================================
