package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.*;
import com.ql_khach_san.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DialogChiTiet extends JDialog {

    private static final Color BLUE_COLOR = new Color(33, 150, 243);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] ROOM_DETAIL_COLUMNS = {
        "Phòng", "Ngày Đặt", "Ngày Nhận Phòng", 
        "Ngày Trả Phòng Dự Kiến", "Ngày Trả Phòng", 
        "Tổng Thời Gian", "Tiền Phòng"
    };
    private static final String[] SERVICE_ADDED_COLUMNS = {
        "Dịch Vụ", "Thời gian", "Số Lượng", "Đơn Giá", "Thành Tiền", "ID"
    };
    private static final String[] SERVICE_AVAILABLE_COLUMNS = {
        "Tên Dịch Vụ", "Giá", "ID"
    };

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CheckinDAO checkinDAO = new CheckinDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceUsageDAO serviceUsageDAO = new ServiceUsageDAO();
    
    // UI Components
    private final RoomView roomView;
    private JLabel lblRoomNumber, lblKhachHang, lblNgayTraPhong, lblGiaPhongTheoNgay, lblTotal, lblTotalService;
    private JTable tableChiTiet, tableDichVuDaChon, tableDichVuSanCo;
    private DefaultTableModel availableServiceModel, addedServiceModel;
    private int currentCheckinId = -1;

    public DialogChiTiet(Frame parent, RoomView roomView) {
        super(parent, "Chi Tiết Phòng", true);
        this.roomView = roomView;
        initComponents();
        loadData();
        setSize(1080, 720);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BLUE_COLOR);
        panel.setPreferredSize(new Dimension(1080, 35));
        
        lblRoomNumber = new JLabel();
        lblRoomNumber.setFont(new Font("Arial", Font.BOLD, 20));
        lblRoomNumber.setForeground(Color.WHITE);
        panel.add(lblRoomNumber);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.add(createPanelChiTietThuePhong(), BorderLayout.NORTH);
        leftPanel.add(createPanelChiTietDichVu(), BorderLayout.CENTER);
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(createPanelThanhToan(), BorderLayout.EAST);
        
        return mainPanel;
    }
    
    private JPanel createPanelChiTietThuePhong() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createPanelHeader("Chi Tiết Thuê Phòng"), BorderLayout.NORTH);
        panel.add(createRoomDetailContent(), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createRoomDetailContent() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(BLUE_COLOR, 2));
        
        contentPanel.add(createInfoPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createTotalPanel(), BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblKhachHang = new JLabel("Khách Hàng: ");
        lblNgayTraPhong = new JLabel("Ngày Trả Phòng: ");
        lblGiaPhongTheoNgay = new JLabel("Giá Phòng Theo Ngày: ");
        lblGiaPhongTheoNgay.setForeground(Color.RED);
        
        panel.add(lblKhachHang);
        panel.add(lblNgayTraPhong);
        panel.add(lblGiaPhongTheoNgay);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        JLabel lblHoaDon = new JLabel("Hóa Đơn Chi Tiết");
        lblHoaDon.setFont(new Font("Arial", Font.BOLD, 12));
        titlePanel.add(lblHoaDon);
        
        // Table
        tableChiTiet = createStyledTable(ROOM_DETAIL_COLUMNS);
        
        tablePanel.add(titlePanel, BorderLayout.NORTH);
        tablePanel.add(tableChiTiet.getTableHeader(), BorderLayout.CENTER);
        tablePanel.add(tableChiTiet, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        lblTotal = new JLabel("Tổng Tiền Phòng: 200 (VNĐ)");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(Color.RED);
        panel.add(lblTotal);
        
        return panel;
    }

    private JPanel createPanelChiTietDichVu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createPanelHeader("Chi Tiết Dịch Vụ"), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(BLUE_COLOR, 2));
        
        // Panel chứa 2 bảng dịch vụ
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.setBackground(Color.WHITE);
        tablesPanel.add(createAddedServicePanel());
        tablesPanel.add(createAvailableServicePanel());
        
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        
        // Panel tổng tiền dịch vụ
        contentPanel.add(createServiceTotalPanel(), BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAddedServicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        
        panel.add(createTableTitle("Dịch Vụ Đã Thêm"), BorderLayout.NORTH);
        
        addedServiceModel = new DefaultTableModel(SERVICE_ADDED_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDichVuDaChon = createStyledTable(addedServiceModel);
        tableDichVuDaChon.removeColumn(tableDichVuDaChon.getColumnModel().getColumn(5));
        
        addPopupMenu(tableDichVuDaChon, "Xóa", e -> deleteSelectedServiceUsage());
        
        JScrollPane scrollPane = new JScrollPane(tableDichVuDaChon);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAvailableServicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        panel.add(createTableTitle("Dịch Vụ Sẵn Có"), BorderLayout.NORTH);
        
        availableServiceModel = new DefaultTableModel(SERVICE_AVAILABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDichVuSanCo = createStyledTable(availableServiceModel);
        tableDichVuSanCo.removeColumn(tableDichVuSanCo.getColumnModel().getColumn(2));
        
        addPopupMenu(tableDichVuSanCo, "Thêm", e -> addSelectedService());
        
        JScrollPane scrollPane = new JScrollPane(tableDichVuSanCo);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createServiceTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        lblTotalService = new JLabel("Tổng Tiền Dịch Vụ: 0 (VNĐ)");
        lblTotalService.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalService.setForeground(Color.RED);
        panel.add(lblTotalService);
        
        return panel;
    }
    
    private JPanel createPanelThanhToan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createPanelHeader("Thanh Toán"), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(BLUE_COLOR, 2));
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper methods for UI
    private JLabel createPanelHeader(String title) {
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(BLUE_COLOR);
        label.setPreferredSize(new Dimension(0, 35));
        return label;
    }
    
    private JPanel createTableTitle(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label);
        return panel;
    }
    
    private JTable createStyledTable(String[] columns) {
        return createStyledTable(new DefaultTableModel(columns, 0));
    }
    
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(BLUE_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return table;
    }
    
    private void addPopupMenu(JTable table, String menuText, java.awt.event.ActionListener action) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(menuText);
        menuItem.addActionListener(action);
        popupMenu.add(menuItem);
        
        table.addMouseListener(new MouseAdapter() {
            private void showPopup(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
        });
    }
    
    // Data loading methods
    private void loadData() {
        if (roomView == null) return;
        
        lblRoomNumber.setText("Phòng " + roomView.getRoomNumber());
        
        currentCheckinId = -1;
        int resId = roomView.getReservationId();
        if (resId > 0) {
            Checkin checkin = checkinDAO.getByReservationId(resId);
            if (checkin != null) {
                currentCheckinId = checkin.getCheckinId();
            }
        }

        loadRoomDetails();
        loadAvailableServices();
        loadServiceUsages();
    }
    
    private void loadRoomDetails() {
        int resId = roomView.getReservationId();
        if (resId <= 0) return;

        Reservation res = reservationDAO.getById(resId);
        if (res == null) return;

        String khachHang = getCustomerName(res.getCustomerId());
        String ngayTra = LocalDateTime.now().format(DATE_FORMATTER);
        String gia = getRoomPrice();
        int tongNgay = calculateTotalDays();

        Object[][] tableData = {
            {
                roomView.getRoomNumber(), 
                formatDate(res.getBookingDate()),
                getCheckinDate(),
                formatDate(res.getCheckoutDate()),
                ngayTra,
                tongNgay + " (Ngày)",
                gia + "(VNĐ)"
            }
        };

        setChiTietThuePhong(khachHang, ngayTra, gia, tableData, gia + " (VNĐ)");
    }
    
    private String getCustomerName(int customerId) {
        Customer cus = customerDAO.getById(customerId);
        return cus != null ? cus.getFullName() : "N/A";
    }
    
    private String getRoomPrice() {
        Room room = roomDAO.getById(roomView.getRoomId());
        if (room != null) {
            RoomType rt = roomTypeDAO.getById(room.getTypeId());
            if (rt != null) {
                return String.valueOf(rt.getPrice());
            }
        }
        return "N/A";
    }
    
    private String getCheckinDate() {
        if (currentCheckinId > 0) {
            Checkin checkin = checkinDAO.getById(currentCheckinId);
            if (checkin != null && checkin.getCheckinTime() != null) {
                return checkin.getCheckinTime().format(DATE_FORMATTER);
            }
        }
        return "N/A";
    }
    
    private int calculateTotalDays() {
        if (currentCheckinId <= 0) return 0;
        
        Checkin checkin = checkinDAO.getById(currentCheckinId);
        if (checkin != null && checkin.getCheckinTime() != null) {
            long days = ChronoUnit.DAYS.between(checkin.getCheckinTime(), LocalDateTime.now());
            return days == 0 ? 1 : (int) days;
        }
        return 0;
    }
    
    private String formatDate(LocalDateTime date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }
    
    private void loadAvailableServices() {
        if (availableServiceModel == null) return;
        
        availableServiceModel.setRowCount(0);
        for (Service s : serviceDAO.getAll()) {
            availableServiceModel.addRow(new Object[]{
                s.getServiceName(), 
                String.format("%.0f", s.getPrice()), 
                s.getServiceId()
            });
        }
    }

    private void loadServiceUsages() {
        if (addedServiceModel == null || currentCheckinId <= 0) {
            if (addedServiceModel != null) addedServiceModel.setRowCount(0);
            updateServiceTotal();
            return;
        }

        addedServiceModel.setRowCount(0);
        double totalService = 0;
        
        for (ServiceUsage su : serviceUsageDAO.getByCheckinId(currentCheckinId)) {
            Service s = serviceDAO.getById(su.getServiceId());
            if (s == null) continue;
            
            double price = s.getPrice();
            int qty = su.getQuantity();
            double total = price * qty;
            totalService += total;
            
            addedServiceModel.addRow(new Object[]{
                s.getServiceName(), 
                formatDate(su.getCreatedAt()),
                qty, 
                String.format("%.0f", price), 
                String.format("%.0f", total), 
                su.getUsageId()
            });
        }
        
        updateServiceTotal();
    }
    
    private void updateServiceTotal() {
        double total = 0;
        if (addedServiceModel != null) {
            for (int i = 0; i < addedServiceModel.getRowCount(); i++) {
                try {
                    String totalStr = String.valueOf(addedServiceModel.getValueAt(i, 4));
                    total += Double.parseDouble(totalStr);
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu không parse được
                }
            }
        }
        lblTotalService.setText("Tổng Tiền Dịch Vụ: " + String.format("%.0f", total) + " (VNĐ)");
    }
    
    // Service actions
    private void addSelectedService() {
        int selectedRow = tableDichVuSanCo.getSelectedRow();
        if (selectedRow < 0) return;

        if (currentCheckinId <= 0) {
            showMessage("Không tìm thấy check-in cho phòng này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tableDichVuSanCo.convertRowIndexToModel(selectedRow);
        
        try {
            int serviceId = Integer.parseInt(String.valueOf(availableServiceModel.getValueAt(modelRow, 2)));
            int quantity = getQuantityFromUser();
            
            if (quantity <= 0) return;
            
            ServiceUsage su = new ServiceUsage(0, currentCheckinId, serviceId, quantity, LocalDateTime.now());
            if (serviceUsageDAO.insert(su)) {
                loadServiceUsages();
            } else {
                showMessage("Thêm dịch vụ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            showMessage("Dữ liệu dịch vụ không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getQuantityFromUser() {
        String input = JOptionPane.showInputDialog(this, "Nhập số lượng", "1");
        if (input == null) return -1;
        
        try {
            int quantity = Integer.parseInt(input.trim());
            if (quantity <= 0) {
                showMessage("Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
            return quantity;
        } catch (NumberFormatException ex) {
            showMessage("Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
    
    private void deleteSelectedServiceUsage() {
        int selectedRow = tableDichVuDaChon.getSelectedRow();
        if (selectedRow < 0) return;

        int row = tableDichVuDaChon.convertRowIndexToModel(selectedRow);
        
        try {
            int usageId = Integer.parseInt(String.valueOf(addedServiceModel.getValueAt(row, 5)));
            
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Bạn có chắc chắn muốn xóa dịch vụ này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm != JOptionPane.YES_OPTION) return;

            if (serviceUsageDAO.delete(usageId)) {
                showMessage("Xóa dịch vụ thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadServiceUsages();
            } else {
                showMessage("Xóa dịch vụ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            showMessage("Không tìm thấy ID sử dụng dịch vụ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public void setChiTietThuePhong(String khachHang, String ngayTra, String gia, Object[][] tableData, String total) {
        lblKhachHang.setText("Khách Hàng: " + khachHang);
        lblNgayTraPhong.setText("Ngày Trả Phòng: " + ngayTra);
        lblGiaPhongTheoNgay.setText("Giá Phòng Theo Ngày: " + gia + " (VNĐ)");
        lblTotal.setText("Tổng Tiền Phòng: " + total);
        
        if (tableData != null && tableData.length > 0) {
            tableChiTiet.setModel(new DefaultTableModel(tableData, ROOM_DETAIL_COLUMNS));
        }
    }
}