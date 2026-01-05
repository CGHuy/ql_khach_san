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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationHistoryDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private int customerId;
    private String customerName;

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
        setSize(1000, 400);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new String[]{
                "Họ tên", "Số phòng", "Loại phòng", "Ngày đặt", "Ngày check-in dự kiến", "Ngày check-out dự kiến", "Trạng thái đặt phòng", "Check-in thực tế", "Check-out thực tế"
        }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Reservation> reservations = reservationDAO.getByCustomerId(customerId);
        for (Reservation r : reservations) {
            Room room = roomDAO.getById(r.getRoomId());
            String roomNumber = room != null ? room.getRoomNumber() : "";
            String roomTypeName = "";
            if (room != null) {
                RoomType rt = roomTypeDAO.getById(room.getTypeId());
                if (rt != null) roomTypeName = rt.getTypeName();
            }
            Checkin checkin = checkinDAO.getByReservationId(r.getReservationId());
            String booking = r.getBookingDate() != null ? r.getBookingDate().format(fmt) : "";
            String expectIn = r.getCheckinDate() != null ? r.getCheckinDate().format(fmt) : "";
            String expectOut = r.getCheckoutDate() != null ? r.getCheckoutDate().format(fmt) : "";
            String actualIn = checkin != null && checkin.getCheckinTime() != null ? checkin.getCheckinTime().format(fmt) : "";
            String actualOut = checkin != null && checkin.getCheckoutTime() != null ? checkin.getCheckoutTime().format(fmt) : "";

            tableModel.addRow(new Object[]{customerName, roomNumber, roomTypeName, booking, expectIn, expectOut, r.getStatus(), actualIn, actualOut});
        }
    }
}
