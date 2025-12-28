package com.ql_khach_san.model;

public class Customer {
    private int customerId;
    private String fullName;
    private String phone;
    private String cccd;
    private String address;

    public Customer() {}

    public Customer(int customerId, String fullName, String phone, String cccd, String address) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.cccd = cccd;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getCccd() {
        return cccd;
    }

    public String getAddress() {
        return address;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    
}
