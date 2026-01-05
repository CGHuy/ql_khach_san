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
        setSize(700, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(255, 182, 193));

        // Title
        JLabel lblTitle = new JLabel("Chi tiết lịch sử đặt phòng", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 10, 700, 30);
        mainPanel.add(lblTitle);

        // Info Panel
        int y = 60;
        int labelWidth = 150;
        int valueWidth = 520;

        // Khách hàng
        JLabel lbl1 = new JLabel("Khách hàng:");
        lbl1.setForeground(Color.WHITE);
        lbl1.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl1);
        lblCustomerName = new JLabel(customerName);
        lblCustomerName.setBounds(180, y, valueWidth, 25);
        lblCustomerName.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblCustomerName);
        y += 40;

        // Số phòng
        JLabel lbl2 = new JLabel("Số phòng:");
        lbl2.setForeground(Color.WHITE);
        lbl2.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl2);
        lblRoomNumber = new JLabel("");
        lblRoomNumber.setBounds(180, y, valueWidth, 25);
        lblRoomNumber.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblRoomNumber);
        y += 40;

        // Loại phòng
        JLabel lbl3 = new JLabel("Loại phòng:");
        lbl3.setForeground(Color.WHITE);
        lbl3.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl3);
        lblRoomType = new JLabel("");
        lblRoomType.setBounds(180, y, valueWidth, 25);
        lblRoomType.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblRoomType);
        y += 40;

        // Ngày đặt
        JLabel lbl4 = new JLabel("Ngày đặt:");
        lbl4.setForeground(Color.WHITE);
        lbl4.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl4);
        lblBookingDate = new JLabel("");
        lblBookingDate.setBounds(180, y, valueWidth, 25);
        lblBookingDate.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblBookingDate);
        y += 40;

        // Ngày check-in dự kiến
        JLabel lbl5 = new JLabel("Check-in dự kiến:");
        lbl5.setForeground(Color.WHITE);
        lbl5.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl5);
        lblCheckinExpected = new JLabel("");
        lblCheckinExpected.setBounds(180, y, valueWidth, 25);
        lblCheckinExpected.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblCheckinExpected);
        y += 40;

        // Ngày check-out dự kiến
        JLabel lbl6 = new JLabel("Check-out dự kiến:");
        lbl6.setForeground(Color.WHITE);
        lbl6.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl6);
        lblCheckoutExpected = new JLabel("");
        lblCheckoutExpected.setBounds(180, y, valueWidth, 25);
        lblCheckoutExpected.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblCheckoutExpected);
        y += 40;

        // Trạng thái
        JLabel lbl7 = new JLabel("Trạng thái:");
        lbl7.setForeground(Color.WHITE);
        lbl7.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl7);
        lblStatus = new JLabel("");
        lblStatus.setBounds(180, y, valueWidth, 25);
        lblStatus.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblStatus);
        y += 40;

        // Check-in thực tế
        JLabel lbl8 = new JLabel("Check-in thực tế:");
        lbl8.setForeground(Color.WHITE);
        lbl8.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl8);
        lblCheckinActual = new JLabel("");
        lblCheckinActual.setBounds(180, y, valueWidth, 25);
        lblCheckinActual.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblCheckinActual);
        y += 40;

        // Check-out thực tế
        JLabel lbl9 = new JLabel("Check-out thực tế:");
        lbl9.setForeground(Color.WHITE);
        lbl9.setBounds(20, y, labelWidth, 25);
        mainPanel.add(lbl9);
        lblCheckoutActual = new JLabel("");
        lblCheckoutActual.setBounds(180, y, valueWidth, 25);
        lblCheckoutActual.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(lblCheckoutActual);

        // Bottom panel with navigation
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(255, 182, 193));

        btnPrevious = new JButton("< Trước");
        btnPrevious.addActionListener(e -> previousReservation());
        bottomPanel.add(btnPrevious);

        lblPageInfo = new JLabel("");
        lblPageInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPageInfo.setForeground(Color.WHITE);
        bottomPanel.add(lblPageInfo);

        btnNext = new JButton("Tiếp theo >");
        btnNext.addActionListener(e -> nextReservation());
        bottomPanel.add(btnNext);

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        mainPanel.add(bottomPanel);
        bottomPanel.setBounds(0, 430, 700, 70);

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
