
package com.ql_khach_san.service;

import com.ql_khach_san.dao.StatisticDAO;
import com.ql_khach_san.model.Statistic;
import com.ql_khach_san.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Statistic business logic moved to service package
 */
public class StatisticService {
    /**
     * Lấy thống kê số lượng từng loại phòng được sử dụng trong năm
     */
    public List<Object[]> getRoomTypeUsageForYear(int year) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT rt.type_name, COUNT(*) as count " +
                     "FROM checkin ci " +
                     "JOIN reservation r ON ci.reservation_id = r.reservation_id " +
                     "JOIN room rm ON r.room_id = rm.room_id " +
                     "JOIN room_type rt ON rm.type_id = rt.type_id " +
                     "WHERE YEAR(ci.checkin_time) = ? " +
                     "GROUP BY rt.type_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("type_name"), rs.getInt("count")});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy thống kê số lượng từng dịch vụ được sử dụng trong năm
     */
    public List<Object[]> getServiceUsageForYear(int year) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.service_name, SUM(su.quantity) as count " +
                     "FROM service_usage su " +
                     "JOIN service s ON su.service_id = s.service_id " +
                     "WHERE YEAR(su.created_at) = ? " +
                     "GROUP BY s.service_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("service_name"), rs.getInt("count")});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy thống kê số lượng từng loại phòng được đặt (reservation) trong năm
     */
    public List<Object[]> getRoomTypeBookedForYear(int year) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT rt.type_name, COUNT(*) as count " +
                     "FROM reservation r " +
                     "JOIN room rm ON r.room_id = rm.room_id " +
                     "JOIN room_type rt ON rm.type_id = rt.type_id " +
                     "WHERE YEAR(r.booking_date) = ? " +
                     "GROUP BY rt.type_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("type_name"), rs.getInt("count")});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private StatisticDAO statisticDAO;

    public StatisticService() {
        this.statisticDAO = new StatisticDAO();
    }

    public List<Statistic> getAllStatistics() {
        return statisticDAO.getAllStatistics();
    }

    public List<Statistic> getStatisticsByPeriod(String period) {
        return statisticDAO.getStatisticsByPeriod(period);
    }

    public List<String[]> getDailyRevenueForMonth(int year, int month) {
        return statisticDAO.getDailyRevenueForMonth(year, month);
    }

    public List<String[]> getMonthlyRevenueForYear(int year) {
        return statisticDAO.getMonthlyRevenueForYear(year);
    }

    public Statistic generateStatisticByDate(Date date) {
        Statistic statistic = new Statistic();
        statistic.setStatDate(new java.util.Date(date.getTime()));
        statistic.setStatPeriod("day");
        
        try (Connection conn = DBConnection.getConnection()) {
            // Revenue
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE DATE(created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setDate(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            
            // Customer count
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE DATE(ci.checkin_time) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setDate(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            
            // Room count
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE DATE(ci.checkin_time) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setDate(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            
            // Service count
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE DATE(created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setDate(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statistic;
    }

    public Statistic generateStatisticByMonth(int year, int month) {
        Statistic statistic = new Statistic();
        statistic.setStatPeriod("month");
        statistic.setStatDate(Date.valueOf(String.format("%04d-%02d-01", year, month)));
        
        try (Connection conn = DBConnection.getConnection()) {
            // Revenue
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            
            // Customer count
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE YEAR(ci.checkin_time) = ? AND MONTH(ci.checkin_time) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            
            // Room count
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(ci.checkin_time) = ? AND MONTH(ci.checkin_time) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            
            // Service count
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statistic;
    }

    public Statistic generateStatisticByYear(int year) {
        Statistic statistic = new Statistic();
        statistic.setStatPeriod("year");
        statistic.setStatDate(Date.valueOf(String.format("%04d-01-01", year)));
        
        try (Connection conn = DBConnection.getConnection()) {
            // Revenue
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE YEAR(created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            
            // Customer count
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE YEAR(ci.checkin_time) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            
            // Room count
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(ci.checkin_time) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            
            // Service count
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE YEAR(created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statistic;
    }

    /**
     * Get nearest N days with revenue around a target date (for comparison charts)
     */
    public List<String[]> getNearestDaysRevenue(Date targetDate, int n) {
        return statisticDAO.getNearestDaysRevenue(targetDate, n);
    }

    /**
     * Compute daily statistics for a range ending at endDate for 'days' days (inclusive)
     */
    public List<Statistic> computeDailyStats(Date endDate, int days) {
        List<Statistic> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        
        // iterate backward to produce ascending-order list
        for (int i = days - 1; i >= 0; i--) {
            Calendar c = (Calendar) cal.clone();
            c.add(Calendar.DATE, -i);
            Date d = new Date(c.getTimeInMillis());
            Statistic s = generateStatisticByDate(d);
            if (s != null) {
                s.setStatDate(new java.util.Date(d.getTime()));
                list.add(s);
            }
        }
        return list;
    }
}