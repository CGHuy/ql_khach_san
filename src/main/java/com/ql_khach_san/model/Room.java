package com.ql_khach_san.model;

public class Room {
    private int roomId;
    private String roomNumber;
    private int typeId;
    private String status;
    
    public Room() {}

    public Room(int roomId, String roomNumber, int typeId, String status) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.typeId = typeId;
        this.status = status;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getStatus() {
        return status;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
