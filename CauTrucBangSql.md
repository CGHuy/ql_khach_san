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
