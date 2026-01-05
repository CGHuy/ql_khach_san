package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.WorkTime;

import java.sql.*;
import java.time.*;
import java.util.*;

public class WorkTimeDAO {

    // Thêm mới bản ghi giờ làm
    public boolean insert(WorkTime w) {
        String sql = "INSERT INTO work_time(employee_id, work_date, time_in, time_out, note) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, w.getEmployeeId());
            setDateParameter(ps, 2, w.getWorkDate());
            setTimeParameter(ps, 3, w.getTimeIn());
            setTimeParameter(ps, 4, w.getTimeOut());
            ps.setString(5, w.getNote());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        w.setWorkId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật bản ghi giờ làm
    public boolean update(WorkTime w) {
        String sql = "UPDATE work_time SET employee_id=?, work_date=?, time_in=?, time_out=?, note=? WHERE work_id=?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, w.getEmployeeId());
            setDateParameter(ps, 2, w.getWorkDate());
            setTimeParameter(ps, 3, w.getTimeIn());
            setTimeParameter(ps, 4, w.getTimeOut());
            ps.setString(5, w.getNote());
            ps.setInt(6, w.getWorkId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa bản ghi giờ làm
    public boolean delete(int workId) {
        String sql = "DELETE FROM work_time WHERE work_id=?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả bản ghi
    public List<WorkTime> getAll() {
        List<WorkTime> list = new ArrayList<>();
        String sql = "SELECT * FROM work_time ORDER BY work_date DESC, time_in";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy bản ghi theo nhân viên
    public List<WorkTime> getByEmployee(int employeeId) {
        List<WorkTime> list = new ArrayList<>();
        String sql = "SELECT * FROM work_time WHERE employee_id=? ORDER BY work_date DESC, time_in";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy bản ghi theo khoảng thời gian
    public List<WorkTime> getByDateRange(LocalDate fromDate, LocalDate toDate) {
        List<WorkTime> list = new ArrayList<>();
        String sql = "SELECT * FROM work_time WHERE work_date BETWEEN ? AND ? ORDER BY work_date DESC, time_in";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setDateParameter(ps, 1, fromDate);
            setDateParameter(ps, 2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy bản ghi theo employee + date range
    public List<WorkTime> getByEmployeeAndDateRange(int employeeId, LocalDate fromDate, LocalDate toDate) {
        List<WorkTime> list = new ArrayList<>();
        String sql = "SELECT * FROM work_time WHERE employee_id=? AND work_date BETWEEN ? AND ? ORDER BY work_date DESC, time_in";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            setDateParameter(ps, 2, fromDate);
            setDateParameter(ps, 3, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy bản ghi theo ID
    public WorkTime getById(int workId) {
        String sql = "SELECT * FROM work_time WHERE work_id=?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra đã có check-in trong ngày không
    public WorkTime getByEmployeeAndDate(int employeeId, LocalDate workDate) {
        String sql = "SELECT * FROM work_time WHERE employee_id=? AND work_date=?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            setDateParameter(ps, 2, workDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper: map ResultSet to WorkTime
    private WorkTime mapResultSet(ResultSet rs) throws SQLException {
        WorkTime w = new WorkTime();
        w.setWorkId(rs.getInt("work_id"));
        w.setEmployeeId(rs.getInt("employee_id"));
        w.setWorkDate(rs.getDate("work_date").toLocalDate());
        
        Time timeIn = rs.getTime("time_in");
        Time timeOut = rs.getTime("time_out");
        w.setTimeIn(timeIn != null ? timeIn.toLocalTime() : null);
        w.setTimeOut(timeOut != null ? timeOut.toLocalTime() : null);
        
        w.setNote(rs.getString("note"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            w.setCreatedAt(createdAt.toLocalDateTime());
        }
        return w;
    }

    // Helper: set time parameter (handle null safely)
    private void setTimeParameter(PreparedStatement ps, int index, LocalTime time) throws SQLException {
        if (time != null) {
            ps.setTime(index, Time.valueOf(time));
        } else {
            ps.setNull(index, java.sql.Types.TIME);
        }
    }

    // Helper: set date parameter
    private void setDateParameter(PreparedStatement ps, int index, LocalDate date) throws SQLException {
        if (date != null) {
            ps.setDate(index, java.sql.Date.valueOf(date));
        } else {
            ps.setNull(index, java.sql.Types.DATE);
        }
    }
}
