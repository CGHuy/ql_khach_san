package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.dao.FloorDAO;
import com.ql_khach_san.model.Room;
import com.ql_khach_san.model.RoomType;
import com.ql_khach_san.model.Floor;

import java.awt.Color;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainService {
    private final RoomDAO roomDAO;
    private final RoomTypeDAO roomTypeDAO;
    private final FloorDAO floorDAO;

    private static final Color COLOR_AVAILABLE = new Color(0, 200, 0);
    private static final Color COLOR_OCCUPIED = new Color(200, 40, 40);
    private static final Color COLOR_CLEANING = new Color(0, 100, 255);
    private static final Color COLOR_RESERVED = new Color(255, 165, 0);

    public MainService() {
        this.roomDAO = new RoomDAO();
        this.roomTypeDAO = new RoomTypeDAO();
        this.floorDAO = new FloorDAO();
    }

    public List<RoomView> getAllRoomViews() {
        List<Room> rooms = roomDAO.getAll();
        List<RoomView> views = new ArrayList<>();

        for (Room r : rooms) {
            RoomType rt = roomTypeDAO.getById(r.getTypeId());
            String typeName = rt != null ? rt.getTypeName() : "";
            Floor f = floorDAO.getById(r.getFloorId());
            String floor = f != null ? String.valueOf(f.getFloor_number()) : "0";
            Color color = mapStatusToColor(r.getStatus());

            // 1. Mặc định reservationId là 0 (hoặc -1) nếu phòng không phải 'Đã đặt'
            int resId = 0; 

            // 2. Nếu trạng thái là "Đã đặt" hoặc "Đã thuê", đi lấy ID đơn đặt từ Database
            if ("Đã đặt".equalsIgnoreCase(r.getStatus()) || "Đã thuê".equalsIgnoreCase(r.getStatus())) {
                resId = roomDAO.getActiveReservationId(r.getRoomId());
            }

            RoomView view = new RoomView(
                r.getRoomId(), 
                resId, 
                r.getRoomNumber(), 
                typeName, 
                r.getStatus(), 
                floor, 
                color
            );

            views.add(view);
        }
        return views;
    }

    public Map<String, List<RoomView>> getRoomsGroupedByFloor() {
        List<RoomView> all = getAllRoomViews();
        Map<String, List<RoomView>> map = new HashMap<>();
        for (RoomView v : all) {
            map.computeIfAbsent(v.getFloor(), k -> new ArrayList<>()).add(v);
        }
        return map;
    }

    public List<String> getFloors() {
        Map<String, List<RoomView>> map = getRoomsGroupedByFloor();
        List<String> floors = new ArrayList<>(map.keySet());
        floors.sort((a, b) -> {
            try { return Integer.compare(Integer.parseInt(a), Integer.parseInt(b)); }
            catch (NumberFormatException e) { return a.compareTo(b); }
        });
        return floors;
    }

    public List<RoomView> getRoomsByFloor(String floor) {
        Map<String, List<RoomView>> map = getRoomsGroupedByFloor();
        return map.getOrDefault(floor, Collections.emptyList());
    }

    private Color mapStatusToColor(String status) {
        if (status == null) return COLOR_AVAILABLE;
        String s = status.toLowerCase().trim();
        if (s.contains("trống")) return COLOR_AVAILABLE;
        if (s.contains("đã thuê")) return COLOR_OCCUPIED;
        if (s.contains("đã đặt")) return COLOR_RESERVED;
        if (s.contains("đang dọn")) return COLOR_CLEANING;
        return COLOR_AVAILABLE;
    }
}
