package com.ql_khach_san.ui.TrangChu;

import java.awt.Color;

public class RoomView {
    private int roomId;
    private int reservationId;
    private String roomNumber;
    private String typeName;
    private String status;
    private String floor;
    private Color color;

    public RoomView(int roomId, int reservationId, String roomNumber, String typeName, String status, String floor, Color color) {
        this.roomId = roomId;
        this.reservationId = reservationId;
        this.roomNumber = roomNumber;
        this.typeName = typeName;
        this.status = status;
        this.floor = floor;
        this.color = color;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getReservationId() {
        return reservationId;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getStatus() {
        return status;
    }

    public String getFloor() {
        return floor;
    }

    public Color getColor() {
        return color;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
