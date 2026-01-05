package com.ql_khach_san.ui.ThongKe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticDialog extends JDialog {
    private boolean saved = false;
    private Statistic statistic;
    private JTextField tfDate, tfPeriod, tfRevenue, tfRoomRevenue, tfServiceRevenue, tfCustomerCount, tfRoomRentedCount, tfNote;
    private JButton btnSave, btnCancel;
    private boolean allowAllFields;

    // allowAllFields = true: cho phép nhập tất cả, false: chỉ cho phép sửa ghi chú
    public StatisticDialog(Statistic s, boolean allowAllFields) {
        this.allowAllFields = allowAllFields;
        setTitle(s == null ? "Thêm thống kê" : "Sửa ghi chú" );
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2, 5, 5));
        initFields();
        if (s != null) setFields(s);
        btnSave = new JButton("OK");
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
        tfNote.setText(s.getNote());
        this.statistic = s;
    }

    private void save() {
        // Persistence disabled in live-only mode; dialog is view-only
        JOptionPane.showMessageDialog(this, "Lưu đã bị vô hiệu hoá; dialog chỉ để xem/ghi chú cục bộ.");
        saved = false;
        dispose();
    }

    public boolean isSaved() { return saved; }
    public Statistic getStatistic() { return statistic; }
}
