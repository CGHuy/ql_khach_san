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

public class YearStatisticFrame extends JFrame {
    private final StatisticService service = new StatisticService();
    private final DecimalFormat df = new DecimalFormat("#,##0");
    private ChartPanel chartPanel, pieRoomBookedPanel, pieServicePanel;
    private JLabel lblTotalRevenue, lblBooked, lblUsed, lblDelta;
    private DefaultTableModel tableModel;
    private DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
    private DefaultPieDataset pieRoomBookedDataset = new DefaultPieDataset();
    private DefaultPieDataset pieServiceDataset = new DefaultPieDataset();

    public YearStatisticFrame() {
        setTitle("Thống kê theo năm");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Control panel
        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbYear = new JComboBox<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = curYear; y >= 2000; y--) cbYear.addItem(String.valueOf(y));
        control.add(new JLabel("Năm:")); control.add(cbYear);
        JButton btnGen = new JButton("Xem năm");
        control.add(btnGen);
        add(control, BorderLayout.NORTH);

        // Charts (2x2)
        chartPanel = new ChartPanel(ChartFactory.createBarChart("Doanh thu theo tháng", "Tháng", "Doanh thu", revenueDataset));
        chartPanel.setPreferredSize(new Dimension(575, 190));
        formatChart(chartPanel);

        pieRoomBookedPanel = new ChartPanel(ChartFactory.createPieChart("Tỉ lệ loại phòng được đặt", pieRoomBookedDataset, true, true, false));
        pieRoomBookedPanel.setPreferredSize(new Dimension(575, 190));
        pieServicePanel = new ChartPanel(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false));
        pieServicePanel.setPreferredSize(new Dimension(575, 190));

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
        tableModel = new DefaultTableModel(new String[]{"Tháng", "Doanh thu"}, 0);
        JScrollPane scroll = new JScrollPane(new JTable(tableModel));
        scroll.setPreferredSize(new Dimension(1180, 150));
        add(scroll, BorderLayout.SOUTH);

        // Events & initial load
        btnGen.addActionListener(e -> loadData(toInt(cbYear)));
        if (cbYear.getItemCount() > 0) loadData(toInt(cbYear));
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

    private void loadData(int year) {
        revenueDataset.clear();
        pieRoomBookedDataset.clear();
        pieServiceDataset.clear();
        tableModel.setRowCount(0);
        double totalRevenue = 0;

        for (String[] row : service.getMonthlyRevenueForYear(year)) {
            String label = row[0].length() >= 7 ? row[0].substring(5) : row[0];
            double val = toDouble(row[1]);
            totalRevenue += val;
            tableModel.addRow(new Object[]{label, df.format(val)});
            revenueDataset.addValue(val, "Doanh thu", label);
        }
        chartPanel.setChart(ChartFactory.createBarChart("Doanh thu theo tháng - " + year, "Tháng", "Doanh thu", revenueDataset));
        formatChart(chartPanel);
        lblTotalRevenue.setText(df.format(totalRevenue) + " đ");

        // KPI
        int totalUsed = sumCount(service.getRoomTypeUsageForYear(year));
        int totalBooked = 0;
        pieRoomBookedDataset.clear();
        for (Object[] row : service.getRoomTypeBookedForYear(year)) {
            int c = toInt(row[1]); totalBooked += c;
            pieRoomBookedDataset.setValue(String.valueOf(row[0]), c);
        }
        lblBooked.setText(df.format(totalBooked));
        lblUsed.setText(df.format(totalUsed));
        lblDelta.setText(df.format(totalBooked - totalUsed) + (totalBooked > 0 ? String.format(" (%.0f%%)", totalUsed * 100.0 / totalBooked) : ""));

        // Service
        for (Object[] row : service.getServiceUsageForYear(year))
            pieServiceDataset.setValue(String.valueOf(row[0]), toInt(row[1]));
        pieServicePanel.setChart(ChartFactory.createPieChart("Tỉ lệ dịch vụ được sử dụng", pieServiceDataset, true, true, false));
    }

    private int sumCount(List<Object[]> list) { int s = 0; for (Object[] r : list) s += toInt(r[1]); return s; }
    private int toInt(Object o) { try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; } }
    private int toInt(JComboBox<String> cb) { return Integer.parseInt((String) cb.getSelectedItem()); }
    private double toDouble(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0; } }
}
