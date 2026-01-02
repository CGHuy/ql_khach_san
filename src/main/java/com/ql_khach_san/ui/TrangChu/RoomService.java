package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.ReservationDAO;
import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.model.Room;

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
    public boolean checkInRoom(int roomId) {
        return updateRoomStatus(roomId, "Đã thuê");
    }

    // Trả phòng (check-out)
    public boolean checkOutRoom(int roomId) {
        return updateRoomStatus(roomId, "Đang dọn");
    }

    // Đánh dấu phòng cần dọn dẹp
    public boolean checkClean(int roomId) {
        return updateRoomStatus(roomId, "Trống");
    }

    // Đặt phòng
    public boolean Reservation(int roomId) {
        return updateRoomStatus(roomId, "Đã đặt");
    }
    
    // Huỷ đặt phòng
    public boolean cancelReservation(int roomId, int reservationId) {
        return updateRoomStatus(roomId, "Trống") && updateReservationStatus(reservationId, "Đã hủy");
    }

    // Kiểm tra xem phòng có được đặt hay không
    public boolean isRoomReserved(int roomId) {
        Room room = getRoomById(roomId);
        if (room == null) return false;
        String status = room.getStatus() != null ? room.getStatus().toLowerCase() : "";
        return status.contains("đã") || status.contains("đặt") || status.contains("thuê");
    }

    // Kiểm tra xem phòng có trống không
    public boolean isRoomAvailable(int roomId) {
        Room room = getRoomById(roomId);
        if (room == null) return false;
        String status = room.getStatus() != null ? room.getStatus().toLowerCase() : "";
        return status.contains("trống") || status.contains("available");
    }
}
