package com.ql_khach_san.model;

import java.time.LocalDateTime;

public class ServiceUsage {
    private int usageId;
    private int checkinId;
    private int serviceId;
    private int quantity;
    private LocalDateTime createdAt;

    public ServiceUsage() {}

    public ServiceUsage(int usageId, int checkinId, int serviceId, int quantity, LocalDateTime createdAt) {
        this.usageId = usageId;
        this.checkinId = checkinId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public int getUsageId() {
        return usageId;
    }

    public int getCheckinId() {
        return checkinId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}
