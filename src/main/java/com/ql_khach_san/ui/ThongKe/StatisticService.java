package com.ql_khach_san.ui.ThongKe;

import com.ql_khach_san.dao.StatisticDAO;
import com.ql_khach_san.model.Statistic;

import java.util.List;

public class StatisticService {
    private StatisticDAO statisticDAO;

    public StatisticService() {
        this.statisticDAO = new StatisticDAO();
    }

    public boolean addStatistic(Statistic statistic) {
        return statisticDAO.insertStatistic(statistic);
    }

    public boolean updateStatistic(Statistic statistic) {
        return statisticDAO.updateStatistic(statistic);
    }

    public boolean deleteStatistic(int statisticId) {
        return statisticDAO.deleteStatistic(statisticId);
    }

    public Statistic getStatisticById(int statisticId) {
        return statisticDAO.getStatisticById(statisticId);
    }

    public List<Statistic> getAllStatistics() {
        return statisticDAO.getAllStatistics();
    }

    // Có thể bổ sung các phương thức tổng hợp dữ liệu tự động từ các bảng gốc ở đây
}
