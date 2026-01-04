package com.ql_khach_san.ui.ThongKe;

import java.util.Date;

public class Statistic {
    private Date statDate;
    private String statPeriod; // 'day', 'month', 'year'
    private double revenue;
    private double roomRevenue;
    private double serviceRevenue;
    private int customerCount;
    private int roomRentedCount;
    private int serviceCount;
    private String note;

    public Statistic() {}

    /**
     * Convenience constructor for computed statistics
     */
    public Statistic(Date statDate, String statPeriod, double revenue, double roomRevenue, double serviceRevenue, int customerCount, int roomRentedCount, int serviceCount, String note) {
        this.statDate = statDate;
        this.statPeriod = statPeriod;
        this.revenue = revenue;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
        this.customerCount = customerCount;
        this.roomRentedCount = roomRentedCount;
        this.serviceCount = serviceCount;
        this.note = note;
    }

    // Getters and setters (only fields currently used by Service/UI)
    public Date getStatDate() { return statDate; }
    public void setStatDate(Date statDate) { this.statDate = statDate; }

    public String getStatPeriod() { return statPeriod; }
    public void setStatPeriod(String statPeriod) { this.statPeriod = statPeriod; }

    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }

    public double getRoomRevenue() { return roomRevenue; }
    public void setRoomRevenue(double roomRevenue) { this.roomRevenue = roomRevenue; }

    public double getServiceRevenue() { return serviceRevenue; }
    public void setServiceRevenue(double serviceRevenue) { this.serviceRevenue = serviceRevenue; }

    public int getCustomerCount() { return customerCount; }
    public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }

    public int getRoomRentedCount() { return roomRentedCount; }
    public void setRoomRentedCount(int roomRentedCount) { this.roomRentedCount = roomRentedCount; }

    public int getServiceCount() { return serviceCount; }
    public void setServiceCount(int serviceCount) { this.serviceCount = serviceCount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
