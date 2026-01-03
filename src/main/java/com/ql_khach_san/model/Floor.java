package com.ql_khach_san.model;

public class Floor {
    private int floor_id;
    private int floor_number;
    private String description;

    public Floor(int floor_id, int floor_number, String description) {
        this.floor_id = floor_id;
        this.floor_number = floor_number;
        this.description = description;
    }

    public int getFloor_id() {
        return floor_id;
    }

    public int getFloor_number() {
        return floor_number;
    }

    public String getDescription() {
        return description;
    }

    public void setFloor_id(int floor_id) {
        this.floor_id = floor_id;
    }

    public void setFloor_number(int floor_number) {
        this.floor_number = floor_number;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
