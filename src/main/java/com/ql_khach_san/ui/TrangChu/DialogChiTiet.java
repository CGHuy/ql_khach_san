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
    private static final String[] ROOM_DETAIL_COLUMNS = {"Phòng", "Ngày Đặt", "Ngày Nhận Phòng", 
        "Ngày Trả Phòng Dự Kiến", "Ngày Trả Phòng", "Tổng Thời Gian", "Tiền Phòng"};
    private static final String[] SERVICE_ADDED_COLUMNS = {"Dịch Vụ", "Thời gian", "Số Lượng", 
        "Đơn Giá", "Thành Tiền", "ID"};
    private static final String[] SERVICE_AVAILABLE_COLUMNS = {"Tên Dịch Vụ", "Giá", "ID"};

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CheckinDAO checkinDAO = new CheckinDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceUsageDAO serviceUsageDAO = new ServiceUsageDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final RoomService roomService = new RoomService();
    
    private final RoomView roomView;
    private final MainFrame mainFrame;
    private final int employeeId;
    private int currentCheckinId = -1;
    
    private JLabel lblRoomNumber, lblKhachHang, lblNgayTraPhong, lblGiaPhongTheoNgay;
    private JLabel lblTotal, lblTotalService, lblTotalRoomPayment, lblTotalServicePayment, lblTotalPayment;
    private JTable tableChiTiet, tableDichVuDaChon, tableDichVuSanCo;
    private DefaultTableModel availableServiceModel, addedServiceModel;
    private JButton btnThanhToan;

    public DialogChiTiet(Frame parent, RoomView roomView) {
        this(parent, roomView, -1);
    }

    public DialogChiTiet(Frame parent, RoomView roomView, int employeeId) {
        super(parent, "Chi Tiết Phòng", true);
        this.roomView = roomView;
        this.mainFrame = (MainFrame) parent;
        this.employeeId = employeeId;
        initComponents();
        loadData();
        setSize(1300, 700);
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
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        JLabel lblHoaDon = new JLabel("Hóa Đơn Chi Tiết");
        lblHoaDon.setFont(new Font("Arial", Font.BOLD, 12));
        titlePanel.add(lblHoaDon);
        
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
        
        lblTotal = createLabel("Tổng Tiền Phòng: 0 (VNĐ)", 14, Color.RED, Font.BOLD);
        panel.add(lblTotal);
        
        return panel;
    }

    private JPanel createPanelChiTietDichVu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createPanelHeader("Chi Tiết Dịch Vụ"), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(BLUE_COLOR, 2));
        
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.setBackground(Color.WHITE);
        tablesPanel.add(createAddedServicePanel());
        tablesPanel.add(createAvailableServicePanel());
        
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        contentPanel.add(createServiceTotalPanel(), BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAddedServicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        
        panel.add(createTableTitle("Dịch Vụ Đã Thêm"), BorderLayout.NORTH);
        
        addedServiceModel = createNonEditableModel(SERVICE_ADDED_COLUMNS);
        tableDichVuDaChon = createStyledTable(addedServiceModel);
        tableDichVuDaChon.removeColumn(tableDichVuDaChon.getColumnModel().getColumn(5));
        
        addPopupMenu(tableDichVuDaChon, "Xóa", e -> deleteSelectedServiceUsage());
        
        panel.add(createScrollPanel(tableDichVuDaChon), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAvailableServicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        panel.add(createTableTitle("Dịch Vụ Sẵn Có"), BorderLayout.NORTH);
        
        availableServiceModel = createNonEditableModel(SERVICE_AVAILABLE_COLUMNS);
        tableDichVuSanCo = createStyledTable(availableServiceModel);
        tableDichVuSanCo.removeColumn(tableDichVuSanCo.getColumnModel().getColumn(2));
        
        addPopupMenu(tableDichVuSanCo, "Thêm", e -> addSelectedService());
        
        panel.add(createScrollPanel(tableDichVuSanCo), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createServiceTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        lblTotalService = createLabel("Tổng Tiền Dịch Vụ: 0 (VNĐ)", 14, Color.RED, Font.BOLD);
        panel.add(lblTotalService);
        
        return panel;
    }
    
    private JPanel createPanelThanhToan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createPanelHeader("Thanh Toán"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(BLUE_COLOR, 2));

        lblTotalRoomPayment = createCenteredLabel("Tổng Tiền Phòng: 0 (VNĐ)", 14, Color.BLACK);
        lblTotalServicePayment = createCenteredLabel("Tổng Tiền Dịch Vụ: 0 (VNĐ)", 14, Color.BLACK);
        lblTotalPayment = createCenteredLabel("Tổng Tiền Phải Thanh Toán: 0 (VNĐ)", 14, Color.RED);

        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 15));
        btnThanhToan.setBackground(BLUE_COLOR);
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnThanhToan.addActionListener(e -> handlePayment());

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(lblTotalRoomPayment);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblTotalServicePayment);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(lblTotalPayment);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(btnThanhToan);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    // Các hàm hỗ trợ UI
    private JLabel createPanelHeader(String title) {
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
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
    
    private DefaultTableModel createNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    
    private JPanel createScrollPanel(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createLabel(String text, int fontSize, Color color, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, fontSize));
        label.setForeground(color);
        return label;
    }
    
    private JLabel createCenteredLabel(String text, int fontSize, Color color) {
        JLabel label = createLabel(text, fontSize, color, Font.BOLD);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
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
        
        lblRoomNumber.setText("PHÒNG " + roomView.getRoomNumber());
        currentCheckinId = getCheckinId();
        
        loadRoomDetails();
        loadAvailableServices();
        loadServiceUsages();
        loadPaymentData();
    }
    
    private int getCheckinId() {
        int resId = roomView.getReservationId();
        if (resId <= 0) return -1;
        
        Checkin checkin = checkinDAO.getByReservationId(resId);
        return checkin != null ? checkin.getCheckinId() : -1;
    }

    private void loadPaymentData() {
        double totalRoom = calculateTotalRoomPayment();
        double totalService = calculateTotalServicePayment();
        double totalPayment = totalRoom + totalService;

        updateLabel(lblTotalRoomPayment, "Tổng Tiền Phòng: %.0f (VNĐ)", totalRoom);
        updateLabel(lblTotalServicePayment, "Tổng Tiền Dịch Vụ: %.0f (VNĐ)", totalService);
        updateLabel(lblTotalPayment, "Tổng Tiền Phải Thanh Toán: %.0f (VNĐ)", totalPayment);
    }
    
    private double calculateTotalRoomPayment() {
        try {
            double giaPhong = Double.parseDouble(getRoomPrice());
            int tongNgay = calculateTotalDays();
            return giaPhong * tongNgay;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private double calculateTotalServicePayment() {
        double total = 0;
        if (addedServiceModel != null) {
            for (int i = 0; i < addedServiceModel.getRowCount(); i++) {
                total += parseDouble(addedServiceModel.getValueAt(i, 4));
            }
        }
        return total;
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

        Object[][] tableData = {{
            roomView.getRoomNumber(), 
            formatDate(res.getBookingDate()),
            getCheckinDate(),
            formatDate(res.getCheckoutDate()),
            ngayTra,
            tongNgay + " (Ngày)",
            gia + " (VNĐ)"
        }};

        lblKhachHang.setText("Khách Hàng: " + khachHang);
        lblNgayTraPhong.setText("Ngày Trả Phòng: " + ngayTra);
        lblGiaPhongTheoNgay.setText("Giá Phòng Theo Ngày: " + gia + " (VNĐ)");
        lblTotal.setText("Tổng Tiền Phòng: " + gia + " (VNĐ)");
        
        tableChiTiet.setModel(new DefaultTableModel(tableData, ROOM_DETAIL_COLUMNS));
    }
    
    private String getCustomerName(int customerId) {
        Customer cus = customerDAO.getById(customerId);
        return cus != null ? cus.getFullName() : "N/A";
    }
    
    private String getRoomPrice() {
        Room room = roomDAO.getById(roomView.getRoomId());
        if (room != null) {
            RoomType rt = roomTypeDAO.getById(room.getTypeId());
            if (rt != null) return String.valueOf(rt.getPrice());
        }
        return "N/A";
    }
    
    private String getCheckinDate() {
        if (currentCheckinId <= 0) return "N/A";
        
        Checkin checkin = checkinDAO.getById(currentCheckinId);
        return (checkin != null && checkin.getCheckinTime() != null) 
            ? checkin.getCheckinTime().format(DATE_FORMATTER) 
            : "N/A";
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
        if (addedServiceModel == null) {
            updateServiceTotal();
            return;
        }
        
        addedServiceModel.setRowCount(0);
        
        if (currentCheckinId <= 0) {
            updateServiceTotal();
            return;
        }

        for (ServiceUsage su : serviceUsageDAO.getByCheckinId(currentCheckinId)) {
            Service s = serviceDAO.getById(su.getServiceId());
            if (s == null) continue;
            
            double price = s.getPrice();
            int qty = su.getQuantity();
            double total = price * qty;
            
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
        double total = calculateTotalServicePayment();
        lblTotalService.setText(String.format("Tổng Tiền Dịch Vụ: %.0f (VNĐ)", total));
        loadPaymentData();
    }
    
    private void addSelectedService() {
        if (tableDichVuSanCo.getSelectedRow() < 0) return;
        
        if (currentCheckinId <= 0) {
            showMessage("Không tìm thấy check-in cho phòng này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tableDichVuSanCo.convertRowIndexToModel(tableDichVuSanCo.getSelectedRow());
        
        try {
            int serviceId = parseInt(availableServiceModel.getValueAt(row, 2));
            int quantity = getQuantityFromUser();
            
            if (quantity <= 0) return;
            
            ServiceUsage su = new ServiceUsage(0, currentCheckinId, serviceId, quantity, LocalDateTime.now());
            if (serviceUsageDAO.insert(su)) {
                showMessage("Thêm dịch vụ thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        if (tableDichVuDaChon.getSelectedRow() < 0) return;

        int row = tableDichVuDaChon.convertRowIndexToModel(tableDichVuDaChon.getSelectedRow());
        
        try {
            int usageId = parseInt(addedServiceModel.getValueAt(row, 5));
            
            if (!confirmAction("Bạn có chắc chắn muốn xóa dịch vụ này?", "Xác nhận xóa")) return;

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

    private void handlePayment() {
        if (currentCheckinId <= 0) {
            showMessage("Không tìm thấy check-in cho phòng này.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!confirmAction("Khách hàng đã thanh toán?", "Xác nhận thanh toán")) return;

        try {
            Checkin checkin = checkinDAO.getById(currentCheckinId);
            Reservation res = reservationDAO.getById(roomView.getReservationId());
            
            if (checkin == null || res == null) {
                showMessage("Không tìm thấy thông tin cần thiết.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double totalRoom = calculateTotalRoomPayment();
            double totalService = calculateTotalServicePayment();
            double totalPayment = totalRoom + totalService;

            // Update checkoutTime vào checkin
            String ngayTraText = lblNgayTraPhong.getText().replace("Ngày Trả Phòng: ", "").trim();
            LocalDateTime checkoutTime = LocalDateTime.parse(ngayTraText, DATE_FORMATTER);
            checkinDAO.checkout(currentCheckinId, checkoutTime);

            // Tạo hóa đơn
            Invoice invoice = new Invoice(0, currentCheckinId, employeeId, totalRoom, totalService, totalPayment, LocalDateTime.now());
            if (invoiceDAO.insert(invoice)) {
                roomService.checkOutRoom(roomView.getRoomId());
                showMessage("Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.refreshRoomPanel();
                dispose();
            } else {
                showMessage("Lỗi khi lưu hóa đơn vào cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessage("Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private boolean confirmAction(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    private void updateLabel(JLabel label, String format, double value) {
        if (label != null) label.setText(String.format(format, value));
    }
    
    private int parseInt(Object value) {
        return Integer.parseInt(String.valueOf(value));
    }
    
    private double parseDouble(Object value) {
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}