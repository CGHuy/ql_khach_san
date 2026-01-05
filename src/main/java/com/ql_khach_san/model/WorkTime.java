package com.ql_khach_san.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class WorkTime {
    private int workId;
    private int employeeId;
    private LocalDate workDate;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private String note;
    private LocalDateTime createdAt;

    public WorkTime() {
    }

    public WorkTime(int employeeId, LocalDate workDate, LocalTime timeIn, LocalTime timeOut, String note) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.note = note;
    }

    // Getters and Setters
    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Tính giờ làm (nếu có time_out)
    public double getHoursWorked() {
        if (timeIn != null && timeOut != null) {
            return (timeOut.toSecondOfDay() - timeIn.toSecondOfDay()) / 3600.0;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "workId=" + workId +
                ", employeeId=" + employeeId +
                ", workDate=" + workDate +
                ", timeIn=" + timeIn +
                ", timeOut=" + timeOut +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
