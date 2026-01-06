package com.ql_khach_san.ui.ThongKe;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class MonthStatisticFrame extends JFrame {
    private final StatisticService service = new StatisticService();
    private final DecimalFormat df = new DecimalFormat("#,##0");
    private ChartPanel chartPanel, pieRoomBookedPanel, pieServicePanel;
    private JLabel lblTotalRevenue, lblBooked, lblUsed, lblDelta;
    private DefaultTableModel tableModel;

    public MonthStatisticFrame() {
        setTitle("Thống kê theo tháng");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Control panel
        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>(), cbMonth = new JComboBox<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = curYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        for (int m = 1; m <= 12; m++) cbMonth.addItem(String.format("%02d", m));
        control.add(new JLabel("Năm:")); control.add(cbYear);
        control.add(new JLabel("Tháng:")); control.add(cbMonth);
        JButton btnGen = new JButton("Xem tháng");
        control.add(btnGen);
        add(control, BorderLayout.NORTH);

        // Charts (2x2)
        chartPanel = createBarChart("Doanh thu theo ngày", "Ngày", "Doanh thu");
        pieRoomBookedPanel = createPieChart("Tỉ lệ loại phòng được đặt");
        pieServicePanel = createPieChart("Tỉ lệ dịch vụ được sử dụng");

        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        chartsPanel.add(chartPanel);
        chartsPanel.add(createKpiPanel());
        chartsPanel.add(pieRoomBookedPanel);
        chartsPanel.add(pieServicePanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        wrapper.add(chartsPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Ngày", "Doanh thu"}, 0);
        JScrollPane scroll = new JScrollPane(new JTable(tableModel));
        scroll.setPreferredSize(new Dimension(1180, 150));
        add(scroll, BorderLayout.SOUTH);

        // Events & initial load
        btnGen.addActionListener(e -> loadData(toInt(cbYear), toInt(cbMonth)));
        loadData(toInt(cbYear), toInt(cbMonth));
    }

    private ChartPanel createBarChart(String title, String x, String y) {
        ChartPanel p = new ChartPanel(ChartFactory.createBarChart(title, x, y, new DefaultCategoryDataset()));
        p.setPreferredSize(new Dimension(575, 190));
        formatChart(p);
        return p;
    }

    private ChartPanel createPieChart(String title) {
        ChartPanel p = new ChartPanel(ChartFactory.createPieChart(title, new DefaultPieDataset(), true, true, false));
        p.setPreferredSize(new Dimension(575, 190));
        return p;
    }

    private void formatChart(ChartPanel p) {
        if (p.getChart() != null && p.getChart().getPlot() instanceof CategoryPlot)
            ((NumberAxis) ((CategoryPlot) p.getChart().getPlot()).getRangeAxis()).setNumberFormatOverride(df);
    }

    private JPanel createKpiPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 5, 5));
        lblTotalRevenue = addKpiTile(p, "Tổng doanh thu", new Color(0, 128, 0));
        lblBooked = addKpiTile(p, "Đặt (Booked)", null);
        lblUsed = addKpiTile(p, "Sử dụng (Used)", null);
        lblDelta = addKpiTile(p, "Chênh lệch", null);
        p.setPreferredSize(new Dimension(575, 190));
        return p;
    }

    private JLabel addKpiTile(JPanel parent, String desc, Color color) {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        if (color != null) lbl.setForeground(color);
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tile.add(lbl, BorderLayout.CENTER);
        tile.add(new JLabel(desc, SwingConstants.CENTER), BorderLayout.SOUTH);
        parent.add(tile);
        return lbl;
    }

    private void loadData(int year, int month) {
        tableModel.setRowCount(0);
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        double totalRevenue = 0;

        for (String[] r : service.getDailyRevenueForMonth(year, month)) {
            String day = r[0].length() >= 10 ? r[0].substring(8) : r[0];
            double val = toDouble(r[1]);
            totalRevenue += val;
            tableModel.addRow(new Object[]{day, df.format(val)});
            ds.addValue(val, "Doanh thu", day);
        }
        chartPanel.setChart(ChartFactory.createBarChart("Doanh thu - " + year + "/" + String.format("%02d", month), "Ngày", "Doanh thu", ds));
        formatChart(chartPanel);
        lblTotalRevenue.setText(df.format(totalRevenue) + " đ");

        // KPI
        int totalUsed = sumCount(service.getRoomTypeUsageForMonth(year, month));
        int totalBooked = 0;
        DefaultPieDataset pieRoom = new DefaultPieDataset();
        for (Object[] row : service.getRoomTypeBookedForMonth(year, month)) {
            int c = toInt(row[1]); totalBooked += c;
            pieRoom.setValue(String.valueOf(row[0]), c);
        }
        pieRoomBookedPanel.setChart(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoom, true, true, false));

        lblBooked.setText(df.format(totalBooked));
        lblUsed.setText(df.format(totalUsed));
        lblDelta.setText(df.format(totalBooked - totalUsed) + (totalBooked > 0 ? String.format(" (%.0f%%)", totalUsed * 100.0 / totalBooked) : ""));

        // Service
        DefaultPieDataset pieSvc = new DefaultPieDataset();
        for (Object[] row : service.getServiceUsageForMonth(year, month))
            pieSvc.setValue(String.valueOf(row[0]), toInt(row[1]));
        pieServicePanel.setChart(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieSvc, true, true, false));
    }

    private int sumCount(List<Object[]> list) { int s = 0; for (Object[] r : list) s += toInt(r[1]); return s; }
    private int toInt(Object o) { try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; } }
    private int toInt(JComboBox<String> cb) { return Integer.parseInt((String) cb.getSelectedItem()); }
    private double toDouble(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0; } }
}
