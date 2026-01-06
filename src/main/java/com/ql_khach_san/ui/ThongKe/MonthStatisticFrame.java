package com.ql_khach_san.ui.ThongKe;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Calendar;

public class MonthStatisticFrame extends JFrame {
    private StatisticService service = new StatisticService();
    private ChartPanel chartPanel;
    private ChartPanel pieRoomBookedPanel;
    private ChartPanel pieServicePanel;
    private DefaultTableModel tableModel;

    public MonthStatisticFrame() {
        setTitle("Thống kê theo tháng");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        JComboBox<String> cbMonth = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        for (int m = 1; m <= 12; m++) cbMonth.addItem(String.format("%02d", m));

        JButton btnGen = new JButton("Xem tháng");
        control.add(new JLabel("Năm:")); control.add(cbYear);
        control.add(new JLabel("Tháng:")); control.add(cbMonth);
        control.add(btnGen);
        add(control, BorderLayout.NORTH);

        // Initialize datasets
        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        DefaultPieDataset pieRoomBookedDataset = new DefaultPieDataset();
        DefaultPieDataset pieServiceDataset = new DefaultPieDataset();

        // Chart panels
        chartPanel = new ChartPanel(ChartFactory.createBarChart("Doanh thu theo ngày trong tháng", "Ngày", "Doanh thu", revenueDataset));
        chartPanel.setPreferredSize(new Dimension(1180, 240));
        formatChart(chartPanel);

        pieRoomBookedPanel = new ChartPanel(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false));
        pieRoomBookedPanel.setPreferredSize(new Dimension(575, 190));

        pieServicePanel = new ChartPanel(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false));
        pieServicePanel.setPreferredSize(new Dimension(575, 190));

        JPanel chartsPanel = new JPanel(new BorderLayout(5,5));
        chartsPanel.add(chartPanel, BorderLayout.NORTH);
        JPanel lower = new JPanel(new GridLayout(1, 2, 5, 5));
        lower.add(pieRoomBookedPanel);
        lower.add(pieServicePanel);
        chartsPanel.add(lower, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        wrapper.add(chartsPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Ngày","Doanh thu"}, 0);
        JTable tbl = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tbl);
        scrollPane.setPreferredSize(new Dimension(1180, 150));
        add(scrollPane, BorderLayout.SOUTH);

        btnGen.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            int m = Integer.parseInt((String)cbMonth.getSelectedItem());
            loadForMonth(y, m);
        });

        // Initial load: current month
        int curY = Integer.parseInt((String)cbYear.getSelectedItem());
        int curM = Integer.parseInt((String)cbMonth.getSelectedItem());
        loadForMonth(curY, curM);
    }

    private void formatChart(ChartPanel panel) {
        if (panel.getChart() != null && panel.getChart().getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) panel.getChart().getPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setNumberFormatOverride(new DecimalFormat("#,##0"));
        }
    }



    private String formatNumber(int n) {
        try { return new DecimalFormat("#,##0").format(n); } catch (Exception ex) { return String.valueOf(n); }
    }
    private void loadForMonth(int year, int month) {
        // Clear and populate
        tableModel.setRowCount(0);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DecimalFormat df = new DecimalFormat("#,##0");

        // Daily revenue
        List<String[]> daily = service.getDailyRevenueForMonth(year, month);
        for (String[] r : daily) {
            String dayLabel = r[0].length() >= 10 ? r[0].substring(8) : r[0];
            double val = 0.0;
            try { val = Double.parseDouble(r[1]); } catch (Exception ex) { }
            tableModel.addRow(new Object[]{dayLabel, df.format(val)});
            dataset.addValue(val, "Doanh thu", dayLabel);
        }
        chartPanel.setChart(ChartFactory.createBarChart("Doanh thu theo ngày trong tháng " + String.format("%04d-%02d", year, month), "Ngày", "Doanh thu", dataset));
        formatChart(chartPanel);

        // Room types booked distribution
        List<Object[]> roomBookedStats = service.getRoomTypeBookedForMonth(year, month);
        DefaultPieDataset pieRoomBooked = new DefaultPieDataset();
        for (Object[] row : roomBookedStats) {
            String type = String.valueOf(row[0]);
            int count = 0; try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { }
            pieRoomBooked.setValue(type, count);
        }
        pieRoomBookedPanel.setChart(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBooked, true, true, false));

        // Service usage in month
        DefaultPieDataset pieService = new DefaultPieDataset();
        List<Object[]> serviceStats = service.getServiceUsageForMonth(year, month);
        for (Object[] row : serviceStats) {
            String name = String.valueOf(row[0]);
            int count = 0; try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { }
            pieService.setValue(name, count);
        }
        pieServicePanel.setChart(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieService, true, true, false));
    }
}
