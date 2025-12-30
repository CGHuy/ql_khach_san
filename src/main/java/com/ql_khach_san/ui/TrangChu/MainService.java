package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.model.Room;
import com.ql_khach_san.model.RoomType;

import java.awt.Color;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainService {
    private final RoomDAO roomDAO;
    private final RoomTypeDAO roomTypeDAO;

    private static final Color COLOR_AVAILABLE = new Color(0, 200, 0);
    private static final Color COLOR_OCCUPIED = new Color(255, 0, 0);
    private static final Color COLOR_PREPARE = new Color(0, 200, 200);
    private static final Color COLOR_VIP = new Color(128, 0, 128);

    public MainService() {
        this.roomDAO = new RoomDAO();
        this.roomTypeDAO = new RoomTypeDAO();
    }

    public List<RoomView> getAllRoomViews() {
        List<Room> rooms = roomDAO.getAll();
        List<RoomView> views = new ArrayList<>();
        for (Room r : rooms) {
            RoomType rt = roomTypeDAO.getById(r.getTypeId());
            String typeName = rt != null ? rt.getTypeName() : "";
            String floor = extractFloorFromRoomNumber(r.getRoomNumber());
            Color color = mapStatusToColor(r.getStatus(), typeName);
            views.add(new RoomView(r.getRoomId(), r.getRoomNumber(), typeName, r.getStatus(), floor, color));
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

    private Color mapStatusToColor(String status, String typeName) {
        if (status == null) return COLOR_AVAILABLE;
        String s = status.toLowerCase();
        if (s.contains("đã") || s.contains("có người") || s.contains("occupied") || s.contains("thuê") || s.contains("đặt")) {
            return COLOR_OCCUPIED;
        }
        if (s.contains("dọn") || s.contains("don") || s.contains("clean")) {
            return COLOR_PREPARE;
        }
        if (typeName != null && typeName.toLowerCase().contains("vip")) {
            return COLOR_VIP;
        }
        return COLOR_AVAILABLE;
    }

    /**
     * Extract floor string from a room number.
     * Rules: find first continuous digit sequence; if length == 3 => use first digit;
     * if length >= 4 => use first two digits; otherwise return the digit sequence.
     */
    public String extractFloorFromRoomNumber(String roomNumber) {
        if (roomNumber == null) return "0";
        Matcher m = Pattern.compile("(\\d+)").matcher(roomNumber);
        if (m.find()) {
            String digits = m.group(1);
            if (digits.length() == 3) {
                return digits.substring(0, 1);
            } else if (digits.length() >= 4) {
                return digits.substring(0, 2);
            } else {
                return digits;
            }
        }
        return "0";
    }
}
