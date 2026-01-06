-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th1 05, 2026 lúc 08:46 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `db_ql_khach_san`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `checkin`
--

CREATE TABLE `checkin` (
  `checkin_id` int(11) NOT NULL,
  `reservation_id` int(11) DEFAULT NULL,
  `checkin_time` datetime NOT NULL,
  `checkout_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `checkin`
--

INSERT INTO `checkin` (`checkin_id`, `reservation_id`, `checkin_time`, `checkout_time`) VALUES
(1, 1, '2025-06-02 14:10:00', '2025-06-04 11:50:00'),
(2, 2, '2025-06-05 14:05:00', '2025-06-07 11:55:00'),
(3, 4, '2025-06-12 14:15:00', '2025-06-14 11:45:00'),
(4, 5, '2025-06-20 14:00:00', '2025-06-22 11:50:00'),
(5, 7, '2025-07-01 14:10:00', '2025-07-03 11:55:00'),
(6, 8, '2025-07-10 14:05:00', '2025-07-12 11:50:00'),
(7, 9, '2025-08-02 14:00:00', '2025-08-04 11:55:00'),
(8, 10, '2025-08-15 14:10:00', '2025-08-18 11:50:00'),
(9, 12, '2025-09-10 14:05:00', '2025-09-13 11:45:00'),
(10, 13, '2025-10-05 14:00:00', '2025-10-07 11:50:00'),
(11, 14, '2025-11-01 14:10:00', '2025-11-03 11:55:00'),
(12, 16, '2025-11-20 14:05:00', '2025-11-23 11:50:00'),
(13, 17, '2025-12-02 14:10:00', '2025-12-05 11:55:00'),
(14, 18, '2025-12-15 14:05:00', '2025-12-18 11:50:00'),
(15, 20, '2026-01-03 14:10:00', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `customer`
--

CREATE TABLE `customer` (
  `customer_id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `cccd` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `customer`
--

INSERT INTO `customer` (`customer_id`, `full_name`, `phone`, `cccd`, `address`) VALUES
(1, 'Nguyễn Văn A', '0901000001', '001001001001', 'Hà Nội'),
(2, 'Trần Thị B', '0901000002', '001001001002', 'TP HCM'),
(3, 'Lê Văn C', '0901000003', '001001001003', 'Đà Nẵng'),
(4, 'Phạm Thị D', '0901000004', '001001001004', 'Hải Phòng'),
(5, 'Hoàng Văn E', '0901000005', '001001001005', 'Cần Thơ'),
(6, 'Đỗ Thị F', '0901000006', '001001001006', 'Bình Dương'),
(7, 'Vũ Văn G', '0901000007', '001001001007', 'Nha Trang'),
(8, 'Bùi Thị H', '0901000008', '001001001008', 'Huế'),
(9, 'Ngô Văn I', '0901000009', '001001001009', 'Quảng Ninh'),
(10, 'Phan Thị K', '0901000010', '001001001010', 'Vũng Tàu');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `employee`
--

CREATE TABLE `employee` (
  `employee_id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'Nhân viên'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employee`
--

INSERT INTO `employee` (`employee_id`, `username`, `password`, `full_name`, `role`) VALUES
(1, 'admin', '123456', 'Quản lý khách sạn', 'Quản lý'),
(2, 'lt01', '123456', 'Lễ tân ca sáng', 'Nhân viên'),
(3, 'lt02', '123456', 'Lễ tân ca chiều', 'Nhân viên'),
(4, 'kt01', '123456', 'Kế toán', 'Nhân viên'),
(5, 'bv01', '123456', 'Bảo vệ', 'Nhân viên');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `floor`
--

CREATE TABLE `floor` (
  `floor_id` int(11) NOT NULL,
  `floor_number` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `floor`
--

INSERT INTO `floor` (`floor_id`, `floor_number`, `description`) VALUES
(1, 1, 'Tầng lễ tân'),
(2, 2, 'Tầng tiêu chuẩn'),
(3, 3, 'Tầng tiêu chuẩn'),
(4, 4, 'Tầng cao cấp'),
(5, 5, 'Tầng VIP');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `invoice`
--

CREATE TABLE `invoice` (
  `invoice_id` int(11) NOT NULL,
  `checkin_id` int(11) DEFAULT NULL,
  `employee_id` int(11) DEFAULT NULL,
  `room_fee` decimal(12,2) NOT NULL,
  `service_fee` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `invoice`
--

INSERT INTO `invoice` (`invoice_id`, `checkin_id`, `employee_id`, `room_fee`, `service_fee`, `total_amount`, `created_at`) VALUES
(1, 1, 2, 600000.00, 250000.00, 850000.00, '2025-06-04 12:00:00'),
(2, 2, 3, 1000000.00, 150000.00, 1150000.00, '2025-06-07 12:00:00'),
(3, 3, 2, 400000.00, 300000.00, 700000.00, '2025-06-14 12:00:00'),
(4, 4, 4, 1000000.00, 160000.00, 1160000.00, '2025-06-22 12:00:00'),
(5, 5, 3, 600000.00, 600000.00, 1200000.00, '2025-07-03 12:00:00'),
(6, 6, 2, 400000.00, 50000.00, 450000.00, '2025-07-12 12:00:00'),
(7, 7, 3, 1000000.00, 350000.00, 1350000.00, '2025-08-04 12:00:00'),
(8, 8, 2, 2100000.00, 160000.00, 2260000.00, '2025-08-18 12:00:00'),
(9, 9, 4, 1500000.00, 400000.00, 1900000.00, '2025-09-13 12:00:00'),
(10, 10, 3, 2000000.00, 300000.00, 2300000.00, '2025-10-07 12:00:00'),
(11, 11, 2, 600000.00, 200000.00, 800000.00, '2025-11-03 12:00:00'),
(12, 12, 4, 2100000.00, 460000.00, 2560000.00, '2025-11-23 12:00:00'),
(13, 13, 3, 900000.00, 250000.00, 1150000.00, '2025-12-05 12:00:00'),
(14, 14, 2, 600000.00, 150000.00, 750000.00, '2025-12-18 12:00:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reservation`
--

CREATE TABLE `reservation` (
  `reservation_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `room_id` int(11) DEFAULT NULL,
  `booking_date` datetime DEFAULT current_timestamp(),
  `checkin_date` datetime DEFAULT NULL,
  `checkout_date` datetime DEFAULT NULL,
  `status` enum('Đã đặt','Đã hủy','Đã nhận phòng') DEFAULT 'Đã đặt'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `reservation`
--

INSERT INTO `reservation` (`reservation_id`, `customer_id`, `room_id`, `booking_date`, `checkin_date`, `checkout_date`, `status`) VALUES
(1, 1, 1, '2026-01-05 14:45:54', '2025-06-02 14:00:00', '2025-06-04 12:00:00', 'Đã nhận phòng'),
(2, 2, 2, '2026-01-05 14:45:54', '2025-06-05 14:00:00', '2025-06-07 12:00:00', 'Đã nhận phòng'),
(3, 3, 3, '2026-01-05 14:45:54', '2025-06-08 14:00:00', '2025-06-10 12:00:00', 'Đã hủy'),
(4, 4, 4, '2026-01-05 14:45:54', '2025-06-12 14:00:00', '2025-06-14 12:00:00', 'Đã nhận phòng'),
(5, 6, 6, '2026-01-05 14:45:54', '2025-06-20 14:00:00', '2025-06-22 12:00:00', 'Đã nhận phòng'),
(6, 7, 7, '2026-01-05 14:45:54', '2025-06-25 14:00:00', '2025-06-27 12:00:00', 'Đã hủy'),
(7, 8, 8, '2026-01-05 14:45:54', '2025-07-01 14:00:00', '2025-07-03 12:00:00', 'Đã nhận phòng'),
(8, 10, 10, '2026-01-05 14:45:54', '2025-07-10 14:00:00', '2025-07-12 12:00:00', 'Đã nhận phòng'),
(9, 1, 11, '2026-01-05 14:45:54', '2025-08-02 14:00:00', '2025-08-04 12:00:00', 'Đã nhận phòng'),
(10, 3, 13, '2026-01-05 14:45:54', '2025-08-15 14:00:00', '2025-08-18 12:00:00', 'Đã nhận phòng'),
(11, 4, 14, '2026-01-05 14:45:54', '2025-09-01 14:00:00', '2025-09-03 12:00:00', 'Đã hủy'),
(12, 5, 15, '2026-01-05 14:45:54', '2025-09-10 14:00:00', '2025-09-13 12:00:00', 'Đã nhận phòng'),
(13, 6, 16, '2026-01-05 14:45:54', '2025-10-05 14:00:00', '2025-10-07 12:00:00', 'Đã nhận phòng'),
(14, 8, 18, '2026-01-05 14:45:54', '2025-11-01 14:00:00', '2025-11-03 12:00:00', 'Đã nhận phòng'),
(15, 9, 19, '2026-01-05 14:45:54', '2025-11-10 14:00:00', '2025-11-12 12:00:00', 'Đã hủy'),
(16, 10, 20, '2026-01-05 14:45:54', '2025-11-20 14:00:00', '2025-11-23 12:00:00', 'Đã nhận phòng'),
(17, 1, 1, '2026-01-05 14:45:54', '2025-12-02 14:00:00', '2025-12-05 12:00:00', 'Đã nhận phòng'),
(18, 3, 3, '2026-01-05 14:45:54', '2025-12-15 14:00:00', '2025-12-18 12:00:00', 'Đã nhận phòng'),
(19, 4, 4, '2026-01-05 14:45:54', '2025-12-20 14:00:00', '2025-12-22 12:00:00', 'Đã hủy'),
(20, 5, 5, '2026-01-05 14:45:54', '2026-01-03 14:00:00', '2026-01-06 12:00:00', 'Đã nhận phòng'),
(21, 6, 6, '2026-01-05 14:45:54', '2026-01-07 14:00:00', '2026-01-09 12:00:00', 'Đã đặt');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `room`
--

CREATE TABLE `room` (
  `room_id` int(11) NOT NULL,
  `room_number` varchar(10) NOT NULL,
  `type_id` int(11) NOT NULL,
  `floor_id` int(11) NOT NULL,
  `status` enum('Trống','Đã đặt','Đã thuê','Đang dọn') DEFAULT 'Trống'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `room`
--

INSERT INTO `room` (`room_id`, `room_number`, `type_id`, `floor_id`, `status`) VALUES
(1, '101', 1, 1, 'Trống'),
(2, '102', 1, 1, 'Trống'),
(3, '103', 6, 1, 'Trống'),
(4, '104', 6, 1, 'Trống'),
(5, '201', 2, 2, 'Đã thuê'),
(6, '202', 2, 2, 'Đã đặt'),
(7, '203', 3, 2, 'Trống'),
(8, '204', 1, 2, 'Trống'),
(9, '205', 1, 2, 'Trống'),
(10, '301', 2, 3, 'Trống'),
(11, '302', 3, 3, 'Trống'),
(12, '303', 3, 3, 'Trống'),
(13, '304', 2, 3, 'Trống'),
(14, '401', 4, 4, 'Trống'),
(15, '402', 4, 4, 'Trống'),
(16, '403', 5, 4, 'Trống'),
(17, '404', 4, 4, 'Trống'),
(18, '501', 5, 5, 'Trống'),
(19, '502', 4, 5, 'Trống'),
(20, '503', 5, 5, 'Trống');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `room_type`
--

CREATE TABLE `room_type` (
  `type_id` int(11) NOT NULL,
  `type_name` varchar(50) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `room_type`
--

INSERT INTO `room_type` (`type_id`, `type_name`, `price`, `description`) VALUES
(1, 'Phòng đơn', 300000.00, '1 giường đơn'),
(2, 'Phòng đôi', 500000.00, '2 giường đơn'),
(3, 'Phòng gia đình', 700000.00, '3-4 người'),
(4, 'Phòng VIP', 1000000.00, 'Cao cấp'),
(5, 'Suite', 1500000.00, 'Hạng sang'),
(6, 'Dorm', 200000.00, 'Phòng tập thể');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `service`
--

CREATE TABLE `service` (
  `service_id` int(11) NOT NULL,
  `service_name` varchar(100) NOT NULL,
  `price` decimal(12,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `service`
--

INSERT INTO `service` (`service_id`, `service_name`, `price`) VALUES
(1, 'Ăn sáng', 100000.00),
(2, 'Giặt ủi', 50000.00),
(3, 'Thuê xe máy', 150000.00),
(4, 'Spa', 300000.00),
(5, 'Đưa đón sân bay', 400000.00),
(6, 'Mini bar', 80000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `service_usage`
--

CREATE TABLE `service_usage` (
  `usage_id` int(11) NOT NULL,
  `checkin_id` int(11) DEFAULT NULL,
  `service_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `service_usage`
--

INSERT INTO `service_usage` (`usage_id`, `checkin_id`, `service_id`, `quantity`, `created_at`) VALUES
(1, 1, 1, 2, '2026-01-05 14:45:55'),
(2, 1, 2, 1, '2026-01-05 14:45:55'),
(3, 2, 3, 1, '2026-01-05 14:45:55'),
(4, 3, 4, 1, '2026-01-05 14:45:55'),
(5, 4, 6, 2, '2026-01-05 14:45:55'),
(6, 5, 1, 2, '2026-01-05 14:45:55'),
(7, 5, 5, 1, '2026-01-05 14:45:55'),
(8, 6, 2, 1, '2026-01-05 14:45:55'),
(9, 7, 1, 2, '2026-01-05 14:45:55'),
(10, 7, 3, 1, '2026-01-05 14:45:55'),
(11, 8, 6, 2, '2026-01-05 14:45:55'),
(12, 9, 5, 1, '2026-01-05 14:45:55'),
(13, 10, 2, 2, '2026-01-05 14:45:55'),
(14, 10, 1, 2, '2026-01-05 14:45:55'),
(15, 11, 1, 2, '2026-01-05 14:45:55'),
(16, 12, 4, 1, '2026-01-05 14:45:55'),
(17, 12, 6, 2, '2026-01-05 14:45:55'),
(18, 13, 2, 1, '2026-01-05 14:45:55'),
(19, 13, 1, 2, '2026-01-05 14:45:55'),
(20, 14, 3, 1, '2026-01-05 14:45:55'),
(21, 15, 1, 2, '2026-01-05 14:45:55');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `checkin`
--
ALTER TABLE `checkin`
  ADD PRIMARY KEY (`checkin_id`),
  ADD UNIQUE KEY `reservation_id` (`reservation_id`);

--
-- Chỉ mục cho bảng `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `phone` (`phone`),
  ADD UNIQUE KEY `cccd` (`cccd`);

--
-- Chỉ mục cho bảng `employee`
--
ALTER TABLE `employee`
  ADD PRIMARY KEY (`employee_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Chỉ mục cho bảng `floor`
--
ALTER TABLE `floor`
  ADD PRIMARY KEY (`floor_id`),
  ADD UNIQUE KEY `floor_number` (`floor_number`);

--
-- Chỉ mục cho bảng `invoice`
--
ALTER TABLE `invoice`
  ADD PRIMARY KEY (`invoice_id`),
  ADD UNIQUE KEY `checkin_id` (`checkin_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Chỉ mục cho bảng `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `room_id` (`room_id`);

--
-- Chỉ mục cho bảng `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`room_id`),
  ADD UNIQUE KEY `room_number` (`room_number`),
  ADD KEY `type_id` (`type_id`),
  ADD KEY `floor_id` (`floor_id`);

--
-- Chỉ mục cho bảng `room_type`
--
ALTER TABLE `room_type`
  ADD PRIMARY KEY (`type_id`);

--
-- Chỉ mục cho bảng `service`
--
ALTER TABLE `service`
  ADD PRIMARY KEY (`service_id`);

--
-- Chỉ mục cho bảng `service_usage`
--
ALTER TABLE `service_usage`
  ADD PRIMARY KEY (`usage_id`),
  ADD KEY `checkin_id` (`checkin_id`),
  ADD KEY `service_id` (`service_id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `checkin`
--
ALTER TABLE `checkin`
  MODIFY `checkin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `customer`
--
ALTER TABLE `customer`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `employee`
--
ALTER TABLE `employee`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `floor`
--
ALTER TABLE `floor`
  MODIFY `floor_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `invoice`
--
ALTER TABLE `invoice`
  MODIFY `invoice_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT cho bảng `reservation`
--
ALTER TABLE `reservation`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT cho bảng `room`
--
ALTER TABLE `room`
  MODIFY `room_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT cho bảng `room_type`
--
ALTER TABLE `room_type`
  MODIFY `type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `service`
--
ALTER TABLE `service`
  MODIFY `service_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `service_usage`
--
ALTER TABLE `service_usage`
  MODIFY `usage_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `checkin`
--
ALTER TABLE `checkin`
  ADD CONSTRAINT `checkin_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`);

--
-- Các ràng buộc cho bảng `invoice`
--
ALTER TABLE `invoice`
  ADD CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`checkin_id`) REFERENCES `checkin` (`checkin_id`),
  ADD CONSTRAINT `invoice_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Các ràng buộc cho bảng `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  ADD CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`);

--
-- Các ràng buộc cho bảng `room`
--
ALTER TABLE `room`
  ADD CONSTRAINT `room_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `room_type` (`type_id`),
  ADD CONSTRAINT `room_ibfk_2` FOREIGN KEY (`floor_id`) REFERENCES `floor` (`floor_id`);

--
-- Các ràng buộc cho bảng `service_usage`
--
ALTER TABLE `service_usage`
  ADD CONSTRAINT `service_usage_ibfk_1` FOREIGN KEY (`checkin_id`) REFERENCES `checkin` (`checkin_id`),
  ADD CONSTRAINT `service_usage_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
