package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.ReservationDAO;
import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.model.Reservation;
import com.ql_khach_san.model.Room;
import java.time.LocalDateTime;

public class RoomService {
    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
        this.reservationDAO = new ReservationDAO();
    }

    // Cập nhật trạng thái phòng
    public boolean updateRoomStatus(int roomId, String newStatus) {
        try {
            return roomDAO.updateStatus(roomId, newStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật trạng thái đơn đặt
    public boolean updateReservationStatus(int reservationId, String newStatus) {
        try {
            return reservationDAO.updateStatus(reservationId, newStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy thông tin phòng theo ID
    public Room getRoomById(int roomId) {
        try {
            return roomDAO.getById(roomId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Nhận phòng (check-in)
    public boolean checkInRoom(int roomId) { return updateRoomStatus(roomId, "Đã thuê"); }

    // Trả phòng
    public boolean checkOutRoom(int roomId) { return updateRoomStatus(roomId, "Đang dọn"); }

    // Dọn dẹp xong
    public boolean checkClean(int roomId) { return updateRoomStatus(roomId, "Trống"); }
    
    // Huỷ đặt phòng
    public boolean cancelReservation(int roomId, int reservationId) {
        return updateRoomStatus(roomId, "Trống") && updateReservationStatus(reservationId, "Đã hủy");
    }
    
    // Đặt phòng
    public boolean makeReservation(int roomId, int customerId, LocalDateTime checkin, LocalDateTime checkout) {
        Reservation res = new Reservation();
        res.setRoomId(roomId);
        res.setCustomerId(customerId);
        res.setBookingDate(LocalDateTime.now()); // Thời gian đặt là hiện tại
        res.setCheckinDate(checkin);
        res.setCheckoutDate(checkout);
        res.setStatus("Đã đặt");

        return reservationDAO.createReservationTransaction(res);
    }
}
