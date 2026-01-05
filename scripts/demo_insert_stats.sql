-- Demo data for statistics (safe inserts with NOT EXISTS)
-- Add reservations / checkins / invoices / service_usage for several dates
-- Adjust customer_id / room_id / service_id / employee_id if your DB differs
-- Run on a dev DB. Designed to be idempotent (won't create duplicates).

-- === Demo day: 2025-12-27 ===
SET @d = '2025-12-27';
-- reservation
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status)
SELECT 1, 2, CONCAT(@d, ' 14:00:00'), CONCAT(@d, ' 12:00:00'), 'Đã nhận phòng'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM reservation r WHERE r.customer_id=1 AND DATE(r.checkin_date)=@d AND r.room_id=2
);
-- checkin
INSERT INTO checkin (reservation_id, checkin_time, checkout_time)
SELECT r.reservation_id, CONCAT(@d, ' 14:05:00'), NULL
FROM reservation r
WHERE r.customer_id=1 AND DATE(r.checkin_date)=@d
  AND NOT EXISTS (SELECT 1 FROM checkin ci WHERE ci.reservation_id = r.reservation_id);
-- invoice
INSERT INTO invoice (checkin_id, employee_id, room_fee, service_fee, total_amount, created_at)
SELECT ci.checkin_id, 2, 500000, 80000, 580000, CONCAT(@d,' 19:00:00')
FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM invoice i WHERE i.checkin_id = ci.checkin_id);
-- service usage
INSERT INTO service_usage (checkin_id, service_id, quantity, created_at)
SELECT ci.checkin_id, 1, 2, CONCAT(@d,' 18:30:00') FROM checkin ci
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM service_usage su WHERE su.checkin_id = ci.checkin_id AND DATE(su.created_at)=@d AND su.service_id=1);

-- === Demo day: 2025-12-29 ===
SET @d = '2025-12-29';
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status)
SELECT 2, 3, CONCAT(@d, ' 14:00:00'), CONCAT(DATE_ADD(@d, INTERVAL 1 DAY),' 12:00:00'), 'Đã nhận phòng'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM reservation r WHERE r.customer_id=2 AND DATE(r.checkin_date)=@d AND r.room_id=3
);
INSERT INTO checkin (reservation_id, checkin_time, checkout_time)
SELECT r.reservation_id, CONCAT(@d, ' 14:10:00'), NULL
FROM reservation r
WHERE r.customer_id=2 AND DATE(r.checkin_date)=@d
  AND NOT EXISTS (SELECT 1 FROM checkin ci WHERE ci.reservation_id = r.reservation_id);
INSERT INTO invoice (checkin_id, employee_id, room_fee, service_fee, total_amount, created_at)
SELECT ci.checkin_id, 3, 4500000, 630000, 5130000, CONCAT(@d,' 19:00:00')
FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM invoice i WHERE i.checkin_id = ci.checkin_id);
INSERT INTO service_usage (checkin_id, service_id, quantity, created_at)
SELECT ci.checkin_id, 4, 3, CONCAT(@d,' 18:00:00') FROM checkin ci
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM service_usage su WHERE su.checkin_id = ci.checkin_id AND DATE(su.created_at)=@d AND su.service_id=4);

-- === Demo day: 2026-01-01 (next year) ===
SET @d = '2026-01-01';
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status)
SELECT 3, 6, CONCAT(@d, ' 14:00:00'), CONCAT(DATE_ADD(@d, INTERVAL 1 DAY),' 12:00:00'), 'Đã nhận phòng'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM reservation r WHERE r.customer_id=3 AND DATE(r.checkin_date)=@d AND r.room_id=6
);
INSERT INTO checkin (reservation_id, checkin_time, checkout_time)
SELECT r.reservation_id, CONCAT(@d, ' 14:05:00'), NULL
FROM reservation r
WHERE r.customer_id=3 AND DATE(r.checkin_date)=@d
  AND NOT EXISTS (SELECT 1 FROM checkin ci WHERE ci.reservation_id = r.reservation_id);
INSERT INTO invoice (checkin_id, employee_id, room_fee, service_fee, total_amount, created_at)
SELECT ci.checkin_id, 2, 700000, 120000, 820000, CONCAT(@d,' 19:30:00')
FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM invoice i WHERE i.checkin_id = ci.checkin_id);
INSERT INTO service_usage (checkin_id, service_id, quantity, created_at)
SELECT ci.checkin_id, 3, 1, CONCAT(@d,' 20:00:00') FROM checkin ci
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM service_usage su WHERE su.checkin_id = ci.checkin_id AND DATE(su.created_at)=@d AND su.service_id=3);

-- === Demo day: 2026-01-02 ===
SET @d = '2026-01-02';
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status)
SELECT 4, 8, CONCAT(@d, ' 14:00:00'), CONCAT(DATE_ADD(@d, INTERVAL 1 DAY),' 12:00:00'), 'Đã nhận phòng'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM reservation r WHERE r.customer_id=4 AND DATE(r.checkin_date)=@d AND r.room_id=8
);
INSERT INTO checkin (reservation_id, checkin_time, checkout_time)
SELECT r.reservation_id, CONCAT(@d, ' 14:20:00'), NULL
FROM reservation r
WHERE r.customer_id=4 AND DATE(r.checkin_date)=@d
  AND NOT EXISTS (SELECT 1 FROM checkin ci WHERE ci.reservation_id = r.reservation_id);
INSERT INTO invoice (checkin_id, employee_id, room_fee, service_fee, total_amount, created_at)
SELECT ci.checkin_id, 3, 550000, 60000, 610000, CONCAT(@d,' 19:10:00')
FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM invoice i WHERE i.checkin_id = ci.checkin_id);
INSERT INTO service_usage (checkin_id, service_id, quantity, created_at)
SELECT ci.checkin_id, 1, 1, CONCAT(@d,' 18:45:00') FROM checkin ci
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM service_usage su WHERE su.checkin_id = ci.checkin_id AND DATE(su.created_at)=@d AND su.service_id=1);

-- === Demo day: 2026-02-05 ===
SET @d = '2026-02-05';
INSERT INTO reservation (customer_id, room_id, checkin_date, checkout_date, status)
SELECT 5, 10, CONCAT(@d, ' 14:00:00'), CONCAT(DATE_ADD(@d, INTERVAL 1 DAY),' 12:00:00'), 'Đã nhận phòng'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM reservation r WHERE r.customer_id=5 AND DATE(r.checkin_date)=@d AND r.room_id=10
);
INSERT INTO checkin (reservation_id, checkin_time, checkout_time)
SELECT r.reservation_id, CONCAT(@d, ' 14:15:00'), NULL
FROM reservation r
WHERE r.customer_id=5 AND DATE(r.checkin_date)=@d
  AND NOT EXISTS (SELECT 1 FROM checkin ci WHERE ci.reservation_id = r.reservation_id);
INSERT INTO invoice (checkin_id, employee_id, room_fee, service_fee, total_amount, created_at)
SELECT ci.checkin_id, 2, 800000, 150000, 950000, CONCAT(@d,' 19:20:00')
FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM invoice i WHERE i.checkin_id = ci.checkin_id);
INSERT INTO service_usage (checkin_id, service_id, quantity, created_at)
SELECT ci.checkin_id, 5, 1, CONCAT(@d,' 18:55:00') FROM checkin ci
WHERE DATE(ci.checkin_time)=@d
  AND NOT EXISTS (SELECT 1 FROM service_usage su WHERE su.checkin_id = ci.checkin_id AND DATE(su.created_at)=@d AND su.service_id=5);


-- End of demo inserts

-- Cleanup helper (run when you want to remove these demo rows):
-- DELETE su FROM service_usage su JOIN checkin ci ON su.checkin_id=ci.checkin_id JOIN reservation r ON ci.reservation_id=r.reservation_id WHERE r.customer_id IN (1,2,3,4,5);
-- DELETE i FROM invoice i JOIN checkin ci ON i.checkin_id=ci.checkin_id JOIN reservation r ON ci.reservation_id=r.reservation_id WHERE r.customer_id IN (1,2,3,4,5);
-- DELETE ci FROM checkin ci JOIN reservation r ON ci.reservation_id=r.reservation_id WHERE r.customer_id IN (1,2,3,4,5);
-- DELETE r FROM reservation r WHERE r.customer_id IN (1,2,3,4,5);

-- Note: adjust customer_id set above to target demo customers (1..5 were used in this script). If you want to tie to DEMO phone numbers, change conditions accordingly.
