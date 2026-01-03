package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.model.Customer;
import com.ql_khach_san.dao.CustomerDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogDatPhong extends JDialog {
    private JComboBox<Customer> cbCustomer;
    private JTextField txtCheckIn, txtCheckOut;
    private RoomService roomService = new RoomService();

    public DialogDatPhong(MainFrame parent, RoomView v) {
        super(parent, "Đặt Phòng - " + v.getRoomNumber(), true);
        setSize(600, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // 1. Panel nhập liệu chính
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        inputPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Dòng 1: Số phòng
        inputPanel.add(new JLabel("Số Phòng:"));
        inputPanel.add(new JLabel("<html><b>" + v.getRoomNumber() + "</b></html>"));

        // Dòng 2: Khách hàng (Sử dụng JComboBox)
        inputPanel.add(new JLabel("Chọn Khách Hàng:"));
        cbCustomer = new JComboBox<>();
        loadCustomerData(); // Tải dữ liệu từ DB
        inputPanel.add(cbCustomer);

        // Dòng 3: Thời gian đặt (Lấy giờ hiện tại)
        inputPanel.add(new JLabel("Thời Gian Đặt:"));
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        inputPanel.add(new JLabel(now));

        // Dòng 4: Dự kiến Check-in
        inputPanel.add(new JLabel("Dự Kiến Check-in:"));
        txtCheckIn = new JTextField();
        txtCheckIn.setToolTipText("Định dạng: dd/mm/yyyy hh:mm");
        inputPanel.add(txtCheckIn);

        // Dòng 5: Dự kiến Check-out
        inputPanel.add(new JLabel("Dự Kiến Check-out:"));
        txtCheckOut = new JTextField();
        txtCheckOut.setToolTipText("Định dạng: dd/mm/yyyy hh:mm");
        inputPanel.add(txtCheckOut);

        add(inputPanel, BorderLayout.CENTER);

        // 2. Nút bấm
        JPanel buttonPanel = new JPanel();
        JButton btnSave = new JButton("Xác Nhận Đặt");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            Customer selected = (Customer) cbCustomer.getSelectedItem();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!");
                return;
            }
            
            try {
                LocalDateTime checkInTime = null;
                LocalDateTime checkOutTime = null;

                if (!txtCheckIn.getText().trim().isEmpty()) {
                    checkInTime = LocalDateTime.parse(txtCheckIn.getText().trim(), formatter);
                }
                if (!txtCheckOut.getText().trim().isEmpty()) {
                    checkOutTime = LocalDateTime.parse(txtCheckOut.getText().trim(), formatter);
                }

                boolean success = roomService.makeReservation(v.getRoomId(), selected.getCustomerId(), checkInTime, checkOutTime);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Đã tạo đơn đặt phòng thành công!");
                    parent.refreshRoomPanel(); // Vẽ lại màu cam cho MainFrame
                    dispose();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày tháng không đúng (dd/MM/yyyy HH:mm)");
            }
        });

        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCustomerData() {
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers = customerDAO.getAll(); // Lấy từ bảng customer
        for (Customer c : customers) {
            cbCustomer.addItem(c);
        }
        
        // Cài đặt trình renderer để hiển thị Tên - SĐT khách hàng trong ComboBox
        cbCustomer.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    Customer c = (Customer) value;
                    setText(c.getFullName() + " (" + c.getPhone() + ")");
                }
                return this;
            }
        });
    }
}