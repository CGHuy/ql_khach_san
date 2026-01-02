package com.ql_khach_san.ui.ThongKe;

import com.ql_khach_san.model.Statistic;
import com.ql_khach_san.service.StatisticService;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class StatisticPanel extends JPanel {
    private StatisticService statisticService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnAuto;
    private ChartPanel chartPanel;

    public StatisticPanel() {
        statisticService = new com.ql_khach_san.service.StatisticService();
        setLayout(new BorderLayout());
        initTable();
        initButtons();
        initChart();
        loadData();
    }

    private void initTable() {
        String[] columns = {"ID", "Ngày", "Kỳ", "Doanh thu", "Phòng", "Dịch vụ", "Số khách", "Số phòng", "Số dịch vụ", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initButtons() {
        JPanel panel = new JPanel();
        btnAdd = new JButton("Thêm thủ công");
        btnEdit = new JButton("Sửa ghi chú");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnAuto = new JButton("Tạo thống kê tự động");
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnRefresh);
        panel.add(btnAuto);
        add(panel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditNoteDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
        btnAuto.addActionListener(e -> showAutoDialog());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Statistic> list = statisticService.getAllStatistics();
        for (Statistic s : list) {
            tableModel.addRow(new Object[]{
                s.getStatisticId(),
                s.getStatDate(),
                s.getStatPeriod(),
                s.getRevenue(),
                s.getRoomRevenue(),
                s.getServiceRevenue(),
                s.getCustomerCount(),
                s.getRoomRentedCount(),
                s.getServiceCount(),
                s.getNote()
            });
        }
        updateChart(list);
    }

    private void initChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo ngày",
                "Ngày",
                "Doanh thu",
                dataset
        );
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 250));
        add(chartPanel, BorderLayout.NORTH);
    }

    private void updateChart(List<Statistic> list) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // sắp xếp theo ngày tăng dần để biểu đồ dễ đọc
        list.sort(Comparator.comparing(Statistic::getStatDate));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Statistic s : list) {
            String label = (s.getStatPeriod() != null && !s.getStatPeriod().isEmpty() && !s.getStatPeriod().equals("day"))
                    ? sdf.format(s.getStatDate()) + " (" + s.getStatPeriod() + ")" : sdf.format(s.getStatDate());
            dataset.addValue(s.getRevenue(), "Doanh thu", label);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu",
                "Thời gian",
                "Doanh thu",
                dataset
        );
        chartPanel.setChart(chart);
    }

    private void showAddDialog() {
        // Thêm thủ công (vẫn giữ cho trường hợp đặc biệt)
        StatisticDialog dialog = new StatisticDialog(null, true);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            statisticService.addStatistic(dialog.getStatistic());
            loadData();
        }
    }

    private void showAutoDialog() {
        String period = JOptionPane.showInputDialog(this, "Chọn kỳ thống kê (day/month/year):", "day");
        if (period == null || period.trim().isEmpty()) return;
        period = period.trim().toLowerCase();
        try {
            Statistic autoStat = null;
            if (period.equals("day")) {
                String dateStr = JOptionPane.showInputDialog(this, "Nhập ngày thống kê (yyyy-MM-dd):");
                if (dateStr == null || dateStr.trim().isEmpty()) return;
                java.sql.Date date = java.sql.Date.valueOf(dateStr.trim());
                autoStat = statisticService.generateStatisticByDate(date);
            } else if (period.equals("month")) {
                String ym = JOptionPane.showInputDialog(this, "Nhập tháng (yyyy-MM):");
                if (ym == null || ym.trim().isEmpty()) return;
                String[] parts = ym.trim().split("-");
                if (parts.length < 2) throw new IllegalArgumentException("Định dạng không đúng");
                int y = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                autoStat = statisticService.generateStatisticByMonth(y, m);
            } else if (period.equals("year")) {
                String yStr = JOptionPane.showInputDialog(this, "Nhập năm (yyyy):");
                if (yStr == null || yStr.trim().isEmpty()) return;
                int y = Integer.parseInt(yStr.trim());
                autoStat = statisticService.generateStatisticByYear(y);
            } else {
                JOptionPane.showMessageDialog(this, "Kỳ không hợp lệ: sử dụng day/month/year");
                return;
            }
            if (autoStat == null) return;
            autoStat.setStatPeriod(period);
            // Cho phép nhập ghi chú nếu muốn
            String note = JOptionPane.showInputDialog(this, "Ghi chú (nếu có):");
            autoStat.setNote(note);
            statisticService.addStatistic(autoStat);
            loadData();
            JOptionPane.showMessageDialog(this, "Đã tạo thống kê tự động cho kỳ " + period);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày/Kỳ không hợp lệ hoặc lỗi: " + ex.getMessage());
        }
    }

    private void showEditNoteDialog() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa ghi chú!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Statistic s = statisticService.getStatisticById(id);
        // Sử dụng dialog để sửa (chỉ mở trường ghi chú)
        StatisticDialog dialog = new StatisticDialog(s);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            statisticService.updateStatistic(dialog.getStatistic());
            loadData();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            statisticService.deleteStatistic(id);
            loadData();
        }
    }
}
