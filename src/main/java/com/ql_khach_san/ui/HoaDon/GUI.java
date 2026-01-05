/*
 * Giao diện quản lý và xem hoá đơn
 */
package com.ql_khach_san.ui.HoaDon;

import com.ql_khach_san.dao.*;
import com.ql_khach_san.model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnViewDetail;
    
    private InvoiceDAO invoiceDao = new InvoiceDAO();
    private CheckinDAO checkinDao = new CheckinDAO();
    private ReservationDAO reservationDao = new ReservationDAO();
    private CustomerDAO customerDao = new CustomerDAO();
    private RoomDAO roomDao = new RoomDAO();
    private EmployeeDAO employeeDao = new EmployeeDAO();

    private List<Invoice> invoiceList = new ArrayList<>();
    private List<Invoice> displayedList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public GUI() {
        setTitle("Quản Lý Hoá Đơn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        Color bg = new Color(224, 236, 243);
        
        // Title
        JLabel lblTitle = new JLabel("Quản Lý Hoá Đơn");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(bg);
        titlePanel.add(lblTitle);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setBackground(bg);
        JLabel lblSearch = new JLabel("Tìm kiếm:"); 
        lblSearch.setForeground(Color.DARK_GRAY);
        txtSearch = new JTextField(28);
        txtSearch.setToolTipText("Nhập mã hoá đơn, mã phòng, hoặc tên khách hàng...");
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);

        // Table
        tableModel = new DefaultTableModel(new Object[]{
            "Mã HĐ", "Mã Phòng", "Khách Hàng", "Tiền Phòng", "Tiền Dịch Vụ", "Tổng Tiền", "Ngày Tạo"
        }, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        int[] colWidths = {80, 80, 150, 100, 100, 100, 150};
        for (int i = 0; i < colWidths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btnViewDetail = new JButton("Xem Chi Tiết");
        buttonPanel.add(btnViewDetail);
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bg);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Setup layout
        getContentPane().setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tableScroll, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadTable();

        // Search listener
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadTable(); }
            public void removeUpdate(DocumentEvent e) { loadTable(); }
            public void changedUpdate(DocumentEvent e) { loadTable(); }
        });

        // Button listeners
        btnViewDetail.addActionListener(e -> onViewDetail());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) onViewDetail();
            }
        });

        setSize(1100, 650);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        invoiceList = invoiceDao.getAll();
        displayedList.clear();

        String q = txtSearch == null ? "" : txtSearch.getText().trim().toLowerCase();

        for (Invoice inv : invoiceList) {
            Checkin checkin = checkinDao.getById(inv.getCheckinId());
            if (checkin == null) continue;

            Reservation res = reservationDao.getById(checkin.getReservationId());
            if (res == null) continue;

            Customer customer = customerDao.getById(res.getCustomerId());
            Room room = roomDao.getById(res.getRoomId());

            String customerName = customer == null ? "" : customer.getFullName();
            String roomNumber = room == null ? "" : room.getRoomNumber();
            String combined = (inv.getInvoiceId() + " " + roomNumber + " " + customerName).toLowerCase();

            if (!q.isEmpty() && !combined.contains(q)) continue;

            String createdAt = inv.getCreatedAt() != null ? 
                dateFormat.format(java.sql.Timestamp.valueOf(inv.getCreatedAt())) : "";

            tableModel.addRow(new Object[]{
                inv.getInvoiceId(),
                roomNumber,
                customerName,
                String.format("%.0f", inv.getRoomFee()),
                String.format("%.0f", inv.getServiceFee()),
                String.format("%.0f", inv.getTotalAmount()),
                createdAt
            });
            displayedList.add(inv);
        }
    }

    private void onViewDetail() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= displayedList.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hoá đơn để xem");
            return;
        }

        Invoice invoice = displayedList.get(row);
        new InvoiceDetailDialog(this, invoice, checkinDao, reservationDao, customerDao, roomDao, employeeDao).setVisible(true);
    }
}

