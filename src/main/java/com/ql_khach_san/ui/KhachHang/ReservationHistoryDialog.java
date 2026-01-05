package com.ql_khach_san.ui.KhachHang;

import com.ql_khach_san.dao.CheckinDAO;
import com.ql_khach_san.dao.ReservationDAO;
import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.model.Checkin;
import com.ql_khach_san.model.Reservation;
import com.ql_khach_san.model.Room;
import com.ql_khach_san.model.RoomType;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationHistoryDialog extends JDialog {

    private int customerId;
    private String customerName;
    private List<Reservation> reservations;
    private int currentIndex = 0;

    private JLabel lblCustomerName, lblRoomNumber, lblRoomType, lblBookingDate, lblCheckinExpected, lblCheckoutExpected, lblStatus, lblCheckinActual, lblCheckoutActual;
    private JButton btnPrevious, btnNext, btnClose;
    private JLabel lblPageInfo;

    private ReservationDAO reservationDAO = new ReservationDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private CheckinDAO checkinDAO = new CheckinDAO();

    public ReservationHistoryDialog(Frame owner, int customerId, String customerName) {
        super(owner, "Lịch sử đặt phòng - " + customerName, true);
        this.customerId = customerId;
        this.customerName = customerName;
        initComponents();
        loadData();
        setPreferredSize(new Dimension(680, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Main with padding
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lblTitle = new JLabel("Lịch sử đặt phòng", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content panel
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 13);

        int row = 0;

        // Helper to add row: label + value
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel l1 = new JLabel("Khách hàng:");
        l1.setFont(labelFont);
        content.add(l1, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblCustomerName = new JLabel(customerName);
        lblCustomerName.setFont(valueFont);
        lblCustomerName.setOpaque(true);
        lblCustomerName.setBackground(new Color(250, 250, 250));
        lblCustomerName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblCustomerName, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l2 = new JLabel("Số phòng:");
        l2.setFont(labelFont);
        content.add(l2, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblRoomNumber = new JLabel("");
        lblRoomNumber.setFont(valueFont);
        lblRoomNumber.setOpaque(true);
        lblRoomNumber.setBackground(new Color(250, 250, 250));
        lblRoomNumber.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblRoomNumber, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l3 = new JLabel("Loại phòng:");
        l3.setFont(labelFont);
        content.add(l3, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblRoomType = new JLabel("");
        lblRoomType.setFont(valueFont);
        lblRoomType.setOpaque(true);
        lblRoomType.setBackground(new Color(250, 250, 250));
        lblRoomType.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblRoomType, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l4 = new JLabel("Ngày đặt:");
        l4.setFont(labelFont);
        content.add(l4, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblBookingDate = new JLabel("");
        lblBookingDate.setFont(valueFont);
        lblBookingDate.setOpaque(true);
        lblBookingDate.setBackground(new Color(250, 250, 250));
        lblBookingDate.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblBookingDate, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l5 = new JLabel("Check-in dự kiến:");
        l5.setFont(labelFont);
        content.add(l5, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblCheckinExpected = new JLabel("");
        lblCheckinExpected.setFont(valueFont);
        lblCheckinExpected.setOpaque(true);
        lblCheckinExpected.setBackground(new Color(250, 250, 250));
        lblCheckinExpected.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblCheckinExpected, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l6 = new JLabel("Check-out dự kiến:");
        l6.setFont(labelFont);
        content.add(l6, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblCheckoutExpected = new JLabel("");
        lblCheckoutExpected.setFont(valueFont);
        lblCheckoutExpected.setOpaque(true);
        lblCheckoutExpected.setBackground(new Color(250, 250, 250));
        lblCheckoutExpected.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblCheckoutExpected, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l7 = new JLabel("Trạng thái:");
        l7.setFont(labelFont);
        content.add(l7, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblStatus = new JLabel("");
        lblStatus.setFont(valueFont);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(250, 250, 250));
        lblStatus.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblStatus, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l8 = new JLabel("Check-in thực tế:");
        l8.setFont(labelFont);
        content.add(l8, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblCheckinActual = new JLabel("");
        lblCheckinActual.setFont(valueFont);
        lblCheckinActual.setOpaque(true);
        lblCheckinActual.setBackground(new Color(250, 250, 250));
        lblCheckinActual.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblCheckinActual, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel l9 = new JLabel("Check-out thực tế:");
        l9.setFont(labelFont);
        content.add(l9, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblCheckoutActual = new JLabel("");
        lblCheckoutActual.setFont(valueFont);
        lblCheckoutActual.setOpaque(true);
        lblCheckoutActual.setBackground(new Color(250, 250, 250));
        lblCheckoutActual.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        content.add(lblCheckoutActual, gbc);

        mainPanel.add(content, BorderLayout.CENTER);

        // Bottom panel with navigation buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bottomPanel.setBackground(Color.WHITE);

        btnPrevious = new JButton("< Trước");
        btnPrevious.setPreferredSize(new Dimension(110, 34));
        btnPrevious.addActionListener(e -> previousReservation());
        bottomPanel.add(btnPrevious);

        lblPageInfo = new JLabel("");
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bottomPanel.add(lblPageInfo);

        btnNext = new JButton("Tiếp theo >");
        btnNext.setPreferredSize(new Dimension(110, 34));
        btnNext.addActionListener(e -> nextReservation());
        bottomPanel.add(btnNext);

        btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 34));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadData() {
        reservations = reservationDAO.getByCustomerId(customerId);
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Khách hàng này chưa có lịch sử đặt phòng.");
            dispose();
        } else {
            displayReservation(0);
        }
    }

    private void displayReservation(int index) {
        if (index < 0 || index >= reservations.size()) {
            return;
        }

        currentIndex = index;
        Reservation r = reservations.get(index);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Get room info
        Room room = roomDAO.getById(r.getRoomId());
        String roomNumber = room != null ? room.getRoomNumber() : "N/A";
        String roomTypeName = "N/A";
        if (room != null) {
            RoomType rt = roomTypeDAO.getById(room.getTypeId());
            if (rt != null) roomTypeName = rt.getTypeName();
        }

        // Get checkin info
        Checkin checkin = checkinDAO.getByReservationId(r.getReservationId());
        String checkinActual = checkin != null && checkin.getCheckinTime() != null ? checkin.getCheckinTime().format(fmt) : "Chưa check-in";
        String checkoutActual = checkin != null && checkin.getCheckoutTime() != null ? checkin.getCheckoutTime().format(fmt) : "Chưa check-out";

        // Update labels
        lblCustomerName.setText(customerName);
        lblRoomNumber.setText(roomNumber);
        lblRoomType.setText(roomTypeName);
        lblBookingDate.setText(r.getBookingDate() != null ? r.getBookingDate().format(fmt) : "");
        lblCheckinExpected.setText(r.getCheckinDate() != null ? r.getCheckinDate().format(fmt) : "");
        lblCheckoutExpected.setText(r.getCheckoutDate() != null ? r.getCheckoutDate().format(fmt) : "");
        lblStatus.setText(r.getStatus() != null ? r.getStatus() : "");
        lblCheckinActual.setText(checkinActual);
        lblCheckoutActual.setText(checkoutActual);

        // Update navigation info
        lblPageInfo.setText((currentIndex + 1) + " / " + reservations.size());

        // Enable/disable buttons
        btnPrevious.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < reservations.size() - 1);
    }

    private void previousReservation() {
        if (currentIndex > 0) {
            displayReservation(currentIndex - 1);
        }
    }

    private void nextReservation() {
        if (currentIndex < reservations.size() - 1) {
            displayReservation(currentIndex + 1);
        }
    }
}
