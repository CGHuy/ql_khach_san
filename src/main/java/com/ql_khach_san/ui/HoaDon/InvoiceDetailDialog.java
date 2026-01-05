/*
 * Dialog xem chi tiết hoá đơn
 */
package com.ql_khach_san.ui.HoaDon;

import com.ql_khach_san.dao.*;
import com.ql_khach_san.model.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailDialog extends JDialog {

    private Invoice invoice;
    private CheckinDAO checkinDao;
    private ReservationDAO reservationDao;
    private CustomerDAO customerDao;
    private RoomDAO roomDao;
    private EmployeeDAO employeeDao;
    private ServiceUsageDAO serviceUsageDao = new ServiceUsageDAO();
    private ServiceDAO serviceDao = new ServiceDAO();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public InvoiceDetailDialog(Frame parent, Invoice invoice, CheckinDAO checkinDao, 
                               ReservationDAO reservationDao, CustomerDAO customerDao, 
                               RoomDAO roomDao, EmployeeDAO employeeDao) {
        super(parent, "Chi Tiết Hoá Đơn", true);
        this.invoice = invoice;
        this.checkinDao = checkinDao;
        this.reservationDao = reservationDao;
        this.customerDao = customerDao;
        this.roomDao = roomDao;
        this.employeeDao = employeeDao;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header
        JLabel lblTitle = new JLabel("CHI TIẾT HÓA ĐƠN #" + invoice.getInvoiceId());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(12));

        // Get related data
        Checkin checkin = checkinDao.getById(invoice.getCheckinId());
        Reservation reservation = null;
        Customer customer = null;
        Room room = null;
        Employee employee = null;

        if (checkin != null) {
            reservation = reservationDao.getById(checkin.getReservationId());
            if (reservation != null) {
                customer = customerDao.getById(reservation.getCustomerId());
                room = roomDao.getById(reservation.getRoomId());
            }
        }
        employee = employeeDao.getById(invoice.getEmployeeId());

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Cơ Bản"));

        addInfoRow(infoPanel, "Mã Hoá Đơn:", String.valueOf(invoice.getInvoiceId()));
        addInfoRow(infoPanel, "Phòng:", room != null ? room.getRoomNumber() : "N/A");
        addInfoRow(infoPanel, "Khách Hàng:", customer != null ? customer.getFullName() : "N/A");
        addInfoRow(infoPanel, "Điện Thoại:", customer != null ? customer.getPhone() : "N/A");
        addInfoRow(infoPanel, "CCCD:", customer != null ? customer.getCccd() : "N/A");
        addInfoRow(infoPanel, "Nhân Viên:", employee != null ? employee.getFullName() : "N/A");

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(12));

        // Check-in/Check-out info
        if (checkin != null) {
            JPanel timePanel = new JPanel(new GridLayout(2, 2, 10, 10));
            timePanel.setBorder(BorderFactory.createTitledBorder("Thời Gian Lưu Trú"));
            
            addInfoRow(timePanel, "Nhận Phòng:", 
                checkin.getCheckinTime() != null ? dateFormat.format(java.sql.Timestamp.valueOf(checkin.getCheckinTime())) : "N/A");
            addInfoRow(timePanel, "Trả Phòng:", 
                checkin.getCheckoutTime() != null ? dateFormat.format(java.sql.Timestamp.valueOf(checkin.getCheckoutTime())) : "Chưa trả");

            mainPanel.add(timePanel);
            mainPanel.add(Box.createVerticalStrut(12));
        }

        // Service usage
        if (checkin != null) {
            List<ServiceUsage> serviceUsages = serviceUsageDao.getByCheckinId(checkin.getCheckinId());
            if (serviceUsages != null && !serviceUsages.isEmpty()) {
                JPanel servicePanel = new JPanel(new BorderLayout());
                servicePanel.setBorder(BorderFactory.createTitledBorder("Dịch Vụ Sử Dụng"));

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

                StringBuilder sb = new StringBuilder();
                for (ServiceUsage su : serviceUsages) {
                    Service svc = serviceDao.getById(su.getServiceId());
                    if (svc != null) {
                        sb.append(String.format("- %s (x%d): %.0f VNĐ\n", 
                            svc.getServiceName(), su.getQuantity(), svc.getPrice() * su.getQuantity()));
                    }
                }
                textArea.setText(sb.toString());

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 80));
                servicePanel.add(scrollPane, BorderLayout.CENTER);

                mainPanel.add(servicePanel);
                mainPanel.add(Box.createVerticalStrut(12));
            }
        }

        // Total panel
        JPanel totalPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        totalPanel.setBorder(BorderFactory.createTitledBorder("Chi Tiết Thanh Toán"));

        addInfoRow(totalPanel, "Tiền Phòng:", String.format("%.0f VNĐ", invoice.getRoomFee()));
        addInfoRow(totalPanel, "Tiền Dịch Vụ:", String.format("%.0f VNĐ", invoice.getServiceFee()));
        addInfoRow(totalPanel, "Tổng Tiền:", String.format("%.0f VNĐ", invoice.getTotalAmount()));
        addInfoRow(totalPanel, "Ngày Tạo:", 
            invoice.getCreatedAt() != null ? dateFormat.format(java.sql.Timestamp.valueOf(invoice.getCreatedAt())) : "N/A");

        mainPanel.add(totalPanel);
        mainPanel.add(Box.createVerticalStrut(12));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        getContentPane().add(scrollPane);
        setSize(600, 700);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(lblLabel.getFont().deriveFont(Font.BOLD));
        JLabel lblValue = new JLabel(value != null ? value : "");
        panel.add(lblLabel);
        panel.add(lblValue);
    }
}