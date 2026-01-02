package com.ql_khach_san.ui.ThongKe;

import com.ql_khach_san.model.Statistic;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticDialog extends JDialog {
    private boolean saved = false;
    private Statistic statistic;
    private JTextField tfDate, tfPeriod, tfRevenue, tfRoomRevenue, tfServiceRevenue, tfCustomerCount, tfRoomRentedCount, tfServiceCount, tfNote;
    private JButton btnSave, btnCancel;
    private boolean allowAllFields;

    // allowAllFields = true: cho phép nhập tất cả, false: chỉ cho phép sửa ghi chú
    public StatisticDialog(Statistic s, boolean allowAllFields) {
        this.allowAllFields = allowAllFields;
        setTitle(s == null ? "Thêm thống kê" : "Sửa ghi chú" );
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(11, 2, 5, 5));
        initFields();
        if (s != null) setFields(s);
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        add(btnSave); add(btnCancel);
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        if (!allowAllFields) setFieldsEditable(false);
        setResizable(false);
        pack();
    }

    // Overload: mặc định cho sửa ghi chú (chỉ sửa note)
    public StatisticDialog(Statistic s) {
        this(s, false);
    }

    private void initFields() {
        add(new JLabel("Ngày (yyyy-MM-dd):")); tfDate = new JTextField(); add(tfDate);
        add(new JLabel("Kỳ (day/month/year):")); tfPeriod = new JTextField(); add(tfPeriod);
        add(new JLabel("Doanh thu:")); tfRevenue = new JTextField(); add(tfRevenue);
        add(new JLabel("Doanh thu phòng:")); tfRoomRevenue = new JTextField(); add(tfRoomRevenue);
        add(new JLabel("Doanh thu dịch vụ:")); tfServiceRevenue = new JTextField(); add(tfServiceRevenue);
        add(new JLabel("Số khách:")); tfCustomerCount = new JTextField(); add(tfCustomerCount);
        add(new JLabel("Số phòng thuê:")); tfRoomRentedCount = new JTextField(); add(tfRoomRentedCount);
        add(new JLabel("Số dịch vụ:")); tfServiceCount = new JTextField(); add(tfServiceCount);
        add(new JLabel("Ghi chú:")); tfNote = new JTextField(); add(tfNote);
    }

    private void setFieldsEditable(boolean allFieldsEditable) {
        // Nếu allFieldsEditable = true thì cho phép sửa toàn bộ trường;
        // nếu false thì chỉ cho phép sửa ghi chú
        tfDate.setEditable(allFieldsEditable);
        tfPeriod.setEditable(allFieldsEditable);
        tfRevenue.setEditable(allFieldsEditable);
        tfRoomRevenue.setEditable(allFieldsEditable);
        tfServiceRevenue.setEditable(allFieldsEditable);
        tfCustomerCount.setEditable(allFieldsEditable);
        tfRoomRentedCount.setEditable(allFieldsEditable);
        tfServiceCount.setEditable(allFieldsEditable);
        tfNote.setEditable(true); // luôn cho phép sửa ghi chú
    }

    private void setFields(Statistic s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tfDate.setText(sdf.format(s.getStatDate()));
        tfPeriod.setText(s.getStatPeriod());
        tfRevenue.setText(String.valueOf(s.getRevenue()));
        tfRoomRevenue.setText(String.valueOf(s.getRoomRevenue()));
        tfServiceRevenue.setText(String.valueOf(s.getServiceRevenue()));
        tfCustomerCount.setText(String.valueOf(s.getCustomerCount()));
        tfRoomRentedCount.setText(String.valueOf(s.getRoomRentedCount()));
        tfServiceCount.setText(String.valueOf(s.getServiceCount()));
        tfNote.setText(s.getNote());
        this.statistic = s;
    }

    private void save() {
        try {
            if (allowAllFields) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(tfDate.getText().trim());
                String period = tfPeriod.getText().trim();
                double revenue = Double.parseDouble(tfRevenue.getText().trim());
                double roomRevenue = Double.parseDouble(tfRoomRevenue.getText().trim());
                double serviceRevenue = Double.parseDouble(tfServiceRevenue.getText().trim());
                int customerCount = Integer.parseInt(tfCustomerCount.getText().trim());
                int roomRentedCount = Integer.parseInt(tfRoomRentedCount.getText().trim());
                int serviceCount = Integer.parseInt(tfServiceCount.getText().trim());
                String note = tfNote.getText().trim();
                if (statistic == null) statistic = new Statistic();
                statistic.setStatDate(date);
                statistic.setStatPeriod(period);
                statistic.setRevenue(revenue);
                statistic.setRoomRevenue(roomRevenue);
                statistic.setServiceRevenue(serviceRevenue);
                statistic.setCustomerCount(customerCount);
                statistic.setRoomRentedCount(roomRentedCount);
                statistic.setServiceCount(serviceCount);
                statistic.setNote(note);
            } else {
                // Chỉ cho phép sửa ghi chú
                String note = tfNote.getText().trim();
                if (statistic != null) {
                    statistic.setNote(note);
                }
            }
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!\n" + ex.getMessage());
        }
    }

    public boolean isSaved() { return saved; }
    public Statistic getStatistic() { return statistic; }
}
