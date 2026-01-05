package com.ql_khach_san.ui.ThongKe;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class StatisticPanel extends JPanel {
    private StatisticService statisticService;
    private JTable table;
    private DefaultTableModel tableModel;
    private ChartPanel chartPanel;
    private JSpinner spinnerDate;
    private Timer autoComputeTimer;
    private int compareWindow = 7;
    
    // Cache last loaded data to avoid redundant database queries
    private List<Statistic> lastComputedRange = null;
    private List<String[]> lastComparisonData = null;
    private Date lastLoadedDate = null;

    public StatisticPanel() {
        statisticService = new StatisticService();
        setLayout(new BorderLayout());
        initTable();
        initButtons();
        initChart();
        loadData();
    }

    private void initTable() {
        String[] columns = {"ID", "Ngày", "Doanh thu", "Phòng", "Dịch vụ", "Số khách", "Số phòng", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }



    private void initButtons() {
        // Top controls: default Day only, plus buttons to open Month/Year dialogs
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Day picker
        spinnerDate = new JSpinner(new SpinnerDateModel());
        spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "dd-MM-yyyy"));
        header.add(new JLabel("Ngày:")); 
        header.add(spinnerDate);

        // Buttons to open Month/Year dialogs
        JButton btnOpenMonth = new JButton("Thống kê tháng...");
        JButton btnOpenYear = new JButton("Thống kê năm...");
        header.add(btnOpenMonth); 
        header.add(btnOpenYear);

        // Chart toggle
        JButton btnChartType = new JButton("Đổi biểu đồ");
        header.add(btnChartType);

        // Create top container that will hold header and chart
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(header, BorderLayout.NORTH);
        add(topContainer, BorderLayout.NORTH);

        // Open month frame
        btnOpenMonth.addActionListener(e -> {
            try {
                MonthStatisticFrame f = new MonthStatisticFrame();
                f.setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể mở Thống kê tháng:\n" + t.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Open year frame
        btnOpenYear.addActionListener(e -> {
            try {
                YearStatisticFrame f = new YearStatisticFrame();
                f.setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể mở Thống kê năm:\n" + t.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnChartType.addActionListener(e -> toggleChartType());

        // Auto compute setup
        autoComputeTimer = new Timer(500, ev -> computeForSpinnerDate());
        autoComputeTimer.setRepeats(false);
        spinnerDate.addChangeListener(ev -> autoComputeTimer.restart());

        // Initial compute for today's spinner value
        computeForSpinnerDate();

        // If chartPanel already created, add it to topContainer center
        if (chartPanel != null) {
            topContainer.add(chartPanel, BorderLayout.CENTER);
        } else {
            this.topContainerRef = topContainer;
        }
    }

    private void loadData() {
        try {
            Date d = (Date) spinnerDate.getValue();
            computeRangeEndingAt(d);
        } catch (Exception ex) {
            // ignore invalid spinner
        }
    }

    private void loadData(String period) {
        loadData();
    }

    private void generateForSelectedPeriod(String period, String input) {
        try {
            Statistic stat = null;
            java.sql.Date sqlDate = null;
            if (period.equals("day")) {
                sqlDate = java.sql.Date.valueOf(input);
                stat = statisticService.generateStatisticByDate(sqlDate);
                List<String[]> nearby = statisticService.getNearestDaysRevenue(sqlDate, 7);
                displayDailyRevenueComparison(nearby);
            } else if (period.equals("month")) {
                String[] parts = input.split("-");
                if (parts.length < 2) throw new IllegalArgumentException("Định dạng tháng: yyyy-MM");
                int y = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                stat = statisticService.generateStatisticByMonth(y, m);
                sqlDate = java.sql.Date.valueOf(String.format("%04d-%02d-01", y, m));
            } else { // year
                int y = Integer.parseInt(input);
                stat = statisticService.generateStatisticByYear(y);
                sqlDate = java.sql.Date.valueOf(String.format("%04d-01-01", y));
            }
            if (stat == null) {
                JOptionPane.showMessageDialog(this, "Không lấy được dữ liệu thống kê");
                return;
            }
            stat.setStatPeriod(period);
            stat.setNote("");
            JOptionPane.showMessageDialog(this, "Thống kê đã được tính toán.");
            if ("day".equals(period)) {
                if (lastComparisonData != null) displayDailyRevenueComparison(lastComparisonData);
            } else {
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hoặc định dạng không hợp lệ: " + ex.getMessage());
        }
    }
    private boolean chartIsBar = true;
    // reference to the top container so initChart can add the chart into it
    private JPanel topContainerRef = null;

    // Comparison view state
    private boolean inComparisonView = false;


    /**
     * Display a computed range of daily statistics (full columns)
     */
    private void displayComputedRange(List<Statistic> list) {
        String[] fullCols = new String[]{"Ngày", "Doanh thu", "Phòng", "Dịch vụ", "Số khách", "Số phòng"};
        tableModel.setColumnIdentifiers(fullCols);
        tableModel.setRowCount(0);
        for (Statistic s : list) {
            tableModel.addRow(new Object[]{
                formatDate(s.getStatDate()),
                formatMoney(s.getRevenue()),
                formatMoney(s.getRoomRevenue()),
                formatMoney(s.getServiceRevenue()),
                s.getCustomerCount(),
                s.getRoomRentedCount()
            });
        }
        updateChart(list);
    }

    /**
     * Compute a range ending at given date (spinner value) and display it
     */
    private void computeRangeEndingAt(Date d) {
        if (d == null) return;
        
        // Avoid redundant computations for the same date
        if (lastLoadedDate != null && lastLoadedDate.equals(d) && lastComputedRange != null) {
            displayComputedRange(lastComputedRange);
            return;
        }
        
        java.sql.Date end = new java.sql.Date(d.getTime());
        List<Statistic> list = statisticService.computeDailyStats(end, compareWindow);
        if (list == null) list = new ArrayList<>();
        
        // Cache the result
        lastComputedRange = list;
        lastLoadedDate = new Date(d.getTime());
        
        displayComputedRange(list);
    }

    private void displayDailyRevenueComparison(List<String[]> data) {
        this.lastComparisonData = data;
        this.inComparisonView = true;

        String[] cols = new String[] {"Ngày", "Doanh thu"};
        tableModel.setColumnIdentifiers(cols);
        tableModel.setRowCount(0);
        
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd-MM-yyyy");
        
        for (String[] r : data) {
            String label = r[0];
            try { 
                label = sdfOut.format(sdfIn.parse(r[0])); 
            } catch (Exception ex) { }
            double val = 0.0; 
            try { 
                val = Double.parseDouble(r[1]); 
            } catch (Exception ex) { }
            tableModel.addRow(new Object[] { label, formatMoney(val) });
        }
        updateChartForComparison(data);
    }

    private void updateChartForComparison(List<String[]> data) {
        if (chartPanel == null) return;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd-MM-yyyy");
        for (String[] r : data) {
            String label = r[0];
            try { 
                label = sdfOut.format(sdfIn.parse(r[0])); 
            } catch (Exception ex) { }
            double rev = 0.0;
            try { 
                rev = Double.parseDouble(r[1]); 
            } catch (Exception ex) { 
                rev = 0.0; 
            }
            dataset.addValue(rev, "Doanh thu", label);
        }
        JFreeChart chart;
        if (chartIsBar) {
            chart = ChartFactory.createBarChart("So sánh doanh thu quanh ngày", "Ngày", "Doanh thu", dataset);
        } else {
            chart = ChartFactory.createLineChart("So sánh doanh thu quanh ngày", "Ngày", "Doanh thu", dataset);
        }
        try {
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setNumberFormatOverride(new DecimalFormat("#,##0"));
        } catch (Exception ex) {}
        chartPanel.setChart(chart);
    }

    // Called by debounce timer to compute based on current spinner value
    private void computeForSpinnerDate() {
        try {
            Date d = (Date) spinnerDate.getValue();
            computeRangeEndingAt(d);
        } catch (Exception ex) {
            // ignore invalid state
        }
    }

    private String formatDate(Date d) {
        if (d == null) return "";
        try { 
            return new SimpleDateFormat("dd-MM-yyyy").format(d); 
        } catch (Exception ex) { 
            return d.toString(); 
        }
    }

    private String formatMoney(double v) {
        try { 
            return new DecimalFormat("#,##0").format(v); 
        } catch (Exception ex) { 
            return String.valueOf(v); 
        }
    }

    private void computeForDate(Date d) {
        if (d == null) return;
        java.sql.Date sqlDate = new java.sql.Date(d.getTime());
        Statistic stat = statisticService.generateStatisticByDate(sqlDate);
        List<String[]> nearby = statisticService.getNearestDaysRevenue(sqlDate, compareWindow);
        
        // ensure target date is included (even if revenue zero)
        String target = sqlDate.toString();
        boolean found = false;
        for (String[] r : nearby) {
            if (r[0].equals(target)) { 
                found = true; 
                break; 
            }
        }
        if (!found) {
            double rev = (stat != null) ? stat.getRevenue() : 0.0;
            nearby.add(new String[] { target, String.valueOf(rev) });
        }
        
        // sort ascending
        nearby.sort((a,b) -> java.sql.Date.valueOf(a[0]).compareTo(java.sql.Date.valueOf(b[0])));
        displayDailyRevenueComparison(nearby);
    }

    private void initChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu",
                "Thời gian",
                "Doanh thu",
                dataset
        );
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 300));
        // If we have a top container (header already added), put chart there; otherwise add to NORTH
        if (topContainerRef != null) {
            topContainerRef.add(chartPanel, BorderLayout.CENTER);
            topContainerRef.revalidate();
            topContainerRef.repaint();
        } else {
            add(chartPanel, BorderLayout.NORTH);
        }
    }

    private void toggleChartType() {
        chartIsBar = !chartIsBar;
        // refresh chart using cached data without recomputing
        if (inComparisonView && lastComparisonData != null) {
            updateChartForComparison(lastComparisonData);
        } else if (lastComputedRange != null) {
            updateChart(lastComputedRange);
        } else {
            loadData();
        }
    }

    private void updateChart(List<Statistic> list) {
        if (chartPanel == null || list == null || list.isEmpty()) return;
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // sắp xếp theo ngày tăng dần
        list.sort(Comparator.comparing(Statistic::getStatDate));
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd-MM-yyyy");
        
        for (Statistic s : list) {
            String dateLabel = sdfOut.format(s.getStatDate());
            String label = (s.getStatPeriod() != null && !s.getStatPeriod().isEmpty() && !s.getStatPeriod().equals("day"))
                    ? dateLabel + " (" + s.getStatPeriod() + ")" : dateLabel;
            dataset.addValue(s.getRevenue(), "Doanh thu", label);
        }
        
        JFreeChart chart;
        if (chartIsBar) {
            chart = ChartFactory.createBarChart("Doanh thu", "Thời gian", "Doanh thu", dataset);
        } else {
            chart = ChartFactory.createLineChart("Doanh thu", "Thời gian", "Doanh thu", dataset);
        }
        
        try {
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setNumberFormatOverride(new DecimalFormat("#,##0"));
        } catch (Exception ex) {}
        
        chartPanel.setChart(chart);
    }
}
