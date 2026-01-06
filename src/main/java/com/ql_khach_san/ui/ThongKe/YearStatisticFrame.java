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

public class YearStatisticFrame extends JFrame {
    private StatisticService service = new StatisticService();
    private ChartPanel chartPanel;
    private ChartPanel chartPanelRight;
    private ChartPanel pieServicePanel;
    private ChartPanel pieRoomBookedPanel;
    private DefaultTableModel tableModel;
    
    // Cache datasets to avoid recreation
    private DefaultCategoryDataset revenueDataset;
    private DefaultPieDataset pieRoomBookedDataset;
    private DefaultPieDataset pieServiceDataset;

    public YearStatisticFrame() {
        setTitle("Thống kê theo năm");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) {
            cbYear.addItem(String.valueOf(y));
        }
        JButton btnGen = new JButton("Xem năm");
        control.add(new JLabel("Năm:"));
        control.add(cbYear);
        control.add(btnGen);
        add(control, BorderLayout.NORTH);

        // Initialize datasets once
        revenueDataset = new DefaultCategoryDataset();
        // roomUsageDataset initialized below (bar chart)
        pieRoomBookedDataset = new DefaultPieDataset();
        pieServiceDataset = new DefaultPieDataset();

        // Main chart: Doanh thu theo tháng
        JFreeChart chart = ChartFactory.createBarChart("Doanh thu theo tháng trong năm", "Tháng", "Doanh thu", revenueDataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(575, 190));
        formatChart(chartPanel);

        // Second revenue chart replaces KPI panel
        chartPanelRight = new ChartPanel(ChartFactory.createBarChart("Doanh thu theo tháng trong năm " + currentYear, "Tháng", "Doanh thu", revenueDataset));
        chartPanelRight.setPreferredSize(new Dimension(575, 190));
        formatChart(chartPanelRight);

        // Pie chart for room types booked (keep as pie)
        JFreeChart pieRoomBookedChart = ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false);
        pieRoomBookedPanel = new ChartPanel(pieRoomBookedChart);
        pieRoomBookedPanel.setPreferredSize(new Dimension(575, 190));
        // Pie chart for services
        JFreeChart pieServiceChart = ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false);
        pieServicePanel = new ChartPanel(pieServiceChart);
        pieServicePanel.setPreferredSize(new Dimension(575, 190));

        // Panel to hold all charts in a 2x2 grid
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        chartsPanel.add(chartPanel);
        chartsPanel.add(chartPanelRight);
        chartsPanel.add(pieRoomBookedPanel);
        chartsPanel.add(pieServicePanel);

        // Add some padding around charts
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        wrapper.add(chartsPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Table for doanh thu
        tableModel = new DefaultTableModel(new String[]{"Tháng","Doanh thu"}, 0);
        JTable tbl = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tbl);
        scrollPane.setPreferredSize(new Dimension(1180, 150));
        add(scrollPane, BorderLayout.SOUTH);

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
    private void loadForYear(int year) {
        // Load all data at once
        revenueDataset.clear();
        pieRoomBookedDataset.clear();
        pieServiceDataset.clear();
        tableModel.setRowCount(0);

        DecimalFormat df = new DecimalFormat("#,##0");

        // Load monthly revenue
        List<String[]> monthlyData = service.getMonthlyRevenueForYear(year);
        for (String[] row : monthlyData) {
            String label = row[0].length() >= 7 ? row[0].substring(5) : row[0];
            double val = 0.0;
            try { 
                val = Double.parseDouble(row[1]); 
            } catch (Exception ex) { }
            tableModel.addRow(new Object[]{label, df.format(val)});
            revenueDataset.addValue(val, "Doanh thu", label);
        }
        chartPanel.setChart(ChartFactory.createBarChart("Doanh thu theo tháng trong năm " + year, "Tháng", "Doanh thu", revenueDataset));
        formatChart(chartPanel);

        List<Object[]> roomTypeBookedStats = service.getRoomTypeBookedForYear(year);
        pieRoomBookedDataset.clear();
        for (Object[] row : roomTypeBookedStats) {
            String type = String.valueOf(row[0]);
            int count = 0; try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { }
            pieRoomBookedDataset.setValue(type, count);
        }
        pieRoomBookedPanel.setChart(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false));

        // Load service usage
        List<Object[]> serviceStats = service.getServiceUsageForYear(year);
        for (Object[] row : serviceStats) {
            String serviceName = String.valueOf(row[0]);
            int count = 0;
            try { 
                count = Integer.parseInt(String.valueOf(row[1])); 
            } catch (Exception ex) { }
            pieServiceDataset.setValue(serviceName, count);
        }
        pieServicePanel.setChart(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false));
    }
}
