/* ==============================
   1. LOẠI PHÒNG (KHÁCH SẠN 3 SAO)
============================== */
INSERT INTO room_type (type_name, price, description) VALUES
('Phòng Tiêu Chuẩn', 500000, 'Phòng tiêu chuẩn, 1–2 khách, tiện nghi cơ bản'),
('Phòng Cao Cấp', 700000, 'Phòng rộng hơn, nội thất hiện đại'),
('Phòng Hạng Sang', 900000, 'Phòng chất lượng cao, không gian thoáng'),
('Phòng Hạng Sang Hướng Thành Phố', 1000000, 'Phòng hạng sang, view thành phố'),
('Phòng Gia Đình', 1200000, 'Phòng lớn, phù hợp gia đình 3–4 người');

/* ==============================
   2. PHÒNG
============================== */
INSERT INTO room (room_number, type_id, status) VALUES
('101', 1, 'Trống'),
('102', 1, 'Đã thuê'),
('103', 1, 'Đang dọn'),
('104', 1, 'Trống'),
('105', 1, 'Đã đặt'),

('201', 2, 'Trống'),
('202', 2, 'Đã đặt'),
('203', 2, 'Đã thuê'),
('204', 2, 'Đang dọn'),
('205', 2, 'Trống'),

('301', 3, 'Trống'),
('302', 3, 'Đã thuê'),
('303', 3, 'Đã đặt'),
('304', 3, 'Trống'),

('401', 4, 'Trống'),
('402', 4, 'Đã đặt'),
('403', 4, 'Đã thuê'),

('501', 5, 'Trống'),
('502', 5, 'Đã thuê'),
('503', 5, 'Trống');

/* ==============================
   3. KHÁCH HÀNG
============================== */
INSERT INTO customer (full_name, phone, cccd, address) VALUES
('Nguyễn Minh Hoàng', '0908123456', '079201000001', 'Ba Đình, Hà Nội'),
('Trần Thị Thu Hương', '0912345678', '079201000002', 'Quận 1, TP Hồ Chí Minh'),
('Lê Quốc Anh', '0987654321', '079201000003', 'Hải Châu, Đà Nẵng'),
('Phạm Ngọc Lan', '0909988776', '079201000004', 'Ninh Kiều, Cần Thơ'),
('Hoàng Văn Long', '0933445566', '079201000005', 'Hồng Bàng, Hải Phòng'),
('Đặng Thị Mai', '0977112233', '079201000006', 'Thanh Xuân, Hà Nội'),
('Vũ Thanh Tùng', '0966112233', '079201000007', 'Huế'),
('Bùi Thị Hạnh', '0944556677', '079201000008', 'Bắc Ninh'),
('Phan Quốc Bảo', '0938997766', '079201000009', 'Quảng Ngãi'),
('Ngô Thị Kim Chi', '0977334455', '079201000010', 'Nam Định'),
('Đỗ Minh Tuấn', '0911223344', '079201000011', 'Hà Nam');

/* ==============================
   4. NHÂN VIÊN
============================== */
INSERT INTO employee (username, password, full_name, role) VALUES
('quanly_trung', 'hashed_pw_ql', 'Nguyễn Văn Trung', 'Quản lý'),
('letan_anh', 'hashed_pw_lt1', 'Trần Thị Ánh', 'Nhân viên'),
('letan_huy', 'hashed_pw_lt2', 'Phạm Quốc Huy', 'Nhân viên'),
('ketoan_linh', 'hashed_pw_kt', 'Lê Ngọc Linh', 'Nhân viên');

/* ==============================
   5. ĐẶT PHÒNG
============================== */
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status) VALUES
(1, 2,  '2025-01-05 14:00:00', '2025-01-07 12:00:00', 'Đã nhận phòng'),
(2, 8,  '2025-01-06 14:00:00', '2025-01-09 12:00:00', 'Đã nhận phòng'),
(3, 6,  '2025-01-10 14:00:00', '2025-01-11 12:00:00', 'Đã đặt'),
(4, 16, '2025-01-12 14:00:00', '2025-01-15 12:00:00', 'Đã đặt'),
(5, 1,  '2025-01-15 14:00:00', '2025-01-17 12:00:00', 'Đã hủy'),
(6, 19, '2025-01-18 14:00:00', '2025-01-20 12:00:00', 'Đã đặt'),
(7, 3,  '2025-01-08 14:00:00', '2025-01-10 12:00:00', 'Đã nhận phòng'),
(8, 12, '2025-01-09 14:00:00', '2025-01-11 12:00:00', 'Đã đặt'),
(9, 14, '2025-01-10 14:00:00', '2025-01-13 12:00:00', 'Đã đặt'),
(10, 18,'2025-01-11 14:00:00', '2025-01-12 12:00:00', 'Đã hủy'),
(11, 17,'2025-01-12 14:00:00', '2025-01-14 12:00:00', 'Đã nhận phòng');

/* ==============================
   6. CHECK-IN
============================== */
INSERT INTO checkin (reservation_id, checkin_time, checkout_time) VALUES
(1, '2025-01-05 14:10:00', '2025-01-07 11:45:00'),
(2, '2025-01-06 14:15:00', NULL),
(7, '2025-01-08 14:05:00', '2025-01-10 11:50:00'),
(11,'2025-01-12 14:20:00', NULL);

/* ==============================
   7. DỊCH VỤ (PHỔ BIẾN 3 SAO)
============================== */
INSERT INTO service (service_name, price) VALUES
('Ăn sáng', 50000),
('Giặt ủi quần áo', 30000),
('Thuê xe máy theo ngày', 150000),
('Nước suối chai 500ml', 20000),
('Đưa đón sân bay', 250000);

/* ==============================
   8. SỬ DỤNG DỊCH VỤ
============================== */
INSERT INTO service_usage (checkin_id, service_id, quantity) VALUES
(1, 1, 2),
(1, 4, 3),
(1, 2, 1),
(2, 1, 3),
(2, 3, 1),
(2, 5, 1),
(3, 1, 1),
(3, 4, 2),
(4, 1, 2),
(4, 2, 1);

/* ==============================
   9. HÓA ĐƠN
============================== */
INSERT INTO invoice (checkin_id, employee_id, room_fee, service_fee, total_amount) VALUES
(1, 2, 1400000, 190000, 1590000),
(2, 3, 2100000, 350000, 2450000),
(3, 2, 1000000, 90000, 1090000);

/* ==============================
   END FILE
============================== */