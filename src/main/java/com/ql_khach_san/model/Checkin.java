package com.ql_khach_san.model;

import java.time.LocalDateTime;

public class Checkin {
    private int checkinId;
    private int reservationId;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;

    public Checkin() {}

    public Checkin(int checkinId, int reservationId, LocalDateTime checkinTime, LocalDateTime checkoutTime) {
        this.checkinId = checkinId;
        this.reservationId = reservationId;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
    }

    public int getCheckinId() {
        return checkinId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
    
    
}
