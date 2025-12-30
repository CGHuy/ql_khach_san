package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.model.Room;

public class RoomActionService {
    private final RoomDAO roomDAO;

    public RoomActionService() {
        this.roomDAO = new RoomDAO();
    }

    /**
     * Cập nhật trạng thái phòng
     */
    public boolean updateRoomStatus(int roomId, String newStatus) {
        try {
            return roomDAO.updateStatus(roomId, newStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy thông tin phòng theo ID
     */
    public Room getRoomById(int roomId) {
        try {
            return roomDAO.getById(roomId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nhận phòng (check-in)
     */
    public boolean checkInRoom(int roomId) {
        return updateRoomStatus(roomId, "Đã thuê");
    }

    /**
     * Trả phòng (check-out)
     */
    public boolean checkOutRoom(int roomId) {
        return updateRoomStatus(roomId, "Trống");
    }

    /**
     * Đánh dấu phòng cần dọn dẹp
     */
    public boolean markRoomForCleaning(int roomId) {
        return updateRoomStatus(roomId, "Đang dọn");
    }

    /**
     * Huỷ đặt phòng
     */
    public boolean cancelReservation(int roomId) {
        return updateRoomStatus(roomId, "Trống");
    }

    /**
     * Kiểm tra xem phòng có được đặt hay không
     */
    public boolean isRoomReserved(int roomId) {
        Room room = getRoomById(roomId);
        if (room == null) return false;
        String status = room.getStatus() != null ? room.getStatus().toLowerCase() : "";
        return status.contains("đã") || status.contains("đặt") || status.contains("thuê");
    }

    /**
     * Kiểm tra xem phòng có trống không
     */
    public boolean isRoomAvailable(int roomId) {
        Room room = getRoomById(roomId);
        if (room == null) return false;
        String status = room.getStatus() != null ? room.getStatus().toLowerCase() : "";
        return status.contains("trống") || status.contains("available");
    }
}
