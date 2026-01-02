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
        btnSaveAgg.setEnabled(false);
        btnSaveAgg.setToolTipText("Lưu đã bị vô hiệu hoá; hệ thống hiển thị dữ liệu động.");
        JButton btnViewSaved = new JButton("Xem thống kê đã lưu");
        btnViewSaved.setEnabled(false);
        btnViewSaved.setToolTipText("Xem đã lưu đã bị vô hiệu hoá; hệ thống hiển thị dữ liệu động.");
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



        // actions
        btnGen.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadForYear(y);
        });

        btnSaveAgg.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Lưu thống kê đã bị vô hiệu hoá; hệ thống chỉ hiển thị dữ liệu động.");
        });

        btnViewSaved.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Xem thống kê đã lưu đã bị vô hiệu hoá; hệ thống chỉ hiển thị dữ liệu động.");
        });



        btnChartType.addActionListener(e -> {
            chartIsBar = !chartIsBar;
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            if (viewingSaved) loadSavedStatsForYear(y); else loadForYear(y);
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
        // change table to columns: ID, Date, Revenue, Note
        tableModel.setColumnIdentifiers(new String[]{"ID", "Ngày", "Doanh thu", "Ghi chú"});
        for (com.ql_khach_san.model.Statistic s : list) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(s.getStatDate());
            int y = cal.get(java.util.Calendar.YEAR);
            if (y == year) {
                tableModel.addRow(new Object[]{s.getStatisticId(), s.getStatDate(), s.getRevenue(), s.getNote()});
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
