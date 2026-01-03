package com.ql_khach_san.ui.ThongKe;

import com.ql_khach_san.service.StatisticService;
import com.ql_khach_san.model.Statistic;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
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
        // Only choose year; compare months in the selected year
        JButton btnGen = new JButton("Xem năm");
        control.add(new JLabel("Năm:")); control.add(cbYear);
        control.add(btnGen);

        add(control, BorderLayout.NORTH);

        // Initial empty chart (will show monthly comparison after selecting year)
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart("Doanh thu theo tháng trong năm", "Tháng", "Doanh thu", dataset);
        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new String[]{"Tháng","Doanh thu"},0);
        JTable tbl = new JTable(tableModel);
        add(new JScrollPane(tbl), BorderLayout.SOUTH);

        btnGen.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadForYear(y);
        });


    }

    private void loadForYear(int year) {
        java.util.List<String[]> data = service.getMonthlyRevenueForYear(year);
        tableModel.setRowCount(0);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DecimalFormat df = new DecimalFormat("#,##0");
        for (String[] row : data) {
            // row[0] formatted as yyyy-MM; show MM label
            String label = row[0].length() >= 7 ? row[0].substring(5) : row[0];
            double val = 0.0;
            try { val = Double.parseDouble(row[1]); } catch (Exception ex) { val = 0.0; }
            tableModel.addRow(new Object[]{label, df.format(val)});
            dataset.addValue(val, "Doanh thu", label);
        }
        JFreeChart chart = ChartFactory.createBarChart("Doanh thu theo tháng trong năm " + year, "Tháng", "Doanh thu", dataset);
        try {
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setNumberFormatOverride(new DecimalFormat("#,##0"));
        } catch (Exception ex) {}
        chartPanel.setChart(chart);
    }
}
