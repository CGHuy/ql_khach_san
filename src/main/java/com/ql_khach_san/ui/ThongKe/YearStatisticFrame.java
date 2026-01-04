package com.ql_khach_san.ui.ThongKe;


import com.ql_khach_san.service.StatisticService;
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
import java.text.SimpleDateFormat;
import java.util.List;


public class YearStatisticFrame extends JFrame {
        private ChartPanel pieRoomBookedPanel;
    private StatisticService service = new com.ql_khach_san.service.StatisticService();
    private ChartPanel chartPanel;
    private ChartPanel pieRoomPanel;
    private ChartPanel pieServicePanel;
    private DefaultTableModel tableModel;

    public YearStatisticFrame() {
        setTitle("Thống kê theo năm");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        JButton btnGen = new JButton("Xem năm");
        control.add(new JLabel("Năm:")); control.add(cbYear);
        control.add(btnGen);
        add(control, BorderLayout.NORTH);

        // Main chart: Doanh thu theo tháng
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart("Doanh thu theo tháng trong năm", "Tháng", "Doanh thu", dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1150, 320)); // slightly taller to fit grid layout comfortably

        // Pie chart for room types
        DefaultPieDataset pieRoomDataset = new DefaultPieDataset();
        JFreeChart pieRoomChart = ChartFactory.createPieChart("Tỉ lệ loại phòng được sử dụng", pieRoomDataset, true, true, false);
        pieRoomPanel = new ChartPanel(pieRoomChart);
        pieRoomPanel.setPreferredSize(new Dimension(550, 300));

        // Pie chart for room types booked (được đặt)
        DefaultPieDataset pieRoomBookedDataset = new DefaultPieDataset();
        JFreeChart pieRoomBookedChart = ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false);
        pieRoomBookedPanel = new ChartPanel(pieRoomBookedChart);
        pieRoomBookedPanel.setPreferredSize(new Dimension(550, 300));

        // Pie chart for services
        DefaultPieDataset pieServiceDataset = new DefaultPieDataset();
        JFreeChart pieServiceChart = ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false);
        pieServicePanel = new ChartPanel(pieServiceChart);
        pieServicePanel.setPreferredSize(new Dimension(550, 300));

        // Panel to hold all charts in a 2x2 grid for balanced layout
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JPanel pnlChartMain = new JPanel(new BorderLayout());
        pnlChartMain.add(chartPanel, BorderLayout.CENTER);
        chartsPanel.add(pnlChartMain);

        JPanel pnlPie1 = new JPanel(new BorderLayout());
        pnlPie1.add(pieRoomPanel, BorderLayout.CENTER);
        chartsPanel.add(pnlPie1);

        JPanel pnlPie2 = new JPanel(new BorderLayout());
        pnlPie2.add(pieRoomBookedPanel, BorderLayout.CENTER);
        chartsPanel.add(pnlPie2);

        JPanel pnlPie3 = new JPanel(new BorderLayout());
        pnlPie3.add(pieServicePanel, BorderLayout.CENTER);
        chartsPanel.add(pnlPie3);

        // Add some padding around charts
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        wrapper.add(chartsPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Table for doanh thu
        tableModel = new DefaultTableModel(new String[]{"Tháng","Doanh thu"},0);
        JTable tbl = new JTable(tableModel);
        add(new JScrollPane(tbl), BorderLayout.SOUTH);

        btnGen.addActionListener(e -> {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadForYear(y);
        });

        // Initial load
        if (cbYear.getItemCount() > 0) {
            int y = Integer.parseInt((String)cbYear.getSelectedItem());
            loadForYear(y);
        }
    }

    private void loadForYear(int year) {
                // Pie chart: loại phòng được đặt (reservation)
                DefaultPieDataset pieRoomBookedDataset = new DefaultPieDataset();
                java.util.List<Object[]> roomTypeBookedStats = service.getRoomTypeBookedForYear(year); // List<Object[]>: {String typeName, Integer count}
                for (Object[] row : roomTypeBookedStats) {
                    String type = String.valueOf(row[0]);
                    int count = 0;
                    try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { count = 0; }
                    pieRoomBookedDataset.setValue(type, count);
                }
                JFreeChart pieRoomBookedChart = ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false);
                pieRoomBookedPanel.setChart(pieRoomBookedChart);
        // Doanh thu theo tháng
        List<String[]> data = service.getMonthlyRevenueForYear(year);
        tableModel.setRowCount(0);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DecimalFormat df = new DecimalFormat("#,##0");
        for (String[] row : data) {
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

        // Pie chart: loại phòng được sử dụng
        DefaultPieDataset pieRoomDataset = new DefaultPieDataset();
        List<Object[]> roomTypeStats = service.getRoomTypeUsageForYear(year); // List<Object[]>: {String typeName, Integer count}
        for (Object[] row : roomTypeStats) {
            String type = String.valueOf(row[0]);
            int count = 0;
            try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { count = 0; }
            pieRoomDataset.setValue(type, count);
        }
        JFreeChart pieRoomChart = ChartFactory.createPieChart("Tỉ lệ loại phòng được sử dụng", pieRoomDataset, true, true, false);
        pieRoomPanel.setChart(pieRoomChart);

        // Pie chart: dịch vụ được sử dụng
        DefaultPieDataset pieServiceDataset = new DefaultPieDataset();
        List<Object[]> serviceStats = service.getServiceUsageForYear(year); // List<Object[]>: {String serviceName, Integer count}
        for (Object[] row : serviceStats) {
            String service = String.valueOf(row[0]);
            int count = 0;
            try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { count = 0; }
            pieServiceDataset.setValue(service, count);
        }
        JFreeChart pieServiceChart = ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false);
        pieServicePanel.setChart(pieServiceChart);
    }

    private void loadSavedStatsForYear(int year) {
        // Load saved statistics for this year and show in table: ID | Date | Period | Revenue | note
        java.util.List<com.ql_khach_san.model.Statistic> list = service.getStatisticsByPeriod("year");
        tableModel.setRowCount(0);
        // change table to columns: ID, Date, Revenue, Note
        tableModel.setColumnIdentifiers(new String[]{"ID", "Ngày", "Doanh thu", "Ghi chú"});
        DecimalFormat df = new DecimalFormat("#,##0");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (com.ql_khach_san.model.Statistic s : list) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(s.getStatDate());
            int y = cal.get(java.util.Calendar.YEAR);
            if (y == year) {
                String dateStr = sdf.format(s.getStatDate());
                String revStr = df.format(s.getRevenue());
                tableModel.addRow(new Object[]{s.getStatisticId(), dateStr, revStr, s.getNote()});
            }
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String label = tableModel.getValueAt(i, 1).toString();
            String revStr = tableModel.getValueAt(i, 2).toString();
            double val = 0.0;
            try { val = Double.parseDouble(revStr.replaceAll("[^0-9.-]", "")); } catch (Exception ex) { val = 0.0; }
            dataset.addValue(val, "Doanh thu", label);
        }
        JFreeChart chart = ChartFactory.createBarChart("Thống kê đã lưu - năm " + year, "Ngày", "Doanh thu", dataset);
        try {
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setNumberFormatOverride(new DecimalFormat("#,##0"));
        } catch (Exception ex) {}
        chartPanel.setChart(chart);
    }
}
