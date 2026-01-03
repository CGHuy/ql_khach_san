
package com.ql_khach_san.service;

import com.ql_khach_san.dao.StatisticDAO;
import com.ql_khach_san.model.Statistic;

import java.util.List;

/**
 * Statistic business logic moved to service package
 */
public class StatisticService {
        /**
         * Lấy thống kê số lượng từng loại phòng được sử dụng trong năm
         */
        public java.util.List<Object[]> getRoomTypeUsageForYear(int year) {
            java.util.List<Object[]> list = new java.util.ArrayList<>();
            String sql = "SELECT rt.type_name, COUNT(*) as count " +
                         "FROM checkin ci " +
                         "JOIN reservation r ON ci.reservation_id = r.reservation_id " +
                         "JOIN room rm ON r.room_id = rm.room_id " +
                         "JOIN room_type rt ON rm.type_id = rt.type_id " +
                         "WHERE YEAR(ci.checkin_time) = ? " +
                         "GROUP BY rt.type_name";
            try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
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
        public java.util.List<Object[]> getServiceUsageForYear(int year) {
            java.util.List<Object[]> list = new java.util.ArrayList<>();
            String sql = "SELECT s.service_name, SUM(su.quantity) as count " +
                         "FROM service_usage su " +
                         "JOIN service s ON su.service_id = s.service_id " +
                         "WHERE YEAR(su.created_at) = ? " +
                         "GROUP BY s.service_name";
            try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
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
    public java.util.List<Object[]> getRoomTypeBookedForYear(int year) {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT rt.type_name, COUNT(*) as count " +
                     "FROM reservation r " +
                     "JOIN room rm ON r.room_id = rm.room_id " +
                     "JOIN room_type rt ON rm.type_id = rt.type_id " +
                     "WHERE YEAR(r.booking_date) = ? " +
                     "GROUP BY rt.type_name";
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
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

    @Deprecated
    public boolean addStatistic(Statistic statistic) {
        throw new UnsupportedOperationException("Manual add is disabled; statistics are computed dynamically.");
    }

    @Deprecated
    public boolean updateStatistic(Statistic statistic) {
        throw new UnsupportedOperationException("Manual update is disabled; statistics are computed dynamically.");
    }

    @Deprecated
    public boolean deleteStatistic(int statisticId) {
        throw new UnsupportedOperationException("Manual delete is disabled; statistics are computed dynamically.");
    }

    @Deprecated
    public Statistic getStatisticById(int statisticId) {
        throw new UnsupportedOperationException("Fetching saved statistics by ID is deprecated in live-only mode.");
    }

    public List<Statistic> getAllStatistics() {
        return statisticDAO.getAllStatistics();
    }

    public List<Statistic> getStatisticsByPeriod(String period) {
        return statisticDAO.getStatisticsByPeriod(period);
    }

    public java.util.List<String[]> getDailyRevenueForMonth(int year, int month) {
        return statisticDAO.getDailyRevenueForMonth(year, month);
    }

    public java.util.List<String[]> getMonthlyRevenueForYear(int year) {
        return statisticDAO.getMonthlyRevenueForYear(year);
    }

    public Statistic generateStatisticByDate(java.sql.Date date) {
        Statistic statistic = new Statistic();
        statistic.setStatDate(new java.util.Date(date.getTime()));
        statistic.setStatPeriod("day");
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection()) {
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE DATE(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE DATE(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int cnt = rs.getInt("customer_count");
                        statistic.setCustomerCount(cnt);
                    }
                }
            }
            // Fallback: if no checkin-based customers found, infer from invoices for the date
            if (statistic.getCustomerCount() == 0) {
                String sqlCustomerFromInvoice = "SELECT COUNT(DISTINCT r.customer_id) AS customer_count FROM invoice i JOIN checkin ci ON i.checkin_id = ci.checkin_id JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE DATE(i.created_at) = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomerFromInvoice)) {
                    ps.setDate(1, date);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int cnt2 = rs.getInt("customer_count");
                            if (cnt2 > 0) {
                                statistic.setCustomerCount(cnt2);
                                System.out.println("[StatisticService] Fallback customer_count from invoice used for date=" + date + ": " + cnt2);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // room_id is stored on reservation, so join reservation to get room_id
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE DATE(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            // Fallback: infer from invoices linked to reservations
            if (statistic.getRoomRentedCount() == 0) {
                String sqlRoomFromInvoice = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM invoice i JOIN checkin ci ON i.checkin_id = ci.checkin_id JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE DATE(i.created_at) = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoomFromInvoice)) {
                    ps.setDate(1, date);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int cnt2 = rs.getInt("room_rented_count");
                            if (cnt2 > 0) {
                                statistic.setRoomRentedCount(cnt2);
                                System.out.println("[StatisticService] Fallback room_rented_count from invoice used for date=" + date + ": " + cnt2);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE DATE(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
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
        statistic.setStatDate(java.sql.Date.valueOf(String.format("%04d-%02d-01", year, month)));
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection()) {
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE YEAR(ci.checkin_time) = ? AND MONTH(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int cnt = rs.getInt("customer_count");
                        statistic.setCustomerCount(cnt);
                    }
                }
            }
            // Fallback: infer from invoices within month if checkin-based count is zero
            if (statistic.getCustomerCount() == 0) {
                String sqlCustomerFromInvoice = "SELECT COUNT(DISTINCT r.customer_id) AS customer_count FROM invoice i JOIN checkin ci ON i.checkin_id = ci.checkin_id JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(i.created_at)=? AND MONTH(i.created_at)=?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomerFromInvoice)) {
                    ps.setInt(1, year);
                    ps.setInt(2, month);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int cnt2 = rs.getInt("customer_count");
                            if (cnt2 > 0) {
                                statistic.setCustomerCount(cnt2);
                                System.out.println("[StatisticService] Fallback customer_count from invoice used for month=" + year + "-" + month + ": " + cnt2);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // room_id is on reservation, join to get room_id
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(ci.checkin_time) = ? AND MONTH(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            // Fallback: infer from invoices linked to reservations
            if (statistic.getRoomRentedCount() == 0) {
                String sqlRoomFromInvoice = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM invoice i JOIN checkin ci ON i.checkin_id = ci.checkin_id JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(i.created_at)=? AND MONTH(i.created_at)=?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoomFromInvoice)) {
                    ps.setInt(1, year);
                    ps.setInt(2, month);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int cnt2 = rs.getInt("room_rented_count");
                            if (cnt2 > 0) {
                                statistic.setRoomRentedCount(cnt2);
                                System.out.println("[StatisticService] Fallback room_rented_count from invoice used for month=" + year + "-" + month + ": " + cnt2);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
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
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection()) {
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE YEAR(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE YEAR(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int cnt = rs.getInt("customer_count");
                        statistic.setCustomerCount(cnt);
                    }
                }
            }
            // Fallback: infer from invoices in the year
            if (statistic.getCustomerCount() == 0) {
                String sqlCustomerFromInvoice = "SELECT COUNT(DISTINCT r.customer_id) AS customer_count FROM invoice i JOIN checkin ci ON i.checkin_id = ci.checkin_id JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(i.created_at)=?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomerFromInvoice)) {
                    ps.setInt(1, year);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int cnt2 = rs.getInt("customer_count");
                            if (cnt2 > 0) {
                                statistic.setCustomerCount(cnt2);
                                System.out.println("[StatisticService] Fallback customer_count from invoice used for year=" + year + ": " + cnt2);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // room_id is on reservation, join to get room_id
            String sqlRoom = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            // Fallback: infer from invoices linked to reservations
            if (statistic.getRoomRentedCount() == 0) {
                String sqlRoomFromInvoice = "SELECT COUNT(DISTINCT r.room_id) AS room_rented_count FROM invoice i JOIN checkin ci ON i.checkin_id = ci.checkin_id JOIN reservation r ON ci.reservation_id = r.reservation_id WHERE YEAR(i.created_at)=?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoomFromInvoice)) {
                    ps.setInt(1, year);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int cnt2 = rs.getInt("room_rented_count");
                            if (cnt2 > 0) {
                                statistic.setRoomRentedCount(cnt2);
                                System.out.println("[StatisticService] Fallback room_rented_count from invoice used for year=" + year + ": " + cnt2);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE YEAR(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
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

    // Check existence by date/period
    @Deprecated
    public boolean existsStatistic(java.sql.Date date, String period) {
        throw new UnsupportedOperationException("Checking saved statistics is deprecated in live-only mode.");
    }

    @Deprecated
    /**
     * Save statistic with optional overwrite if exists - deprecated in live-only mode
     */
    public boolean saveStatistic(Statistic stat, boolean overwrite) {
        throw new UnsupportedOperationException("Manual save is disabled; statistics are computed dynamically.");
    }

    /**
     * Get nearest N days with revenue around a target date (for comparison charts)
     */
    public java.util.List<String[]> getNearestDaysRevenue(java.sql.Date targetDate, int n) {
        return statisticDAO.getNearestDaysRevenue(targetDate, n);
    }

    /**
     * Compute daily statistics for a range ending at endDate for 'days' days (inclusive)
     */
    public java.util.List<Statistic> computeDailyStats(java.sql.Date endDate, int days) {
        java.util.List<Statistic> list = new java.util.ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(endDate);
        // iterate backward to produce ascending-order list
        for (int i = days - 1; i >= 0; i--) {
            java.util.Calendar c = (java.util.Calendar) cal.clone();
            c.add(java.util.Calendar.DATE, -i);
            java.sql.Date d = new java.sql.Date(c.getTimeInMillis());
            Statistic s = generateStatisticByDate(d);
            if (s != null) {
                // ensure statDate set properly
                s.setStatDate(new java.util.Date(d.getTime()));
                list.add(s);
            }
        }
        return list;
    }
}