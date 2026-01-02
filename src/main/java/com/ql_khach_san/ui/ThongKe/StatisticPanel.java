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
    private ChartPanel chartPanel;
    private JSpinner spinnerDate;
    private javax.swing.Timer autoComputeTimer;
    private int compareWindow = 7; // number of nearest days to include in comparison

    public StatisticPanel() {
        statisticService = new com.ql_khach_san.service.StatisticService();
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
        spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "yyyy-MM-dd"));
        header.add(new JLabel("Ngày:")); header.add(spinnerDate);

        // Buttons to open Month/Year dialogs
        JButton btnOpenMonth = new JButton("Thống kê tháng...");
        JButton btnOpenYear = new JButton("Thống kê năm...");
        header.add(btnOpenMonth); header.add(btnOpenYear);

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
                MonthStatisticFrame f = new com.ql_khach_san.ui.ThongKe.MonthStatisticFrame();
                f.setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể mở Thống kê tháng:\n" + t.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Open year frame
        btnOpenYear.addActionListener(e -> {
            try {
                YearStatisticFrame f = new com.ql_khach_san.ui.ThongKe.YearStatisticFrame();
                f.setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể mở Thống kê năm:\n" + t.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnChartType.addActionListener(e -> toggleChartType());

        // Auto compute setup: debounce spinner changes (enabled by default)
        autoComputeTimer = new javax.swing.Timer(500, ev -> computeForSpinnerDate());
        autoComputeTimer.setRepeats(false);
        spinnerDate.addChangeListener(ev -> autoComputeTimer.restart());

        // Live-only view (no 'Xem đã lưu' toggle)

        // Initial compute for today's spinner value
        computeForSpinnerDate();

        // If chartPanel already created, add it to topContainer center
        if (chartPanel != null) {
            topContainer.add(chartPanel, BorderLayout.CENTER);
        } else {
            // ensure initChart will add to the same container later by setting a flag
            this.topContainerRef = topContainer;
        }
    }

    private void loadData() {
        loadData(null);
    }

    private void loadData(String period) {
        // Live-only: compute range ending at spinner value
        try {
            java.util.Date d = (java.util.Date) spinnerDate.getValue();
            computeRangeEndingAt(d);
        } catch (Exception ex) {
            // ignore invalid spinner
        }
    }

    private void generateForSelectedPeriod(String period, String input) {
        try {
            Statistic stat = null;
            java.sql.Date sqlDate = null;
            if (period.equals("day")) {
                sqlDate = java.sql.Date.valueOf(input);
                stat = statisticService.generateStatisticByDate(sqlDate);
                // Show comparison with nearest days from DB
                java.util.List<String[]> nearby = statisticService.getNearestDaysRevenue(sqlDate, 7); // 7 closest days
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
            // Saving is disabled in live-only mode
            JOptionPane.showMessageDialog(this, "Lưu thủ công đã bị vô hiệu hoá; hệ thống hiển thị dữ liệu động từ các bảng nguồn.");
            if ("day".equals(period)) {
                // keep comparison view
                if (lastComparisonData != null) displayDailyRevenueComparison(lastComparisonData);
            } else {
                loadData(period);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hoặc định dạng không hợp lệ: " + ex.getMessage());
        }
    }
    private boolean chartIsBar = true;
    // reference to the top container so initChart can add the chart into it
    private JPanel topContainerRef = null;

    // Comparison view state
    private java.util.List<String[]> lastComparisonData = null;
    private boolean inComparisonView = false;

    // Live vs saved view


    /**
     * Display a computed range of daily statistics (full columns)
     */
    private void displayComputedRange(java.util.List<Statistic> list) {
        // full columns
        String[] fullCols = new String[]{"Ngày", "Doanh thu", "Phòng", "Dịch vụ", "Số khách", "Số phòng"};
        tableModel.setColumnIdentifiers(fullCols);
        tableModel.setRowCount(0);
        for (Statistic s : list) {
            tableModel.addRow(new Object[]{
                s.getStatDate(),
                s.getRevenue(),
                s.getRoomRevenue(),
                s.getServiceRevenue(),
                s.getCustomerCount(),
                s.getRoomRentedCount()
            });
        }
        // update chart
        updateChart(list);

    }

    /**
     * Compute a range ending at given date (spinner value) and display it (no save)
     */
    private void computeRangeEndingAt(java.util.Date d) {
        if (d == null) return;
        java.sql.Date end = new java.sql.Date(d.getTime());
        java.util.List<Statistic> list = statisticService.computeDailyStats(end, compareWindow);
        if (list == null) list = new java.util.ArrayList<>();
        displayComputedRange(list);
    }

    private void displayDailyRevenueComparison(java.util.List<String[]> data) {
        this.lastComparisonData = data;
        this.inComparisonView = true;

        // Set simple columns Date / Revenue
        String[] cols = new String[] {"Ngày", "Doanh thu"};
        tableModel.setColumnIdentifiers(cols);
        tableModel.setRowCount(0);
        for (String[] r : data) {
            tableModel.addRow(new Object[] { r[0], Double.parseDouble(r[1]) });
        }
        updateChartForComparison(data);
    }

    private void updateChartForComparison(java.util.List<String[]> data) {
        if (chartPanel == null) return;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] r : data) {
            String label = r[0];
            double rev = 0.0;
            try { rev = Double.parseDouble(r[1]); } catch (Exception ex) { rev = 0.0; }
            dataset.addValue(rev, "Doanh thu", label);
        }
        JFreeChart chart;
        if (chartIsBar) chart = ChartFactory.createBarChart("So sánh doanh thu quanh ngày", "Ngày", "Doanh thu", dataset);
        else chart = ChartFactory.createLineChart("So sánh doanh thu quanh ngày", "Ngày", "Doanh thu", dataset);
        chartPanel.setChart(chart);
    }

    // Called by debounce timer to compute based on current spinner value
    private void computeForSpinnerDate() {
        try {
            java.util.Date d = (java.util.Date) spinnerDate.getValue();
            computeRangeEndingAt(d);
        } catch (Exception ex) {
            // ignore invalid state
        }
    }

    // Compute statistics for the given date and display comparison (no saving)
    private void computeForDate(java.util.Date d) {
        if (d == null) return;
        java.sql.Date sqlDate = new java.sql.Date(d.getTime());
        // generate single-day aggregate (not saved)
        Statistic stat = statisticService.generateStatisticByDate(sqlDate);
        java.util.List<String[]> nearby = statisticService.getNearestDaysRevenue(sqlDate, compareWindow);
        // ensure target date is included (even if revenue zero)
        String target = sqlDate.toString();
        boolean found = false;
        for (String[] r : nearby) {
            if (r[0].equals(target)) { found = true; break; }
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
        // refresh chart using current view
        if (inComparisonView && lastComparisonData != null) {
            updateChartForComparison(lastComparisonData);
        } else {
            loadData();
        }
    }

    private void updateChart(List<Statistic> list) {
        if (chartPanel == null) return; // bảo vệ null
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // sắp xếp theo ngày tăng dần để biểu đồ dễ đọc
        list.sort(Comparator.comparing(Statistic::getStatDate));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Statistic s : list) {
            String label = (s.getStatPeriod() != null && !s.getStatPeriod().isEmpty() && !s.getStatPeriod().equals("day"))
                    ? sdf.format(s.getStatDate()) + " (" + s.getStatPeriod() + ")" : sdf.format(s.getStatDate());
            dataset.addValue(s.getRevenue(), "Doanh thu", label);
        }
        JFreeChart chart;
        if (chartIsBar) {
            chart = ChartFactory.createBarChart("Doanh thu", "Thời gian", "Doanh thu", dataset);
        } else {
            chart = ChartFactory.createLineChart("Doanh thu", "Thời gian", "Doanh thu", dataset);
        }
        chartPanel.setChart(chart);
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
            // Saving is disabled; just compute and display
            if (period.equals("day")) {
                java.sql.Date d = new java.sql.Date(autoStat.getStatDate().getTime());
                java.util.List<String[]> nearby = statisticService.getNearestDaysRevenue(d, 7);
                displayDailyRevenueComparison(nearby);
            } else {
                loadData();
            }
            JOptionPane.showMessageDialog(this, "Lưu tự động đã bị vô hiệu hoá; dữ liệu chỉ hiển thị động.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày/Kỳ không hợp lệ hoặc lỗi: " + ex.getMessage());
        }
    }





    private void showMonthDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Thống kê tháng", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        JComboBox<String> cbMonth = new JComboBox<>();
        for (int m = 1; m <= 12; m++) cbMonth.addItem(String.format("%02d", m));
        JButton btn = new JButton("Generate");
        btn.addActionListener(e -> {
            String input = cbYear.getSelectedItem() + "-" + cbMonth.getSelectedItem();
            generateForSelectedPeriod("month", input);
            d.dispose();
        });
        p.add(new JLabel("Chọn tháng:")); p.add(cbYear); p.add(cbMonth); p.add(btn);
        d.add(p);
        d.pack(); d.setLocationRelativeTo(this); d.setVisible(true);
    }

    private void showYearDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Thống kê năm", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int y = currentYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        JButton btn = new JButton("Generate");
        btn.addActionListener(e -> {
            String input = (String) cbYear.getSelectedItem();
            generateForSelectedPeriod("year", input);
            d.dispose();
        });
        p.add(new JLabel("Chọn năm:")); p.add(cbYear); p.add(btn);
        d.add(p);
        d.pack(); d.setLocationRelativeTo(this); d.setVisible(true);
    }
}
