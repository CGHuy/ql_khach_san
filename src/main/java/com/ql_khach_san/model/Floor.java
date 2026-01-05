package com.ql_khach_san.model;

public class Floor {
    private int floorId;
    private int floorNumber;
    private String description;

    public Floor() {}

    public Floor(int floorId, int floorNumber, String description) {
        this.floorId = floorId;
        this.floorNumber = floorNumber;
        this.description = description;
    }

    public int getFloorId() { return floorId; }
    public int getFloorNumber() { return floorNumber; }
    public String getDescription() { return description; }

    public void setFloorId(int floorId) { this.floorId = floorId; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }
    public void setDescription(String description) { this.description = description; }
}
