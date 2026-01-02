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

public class MonthStatisticFrame extends JFrame {
    private StatisticService service = new com.ql_khach_san.service.StatisticService();
    private ChartPanel chartPanel;
    private DefaultTableModel tableModel;

    public MonthStatisticFrame() {
        setTitle("Thống kê theo tháng");
        setSize(900,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        JComboBox<String> cbMonth = new JComboBox<>();
        for (int m = 1; m <= 12; m++) cbMonth.addItem(String.format("%02d", m));
        JButton btnGen = new JButton("Generate");
        JButton btnSaveAgg = new JButton("Lưu tổng hợp tháng");
        btnSaveAgg.setEnabled(false);
        btnSaveAgg.setToolTipText("Lưu đã bị vô hiệu hoá; hệ thống hiển thị dữ liệu động.");
        control.add(new JLabel("Năm:")); control.add(cbYear);
        control.add(new JLabel("Tháng:")); control.add(cbMonth);
        control.add(btnGen); control.add(btnSaveAgg);

        add(control, BorderLayout.NORTH);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart("Doanh thu theo ngày trong tháng", "Ngày", "Doanh thu", dataset);
        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new String[]{"Ngày","Doanh thu"},0);
        JTable tbl = new JTable(tableModel);
        add(new JScrollPane(tbl), BorderLayout.SOUTH);

        btnGen.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            int m = Integer.parseInt((String)cbMonth.getSelectedItem());
            loadForMonth(y,m);
        });

        btnSaveAgg.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Lưu thống kê đã bị vô hiệu hoá; hệ thống chỉ hiển thị dữ liệu động.");
        });
    }

    private void loadForMonth(int year, int month) {
        List<String[]> data = service.getDailyRevenueForMonth(year, month);
        tableModel.setRowCount(0);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            tableModel.addRow(new Object[]{row[0], Double.parseDouble(row[1])});
            dataset.addValue(Double.parseDouble(row[1]), "Doanh thu", row[0]);
        }
        JFreeChart chart = ChartFactory.createLineChart("Doanh thu theo ngày trong tháng", "Ngày", "Doanh thu", dataset);
        chartPanel.setChart(chart);
    }
}
