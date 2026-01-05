package com.ql_khach_san.model;

public class RoomType {
    private int typeId;
    private String typeName;
    private double price;
    private String description;

    public RoomType() {}

    public RoomType(int typeId, String typeName, double price, String description) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.price = price;
        this.description = description;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
