package com.ql_khach_san.model;

import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private int customerId;
    private int roomId;
    private LocalDateTime bookingDate;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private String status;

    public Reservation() {}

    public Reservation(int reservationId, int customerId, int roomId, LocalDateTime bookingDate, LocalDateTime checkinDate, LocalDateTime checkoutDate, String status) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.roomId = roomId;
        this.bookingDate = bookingDate;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public LocalDateTime getCheckinDate() {
        return checkinDate;
    }

    public LocalDateTime getCheckoutDate() {
        return checkoutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setCheckinDate(LocalDateTime checkinDate) {
        this.checkinDate = checkinDate;
    }

    public void setCheckoutDate(LocalDateTime checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
