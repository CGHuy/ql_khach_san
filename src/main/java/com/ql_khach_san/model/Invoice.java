package com.ql_khach_san.model;

import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int checkinId;
    private int employeeId;
    private double roomFee;
    private double serviceFee;
    private double totalAmount;
    private LocalDateTime createdAt;

    public Invoice() {}

    public Invoice(int invoiceId, int checkinId, int employeeId, double roomFee, double serviceFee, double totalAmount, LocalDateTime createdAt) {
        this.invoiceId = invoiceId;
        this.checkinId = checkinId;
        this.employeeId = employeeId;
        this.roomFee = roomFee;
        this.serviceFee = serviceFee;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getCheckinId() {
        return checkinId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public double getRoomFee() {
        return roomFee;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setRoomFee(double roomFee) {
        this.roomFee = roomFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
