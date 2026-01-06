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
    
    // Đặt phòng
    public boolean makeReservation(int roomId, int customerId, LocalDateTime checkin, LocalDateTime checkout) {
        Reservation res = new Reservation();
        res.setRoomId(roomId);
        res.setCustomerId(customerId);
        res.setBookingDate(LocalDateTime.now());
        res.setCheckinDate(checkin);
        res.setCheckoutDate(checkout);
        res.setStatus("Đã đặt");

        return reservationDAO.createReservationTransaction(res);
    }
    
    // Huỷ đặt phòng
    public boolean cancelReservation(int reservationId) {
        Reservation res = reservationDAO.getById(reservationId);
        if (res != null) {
            boolean ok = reservationDAO.cancelReservationTransaction(res);
            if (ok) {
                updateRoomStatus(res.getRoomId(), "Trống");
            }
            return ok;
        }
        return false;
    }
    
    // Nhận phòng (check-in)
    public boolean checkInRoom(int reservationId) {
        Reservation res = reservationDAO.getById(reservationId);
        if (res != null) {
            boolean ok = reservationDAO.checkInReservationTransaction(res);
            if (ok) {
                updateRoomStatus(res.getRoomId(), "Đã thuê");
            }
            return ok;
        }
        return false;
    }
    
    // Trả phòng
    public boolean checkOutRoom(int roomId) { return updateRoomStatus(roomId, "Đang dọn"); }
    
    // Dọn dẹp xong
    public boolean checkClean(int roomId) { return updateRoomStatus(roomId, "Trống"); }
    
}
