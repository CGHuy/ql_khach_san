package com.ql_khach_san.ui.ThongKe;

import com.ql_khach_san.service.StatisticService;
import com.ql_khach_san.model.Statistic;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class YearStatisticFrame extends JFrame {
    private StatisticService service = new com.ql_khach_san.service.StatisticService();
    private ChartPanel chartPanel;
    private DefaultTableModel tableModel;
    private boolean chartIsBar = true; // true = bar, false = line
    private boolean viewingSaved = false; // whether current table shows saved stats (allows edit/delete)

    public YearStatisticFrame() {
        setTitle("Thống kê theo năm");
        setSize(900,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel control = new JPanel(new BorderLayout());
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        JButton btnGen = new JButton("Generate");
        JButton btnSaveAgg = new JButton("Lưu tổng hợp năm");
        JButton btnViewSaved = new JButton("Xem thống kê đã lưu");
        leftControls.add(new JLabel("Năm:")); leftControls.add(cbYear);
        leftControls.add(btnGen); leftControls.add(btnSaveAgg); leftControls.add(btnViewSaved);
        control.add(leftControls, BorderLayout.WEST);

        JButton btnChartType = new JButton("Đổi biểu đồ");
        control.add(btnChartType, BorderLayout.EAST);

        // top container with header + chart
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(control, BorderLayout.NORTH);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart("Doanh thu theo tháng trong năm", "Tháng", "Doanh thu", dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800,300));
        topContainer.add(chartPanel, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Tháng","Doanh thu"},0);
        JTable tbl = new JTable(tableModel);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm thủ công");
        JButton btnEdit = new JButton("Sửa ghi chú");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");
        footer.add(btnAdd); footer.add(btnEdit); footer.add(btnDelete); footer.add(btnRefresh);
        add(footer, BorderLayout.SOUTH);

        // actions
        btnGen.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadForYear(y);
        });

        btnSaveAgg.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            Statistic stat = service.generateStatisticByYear(y);
            stat.setStatPeriod("year");
            stat.setNote("Tổng hợp năm " + y);
            java.sql.Date sqlDate = java.sql.Date.valueOf(String.format("%04d-01-01", y));
            boolean exists = service.existsStatistic(sqlDate, "year");
            if (exists) {
                int opt = JOptionPane.showConfirmDialog(this, "Đã tồn tại thống kê năm này. Ghi đè?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (opt != JOptionPane.YES_OPTION) return;
                service.saveStatistic(stat, true);
                JOptionPane.showMessageDialog(this, "Đã ghi đè thống kê năm");
            } else {
                service.saveStatistic(stat, false);
                JOptionPane.showMessageDialog(this, "Đã lưu thống kê năm");
            }
        });

        btnViewSaved.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadSavedStatsForYear(y);
        });

        btnRefresh.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadForYear(y);
        });

        btnChartType.addActionListener(e -> {
            chartIsBar = !chartIsBar;
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            if (viewingSaved) loadSavedStatsForYear(y); else loadForYear(y);
        });

        btnAdd.addActionListener(e -> {
            // open dialog to add manual statistic for the year
            StatisticDialog dialog = new StatisticDialog(null, true);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                Statistic s = dialog.getStatistic();
                s.setStatPeriod("year");
                service.saveStatistic(s, false);
                loadSavedStatsForYear(Integer.parseInt((String)cbYear.getSelectedItem()));
            }
        });

        btnEdit.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa ghi chú!"); return; }
            // Expecting saved-stats view; id stored in table hidden column? we'll store id in model by replacing first column
            Object idObj = tbl.getValueAt(row, 0);
            if (idObj == null) { JOptionPane.showMessageDialog(this, "Không có bản ghi để sửa ở chế độ này."); return; }
            int id = Integer.parseInt(idObj.toString());
            Statistic s = service.getStatisticById(id);
            StatisticDialog dialog = new StatisticDialog(s);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                service.updateStatistic(dialog.getStatistic());
                loadSavedStatsForYear(Integer.parseInt((String)cbYear.getSelectedItem()));
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!"); return; }
            Object idObj = tbl.getValueAt(row, 0);
            if (idObj == null) { JOptionPane.showMessageDialog(this, "Không có bản ghi để xóa ở chế độ này."); return; }
            int id = Integer.parseInt(idObj.toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.deleteStatistic(id);
                loadSavedStatsForYear(Integer.parseInt((String)cbYear.getSelectedItem()));
            }
        });
    }

    private void loadForYear(int year) {
        viewingSaved = false;
        List<String[]> data = service.getMonthlyRevenueForYear(year);
        // Ensure table columns are Month, Revenue
        tableModel.setColumnIdentifiers(new String[]{"Tháng","Doanh thu"});
        tableModel.setRowCount(0);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            tableModel.addRow(new Object[]{row[0], Double.parseDouble(row[1])});
            dataset.addValue(Double.parseDouble(row[1]), "Doanh thu", row[0]);
        }
        JFreeChart chart = chartIsBar ? ChartFactory.createBarChart("Doanh thu theo tháng trong năm", "Tháng", "Doanh thu", dataset)
                : ChartFactory.createLineChart("Doanh thu theo tháng trong năm", "Tháng", "Doanh thu", dataset);
        chartPanel.setChart(chart);
    }

    private void loadSavedStatsForYear(int year) {
        // Load saved statistics for this year and show in table: ID | Date | Period | Revenue | note
        java.util.List<com.ql_khach_san.model.Statistic> list = service.getStatisticsByPeriod("year");
        tableModel.setRowCount(0);
        // change table to columns: ID, Date, Period, Revenue, Note
        tableModel.setColumnIdentifiers(new String[]{"ID", "Ngày", "Kỳ", "Doanh thu", "Ghi chú"});
        for (com.ql_khach_san.model.Statistic s : list) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(s.getStatDate());
            int y = cal.get(java.util.Calendar.YEAR);
            if (y == year) {
                tableModel.addRow(new Object[]{s.getStatisticId(), s.getStatDate(), s.getStatPeriod(), s.getRevenue(), s.getNote()});
            }
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String label = tableModel.getValueAt(i, 1).toString();
            double val = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
            dataset.addValue(val, "Doanh thu", label);
        }
        JFreeChart chart = ChartFactory.createBarChart("Thống kê đã lưu - năm " + year, "Ngày", "Doanh thu", dataset);
        chartPanel.setChart(chart);
    }
}
