package com.ql_khach_san.ui.TrangChu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogNhanPhong extends JDialog {
    public DialogNhanPhong(JFrame parent, String roomNumber) {
        super(parent, "Xác Nhận Nhận Phòng - " + roomNumber, true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Phòng:"));
        panel.add(new JLabel(roomNumber));

        // Giả sử lấy thông tin cũ từ database lên để hiển thị
        panel.add(new JLabel("Tên khách:"));
        panel.add(new JLabel("Nguyễn Văn A")); 

        panel.add(new JLabel("Tiền cọc thêm:"));
        JTextField txtDeposit = new JTextField("0");
        panel.add(txtDeposit);

        panel.add(new JLabel("Ghi chú:"));
        JTextField txtNote = new JTextField();
        panel.add(txtNote);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnConfirm = new JButton("Xác Nhận Nhận Phòng");
        btnConfirm.setBackground(new Color(0, 150, 0));
        btnConfirm.setForeground(Color.WHITE);
        
        btnConfirm.addActionListener(e -> {
            // Logic cập nhật trạng thái phòng từ "Đã đặt" sang "Đã thuê" (Màu Đỏ)
            JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái phòng " + roomNumber + " sang 'Đã thuê'");
            dispose();
        });

        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);
    }
}