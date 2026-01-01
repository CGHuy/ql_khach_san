package com.ql_khach_san.ui.TrangChu;

import java.awt.Color;

public class RoomView {
    private int roomId;
    private String roomNumber;
    private String typeName;
    private String status;
    private String floor;
    private Color color;

    public RoomView(int roomId, String roomNumber, String typeName, String status, String floor, Color color) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.typeName = typeName;
        this.status = status;
        this.floor = floor;
        this.color = color;
    }

    public int getRoomId() {
        return roomId;
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

    public void setColor(Color color) {
        this.color = color;
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
}
