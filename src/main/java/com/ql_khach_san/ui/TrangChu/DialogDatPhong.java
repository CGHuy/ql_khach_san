package com.ql_khach_san.ui.TrangChu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDatPhong extends JDialog {
    public DialogDatPhong(JFrame parent, String roomNumber) {
        super(parent, "Đặt Phòng - " + roomNumber, true); // true để làm modal
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Số Phòng:"));
        JTextField txtRoom = new JTextField(roomNumber);
        txtRoom.setEditable(false); // Không cho sửa số phòng
        inputPanel.add(txtRoom);

        inputPanel.add(new JLabel("Tên Khách Hàng:"));
        JTextField txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Số Điện Thoại:"));
        JTextField txtPhone = new JTextField();
        inputPanel.add(txtPhone);

        add(inputPanel, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel();
        JButton btnSave = new JButton("Xác Nhận Đặt");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            String name = txtName.getText();
            String phone = txtPhone.getText();
            
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            } else {
                // Ở đây bạn sẽ gọi tới Service/DAO để lưu vào Database
                JOptionPane.showMessageDialog(this, "Đặt phòng " + roomNumber + " thành công cho " + name);
                dispose(); // Đóng dialog
            }
        });

        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}