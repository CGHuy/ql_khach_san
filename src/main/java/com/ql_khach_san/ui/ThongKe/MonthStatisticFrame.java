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
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

public class MonthStatisticFrame extends JFrame {
    private StatisticService service = new StatisticService();
    private ChartPanel chartPanel;
    private JPanel kpiPanel;
    private JLabel lblBooked, lblUsed, lblDelta, lblTotalRevenue;
    private ChartPanel pieRoomPanel;
    private ChartPanel pieRoomBookedPanel;
    private ChartPanel pieServicePanel;
    private DefaultTableModel tableModel;
    
    // Cache to avoid redundant queries (year -> daily list)
    private Map<Integer, List<String[]>> dataCache = new HashMap<>();

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
        chartPanel.setPreferredSize(new Dimension(575, 190));
        formatChart(chartPanel);

        // KPI tiles panel (Booked / Used / Delta) replacing room-usage chart
        kpiPanel = createKpiPanel();
        kpiPanel.setPreferredSize(new Dimension(575, 190));

        pieRoomBookedPanel = new ChartPanel(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false));
        pieRoomBookedPanel.setPreferredSize(new Dimension(575, 190));

        pieServicePanel = new ChartPanel(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false));
        pieServicePanel.setPreferredSize(new Dimension(575, 190));

        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        chartsPanel.add(chartPanel);
        chartsPanel.add(kpiPanel);
        chartsPanel.add(pieRoomBookedPanel);
        chartsPanel.add(pieServicePanel);

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

    private JPanel createKpiPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 5, 5));

        lblTotalRevenue = new JLabel("0", SwingConstants.CENTER);
        lblTotalRevenue.setFont(lblTotalRevenue.getFont().deriveFont(16f).deriveFont(Font.BOLD));
        lblTotalRevenue.setForeground(new Color(0, 128, 0)); // Green color
        JLabel descRevenue = new JLabel("Tổng doanh thu", SwingConstants.CENTER);
        JPanel tile0 = new JPanel(new BorderLayout());
        tile0.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tile0.add(lblTotalRevenue, BorderLayout.CENTER);
        tile0.add(descRevenue, BorderLayout.SOUTH);

        lblBooked = new JLabel("0", SwingConstants.CENTER);
        lblBooked.setFont(lblBooked.getFont().deriveFont(16f).deriveFont(Font.BOLD));
        JLabel descBooked = new JLabel("Đặt (Booked)", SwingConstants.CENTER);
        JPanel tile1 = new JPanel(new BorderLayout());
        tile1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tile1.add(lblBooked, BorderLayout.CENTER);
        tile1.add(descBooked, BorderLayout.SOUTH);

        lblUsed = new JLabel("0", SwingConstants.CENTER);
        lblUsed.setFont(lblUsed.getFont().deriveFont(16f).deriveFont(Font.BOLD));
        JLabel descUsed = new JLabel("Sử dụng (Used)", SwingConstants.CENTER);
        JPanel tile2 = new JPanel(new BorderLayout());
        tile2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tile2.add(lblUsed, BorderLayout.CENTER);
        tile2.add(descUsed, BorderLayout.SOUTH);

        lblDelta = new JLabel("0", SwingConstants.CENTER);
        lblDelta.setFont(lblDelta.getFont().deriveFont(16f).deriveFont(Font.BOLD));
        JLabel descDelta = new JLabel("Chênh lệch", SwingConstants.CENTER);
        JPanel tile3 = new JPanel(new BorderLayout());
        tile3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tile3.add(lblDelta, BorderLayout.CENTER);
        tile3.add(descDelta, BorderLayout.SOUTH);

        p.add(tile0); p.add(tile1); p.add(tile2); p.add(tile3);
        return p;
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
        double totalRevenue = 0.0;
        List<String[]> daily = service.getDailyRevenueForMonth(year, month);
        for (String[] r : daily) {
            String dayLabel = r[0].length() >= 10 ? r[0].substring(8) : r[0];
            double val = 0.0;
            try { val = Double.parseDouble(r[1]); } catch (Exception ex) { }
            totalRevenue += val;
            tableModel.addRow(new Object[]{dayLabel, df.format(val)});
            dataset.addValue(val, "Doanh thu", dayLabel);
        }
        chartPanel.setChart(ChartFactory.createBarChart("Doanh thu theo ngày trong tháng " + String.format("%04d-%02d", year, month), "Ngày", "Doanh thu", dataset));
        formatChart(chartPanel);

        // Update total revenue label
        lblTotalRevenue.setText(df.format(totalRevenue) + " đ");

        // Compute KPI totals for month
        int totalUsed = 0;
        List<Object[]> roomTypeStats = service.getRoomTypeUsageForMonth(year, month);
        for (Object[] row : roomTypeStats) {
            int count = 0; try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { }
            totalUsed += count;
        }

        int totalBooked = 0;
        List<Object[]> roomBookedStats = service.getRoomTypeBookedForMonth(year, month);
        DefaultPieDataset pieRoomBooked = new DefaultPieDataset();
        for (Object[] row : roomBookedStats) {
            String type = String.valueOf(row[0]);
            int count = 0; try { count = Integer.parseInt(String.valueOf(row[1])); } catch (Exception ex) { }
            totalBooked += count;
            pieRoomBooked.setValue(type, count);
        }
        pieRoomBookedPanel.setChart(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBooked, true, true, false));

        lblBooked.setText(formatNumber(totalBooked));
        lblUsed.setText(formatNumber(totalUsed));
        int delta = totalBooked - totalUsed;
        String pct = totalBooked > 0 ? String.format(" (%.0f%%)", (totalUsed * 100.0 / totalBooked)) : "";
        lblDelta.setText(formatNumber(delta) + pct);

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
