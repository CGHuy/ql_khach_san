
package com.ql_khach_san.ui.ThongKe;

import com.ql_khach_san.config.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Service layer for statistics - queries data dynamically from invoice, checkin, reservation, service_usage tables
 */
public class StatisticService {

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





    public List<String[]> getDailyRevenueForMonth(int year, int month) {
        // Return all days of the month (fill zeros for days without invoices)
        List<String[]> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // compute start (inclusive) and end (exclusive)
        java.sql.Timestamp start = new java.sql.Timestamp(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, daysInMonth);
        java.sql.Timestamp end = new java.sql.Timestamp(cal.getTimeInMillis());

        String sql = "SELECT DATE(created_at) as d, SUM(total_amount) as revenue FROM invoice WHERE created_at >= ? AND created_at < ? GROUP BY DATE(created_at)";
        Map<String, Double> map = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("d");
                    map.put(d.toString(), rs.getDouble("revenue"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // fill all days
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            java.sql.Date d = new java.sql.Date(cal.getTimeInMillis());
            double rev = map.getOrDefault(d.toString(), 0.0);
            list.add(new String[] { d.toString(), String.valueOf(rev) });
        }
        return list;
    }

    public List<String[]> getMonthlyRevenueForYear(int year) {
        // Return 12 months with zero fill for missing months
        List<String[]> list = new ArrayList<>();
        // compute start (inclusive) and end (exclusive) for the year
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.YEAR, year);
        calStart.set(Calendar.MONTH, Calendar.JANUARY);
        calStart.set(Calendar.DAY_OF_MONTH, 1);
        java.sql.Timestamp start = new java.sql.Timestamp(calStart.getTimeInMillis());
        calStart.add(Calendar.YEAR, 1);
        java.sql.Timestamp end = new java.sql.Timestamp(calStart.getTimeInMillis());

        String sql = "SELECT MONTH(created_at) as m, SUM(total_amount) as revenue FROM invoice WHERE created_at >= ? AND created_at < ? GROUP BY MONTH(created_at)";
        Map<Integer, Double> map = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("m"), rs.getDouble("revenue"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int m = 1; m <= 12; m++) {
            String label = String.format("%04d-%02d", year, m);
            double rev = map.getOrDefault(m, 0.0);
            list.add(new String[] { label, String.valueOf(rev) });
        }
        return list;
    }

    public Statistic generateStatisticByDate(java.sql.Date date) {
        Statistic statistic = new Statistic();
        statistic.setStatDate(new java.util.Date(date.getTime()));
        statistic.setStatPeriod("day");
        
        // compute half-open day range [date, date+1)
        java.sql.Timestamp start = new java.sql.Timestamp(date.getTime());
        java.sql.Timestamp end = new java.sql.Timestamp(date.getTime() + 24L * 3600L * 1000L);

        try (Connection conn = DBConnection.getConnection()) {
            // Revenue (use half-open range to include time part correctly)
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE created_at >= ? AND created_at < ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setTimestamp(1, start);
                ps.setTimestamp(2, end);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            // Fallback: if no invoice with created_at in the day, try summing invoices for checkins that happened that day
            if (statistic.getRevenue() == 0.0) {
                String sqlFallbackInv = "SELECT SUM(inv.total_amount) AS total_revenue FROM invoice inv JOIN checkin ci ON inv.checkin_id = ci.checkin_id WHERE ci.checkin_time >= ? AND ci.checkin_time < ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlFallbackInv)) {
                    ps.setTimestamp(1, start);
                    ps.setTimestamp(2, end);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            double val = rs.getDouble("total_revenue");
                            statistic.setRevenue(val);
                            // conservatively set room/service revenue unknown (leave as 0) unless breakdown available
                        }
                    }
                }
            }
            
            // Customer count
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE ci.checkin_time >= ? AND ci.checkin_time < ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setTimestamp(1, start);
                ps.setTimestamp(2, end);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            
            // Room count
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE ci.checkin_time >= ? AND ci.checkin_time < ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setTimestamp(1, start);
                ps.setTimestamp(2, end);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            
            // Service count
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE created_at >= ? AND created_at < ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setTimestamp(1, start);
                ps.setTimestamp(2, end);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
            // Fallback: if no service_usage in that created_at range, try summing usages tied to checkins of that day
            if (statistic.getServiceCount() == 0) {
                String sqlFallbackSu = "SELECT SUM(su.quantity) AS service_count FROM service_usage su JOIN checkin ci ON su.checkin_id = ci.checkin_id WHERE ci.checkin_time >= ? AND ci.checkin_time < ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlFallbackSu)) {
                    ps.setTimestamp(1, start);
                    ps.setTimestamp(2, end);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            statistic.setServiceCount(rs.getInt("service_count"));
                        }
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
        statistic.setStatDate(java.sql.Date.valueOf(String.format("%04d-%02d-01", year, month)));
        
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
        statistic.setStatDate(java.sql.Date.valueOf(String.format("%04d-01-01", year)));
        
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


    public List<Object[]> getRoomTypeBookedForMonth(int year, int month) {
        // Use booking_date range to avoid DATE() and include full days
        List<Object[]> list = new ArrayList<>();
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.YEAR, year);
        calStart.set(Calendar.MONTH, month - 1);
        calStart.set(Calendar.DAY_OF_MONTH, 1);
        java.sql.Timestamp start = new java.sql.Timestamp(calStart.getTimeInMillis());
        calStart.add(Calendar.MONTH, 1);
        java.sql.Timestamp end = new java.sql.Timestamp(calStart.getTimeInMillis());

        String sql = "SELECT rt.type_name, COUNT(*) as count " +
                     "FROM reservation r " +
                     "JOIN room rm ON r.room_id = rm.room_id " +
                     "JOIN room_type rt ON rm.type_id = rt.type_id " +
                     "WHERE r.booking_date >= ? AND r.booking_date < ? " +
                     "GROUP BY rt.type_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
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

    public List<Object[]> getServiceUsageForMonth(int year, int month) {
        List<Object[]> list = new ArrayList<>();
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.YEAR, year);
        calStart.set(Calendar.MONTH, month - 1);
        calStart.set(Calendar.DAY_OF_MONTH, 1);
        java.sql.Timestamp start = new java.sql.Timestamp(calStart.getTimeInMillis());
        calStart.add(Calendar.MONTH, 1);
        java.sql.Timestamp end = new java.sql.Timestamp(calStart.getTimeInMillis());

        String sql = "SELECT s.service_name, SUM(su.quantity) as count " +
                     "FROM service_usage su " +
                     "JOIN service s ON su.service_id = s.service_id " +
                     "WHERE su.created_at >= ? AND su.created_at < ? " +
                     "GROUP BY s.service_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
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
     * Get nearest N days with revenue around a target date (for comparison charts)
     */
    public List<String[]> getNearestDaysRevenue(java.sql.Date targetDate, int n) {
        // Return the last n consecutive days ending at targetDate (fill zeros for missing days)
        List<String[]> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(targetDate);
        cal.add(Calendar.DATE, - (n - 1)); // start date
        java.sql.Timestamp start = new java.sql.Timestamp(cal.getTimeInMillis());
        cal.setTime(targetDate);
        cal.add(Calendar.DATE, 1);
        java.sql.Timestamp end = new java.sql.Timestamp(cal.getTimeInMillis());

        String sql = "SELECT DATE(created_at) as d, SUM(total_amount) as revenue FROM invoice WHERE created_at >= ? AND created_at < ? GROUP BY DATE(created_at)";
        Map<String, Double> map = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("d");
                    map.put(d.toString(), rs.getDouble("revenue"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // fill consecutive days from start to targetDate
        cal.setTimeInMillis(start.getTime());
        for (int i = 0; i < n; i++) {
            java.sql.Date d = new java.sql.Date(cal.getTimeInMillis());
            double rev = map.getOrDefault(d.toString(), 0.0);
            list.add(new String[] { d.toString(), String.valueOf(rev) });
            cal.add(Calendar.DATE, 1);
        }

        return list;
    }

    /**
     * Compute daily statistics for a range ending at endDate for 'days' days (inclusive)
     */
    public List<Statistic> computeDailyStats(java.util.Date endDate, int days) {
        List<Statistic> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        
        // iterate backward to produce ascending-order list
        for (int i = days - 1; i >= 0; i--) {
            Calendar c = (Calendar) cal.clone();
            c.add(Calendar.DATE, -i);
            java.sql.Date d = new java.sql.Date(c.getTimeInMillis());
            Statistic s = generateStatisticByDate(d);
            if (s != null) {
                s.setStatDate(new java.util.Date(d.getTime()));
                list.add(s);
            }
        }
        return list;
    }
}