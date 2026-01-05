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

public class DialogChiTiet extends JDialog {

    private RoomView roomView;
    private RoomDAO roomDAO = new RoomDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private CheckinDAO checkinDAO = new CheckinDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private ServiceUsageDAO serviceUsageDAO = new ServiceUsageDAO();
    
    private JLabel lblRoomNumber;
    private JPanel panelChiTietThuePhong;
    private JPanel panelChiTietDichVu;
    private JPanel panelThanhToan;
    
    private JLabel lblKhachHang, lblNgayTraPhong, lblGiaPhongTheoNgay, lblTotal;
    private JTable tableChiTiet;
    private JTable tableDichVuDaChon;
    private JTable tableDichVuSanCo;
    private DefaultTableModel availableServiceModel;
    private DefaultTableModel addedServiceModel;
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
        
        // Panel header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(128, 0, 128)); // Màu tím
        headerPanel.setPreferredSize(new Dimension(1080, 35));
        lblRoomNumber = new JLabel();
        lblRoomNumber.setFont(new Font("Arial", Font.BOLD, 20));
        lblRoomNumber.setForeground(Color.WHITE);
        headerPanel.add(lblRoomNumber);
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel main
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel trái
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(10, 10));
        
        // Panel 1: Chi Tiết Thuê Phòng
        panelChiTietThuePhong = createPanelChiTietThuePhong();
        leftPanel.add(panelChiTietThuePhong, BorderLayout.NORTH);
        
        // Panel 2: Chi Tiết Dịch Vụ
        panelChiTietDichVu = createPanelChiTietDichVu();
        leftPanel.add(panelChiTietDichVu, BorderLayout.CENTER);
        
        // Panel 3: Thanh Toán
        panelThanhToan = createPanelThanhToan();
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(panelThanhToan, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPanelChiTietThuePhong() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        // Header
        JLabel lblTitle = new JLabel("Chi Tiết Thuê Phòng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(128, 0, 128));
        lblTitle.setPreferredSize(new Dimension(0, 35));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 128), 2));
        
        // Thông tin khách hàng và giá phòng
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 3, 10, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblKhachHang = new JLabel("Khách Hàng: ");
        lblNgayTraPhong = new JLabel("Ngày Trả Phòng: ");
        lblGiaPhongTheoNgay = new JLabel("Giá Phòng Theo Ngày: ");
        lblGiaPhongTheoNgay.setForeground(Color.RED);
        
        infoPanel.add(lblKhachHang);
        infoPanel.add(lblNgayTraPhong);
        infoPanel.add(lblGiaPhongTheoNgay);
        
        contentPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Tiêu đề bảng
        JPanel tableTitlePanel = new JPanel();
        tableTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tableTitlePanel.setBackground(Color.WHITE);
        tableTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        JLabel lblHoaDon = new JLabel("Hóa Đơn Chi Tiết");
        lblHoaDon.setFont(new Font("Arial", Font.BOLD, 12));
        tableTitlePanel.add(lblHoaDon);
        
        // Bảng chi tiết
        String[] columns = {"Phòng", "Ngày Đặt", "Ngày Nhận Phòng", "Ngày Trả Phòng Dự Kiến", 
                           "Ngày Trả Phòng", "Tổng Thời Gian", "Tiền Phòng"};
        tableChiTiet = new JTable(new javax.swing.table.DefaultTableModel(columns, 0));
        tableChiTiet.setRowHeight(25);
        tableChiTiet.getTableHeader().setBackground(new Color(128, 0, 128));
        tableChiTiet.getTableHeader().setForeground(Color.WHITE);
        tableChiTiet.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tableChiTiet.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        tablePanel.add(tableTitlePanel, BorderLayout.NORTH);
        tablePanel.add(tableChiTiet.getTableHeader(), BorderLayout.CENTER);
        tablePanel.add(tableChiTiet, BorderLayout.SOUTH);
        
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Tổng tiền phòng
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        lblTotal = new JLabel("Tổng Tiền Phòng: 200 (VNĐ)");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(Color.RED);
        totalPanel.add(lblTotal);
        
        contentPanel.add(totalPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void addSelectedService() {
        int selectedRow = tableDichVuSanCo.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        if (currentCheckinId <= 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy check-in cho phòng này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tableDichVuSanCo.convertRowIndexToModel(selectedRow);
        DefaultTableModel availableModel = (DefaultTableModel) tableDichVuSanCo.getModel();

        String tenDv = String.valueOf(availableModel.getValueAt(modelRow, 0));
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Object giaObj = availableModel.getValueAt(modelRow, 1);
        Object idObj = availableModel.getValueAt(modelRow, 2);

        int serviceId;
        double gia;
        try {
            serviceId = Integer.parseInt(String.valueOf(idObj));
            gia = Double.parseDouble(String.valueOf(giaObj));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu dịch vụ không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Nhập số lượng", "1");
        if (input == null) {
            return; // user cancelled
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(input.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (soLuong <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ServiceUsage su = new ServiceUsage(0, currentCheckinId, serviceId, soLuong, LocalDateTime.now());
        boolean ok = serviceUsageDAO.insert(su);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadServiceUsages();
    }
    
    private JPanel createPanelChiTietDichVu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        // Header
        JLabel lblTitle = new JLabel("Chi Tiết Dịch Vụ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(128, 0, 128));
        lblTitle.setPreferredSize(new Dimension(0, 35));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 128), 2));

        // Panel trái: dịch vụ đã thêm
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        // Tiêu đề bảng
        JPanel tableTitlePanelLeft = new JPanel();
        tableTitlePanelLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        tableTitlePanelLeft.setBackground(Color.WHITE);
        tableTitlePanelLeft.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        JLabel lblAddedTitle = new JLabel("Dịch Vụ Đã Thêm");
        lblAddedTitle.setFont(new Font("Arial", Font.BOLD, 12));
        tableTitlePanelLeft.add(lblAddedTitle);
        leftPanel.add(tableTitlePanelLeft, BorderLayout.NORTH);
        
        // Bảng dịch vụ đã thêm
        addedServiceModel = new DefaultTableModel(new String[]{"Dịch Vụ", "Thời gian", "Số Lượng", "Đơn Giá", "Thành Tiền", "ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDichVuDaChon = new JTable(addedServiceModel);
        tableDichVuDaChon.setRowHeight(24);
        tableDichVuDaChon.getTableHeader().setBackground(new Color(128, 0, 128));
        tableDichVuDaChon.getTableHeader().setForeground(Color.WHITE);
        tableDichVuDaChon.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tableDichVuDaChon.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Popup menu để xóa dịch vụ
        JPopupMenu popupDeleteMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Xóa");
        deleteItem.addActionListener(e -> deleteSelectedServiceUsage());
        popupDeleteMenu.add(deleteItem);
        tableDichVuDaChon.addMouseListener(new MouseAdapter() {
            private void showPopup(MouseEvent e) {
                int row = tableDichVuDaChon.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    tableDichVuDaChon.setRowSelectionInterval(row, row);
                    popupDeleteMenu.show(tableDichVuDaChon, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });

        tableDichVuDaChon.removeColumn(tableDichVuDaChon.getColumnModel().getColumn(5)); // Ẩn cột ID
        JPanel addedTablePanel = new JPanel(new BorderLayout());
        addedTablePanel.setBackground(Color.WHITE);
        addedTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JScrollPane addedScrollPane = new JScrollPane(tableDichVuDaChon);
        addedScrollPane.setBorder(BorderFactory.createEmptyBorder());
        addedTablePanel.add(addedScrollPane, BorderLayout.CENTER);

        leftPanel.add(addedTablePanel, BorderLayout.CENTER);

        // Panel phải: danh sách dịch vụ để chọn
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        // Tiêu đề bảng
        JPanel tableTitlePanelRight = new JPanel();
        tableTitlePanelRight.setLayout(new FlowLayout(FlowLayout.LEFT));
        tableTitlePanelRight.setBackground(Color.WHITE);
        tableTitlePanelRight.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        JLabel lblAvailableTitle = new JLabel("Dịch Vụ Sẵn Có");
        tableTitlePanelRight.add(lblAvailableTitle);
        rightPanel.add(tableTitlePanelRight, BorderLayout.NORTH);

        // Bảng dịch vụ sẵn có
        availableServiceModel = new DefaultTableModel(new String[]{"Tên Dịch Vụ", "Giá", "ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDichVuSanCo = new JTable(availableServiceModel);
        tableDichVuSanCo.setRowHeight(24);
        tableDichVuSanCo.getTableHeader().setBackground(new Color(128, 0, 128));
        tableDichVuSanCo.getTableHeader().setForeground(Color.WHITE);
        tableDichVuSanCo.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tableDichVuSanCo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tableDichVuSanCo.removeColumn(tableDichVuSanCo.getColumnModel().getColumn(2)); // Ẩn cột ID

        // Popup thêm dịch vụ
        JPopupMenu popupAddMenu = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("Thêm");
        addItem.addActionListener(e -> addSelectedService());
        popupAddMenu.add(addItem);
        tableDichVuSanCo.addMouseListener(new MouseAdapter() {
            private void showPopup(MouseEvent e) {
                int row = tableDichVuSanCo.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    tableDichVuSanCo.setRowSelectionInterval(row, row);
                    popupAddMenu.show(tableDichVuSanCo, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });

        JPanel availableTablePanel = new JPanel(new BorderLayout());
        availableTablePanel.setBackground(Color.WHITE);
        availableTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JScrollPane availableScrollPane = new JScrollPane(tableDichVuSanCo);
        availableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        availableTablePanel.add(availableScrollPane, BorderLayout.CENTER);

        rightPanel.add(availableTablePanel, BorderLayout.CENTER);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelThanhToan() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        // Header
        JLabel lblTitle = new JLabel("Thanh Toán", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(128, 0, 128));
        lblTitle.setPreferredSize(new Dimension(0, 35));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 128), 2));
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadData() {
        currentCheckinId = -1;
        if (roomView != null) {
            lblRoomNumber.setText("Phòng " + roomView.getRoomNumber());
            
            // Load dữ liệu từ DAO
            int resId = roomView.getReservationId();
            if (resId > 0) {
                Reservation res = reservationDAO.getById(resId);
                if (res != null) {
                    Customer cus = customerDAO.getById(res.getCustomerId());
                    String khachHang = cus != null ? cus.getFullName() : "N/A";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    
                    LocalDateTime ngayTraTime = LocalDateTime.now(); // Thời gian hiện tại
                    String ngayTra = ngayTraTime.format(formatter);

                    // Lấy giá phòng
                    String gia = "N/A";
                    Room room = roomDAO.getById(roomView.getRoomId());
                    if (room != null) {
                        RoomType rt = roomTypeDAO.getById(room.getTypeId());
                        if (rt != null) {
                            gia = String.valueOf(rt.getPrice());
                        }
                    }
                    
                    String ngayNhan = "N/A";
                    LocalDateTime ngayNhanTime = null;
                    int tongNgay = 0;
                    
                    Checkin checkin = checkinDAO.getByReservationId(resId);
                    if (checkin != null && checkin.getCheckinTime() != null) {
                        currentCheckinId = checkin.getCheckinId();
                        ngayNhanTime = checkin.getCheckinTime();
                        ngayNhan = ngayNhanTime.format(formatter);
                        
                        // Tính tổng số ngày
                        tongNgay = (int) java.time.temporal.ChronoUnit.DAYS.between(ngayNhanTime, ngayTraTime);
                        if (tongNgay == 0) {
                            tongNgay = 1; // Tối thiểu 1 ngày
                        }
                    }
                    
                    // Dữ liệu bảng
                    Object[][] tableData = {
                        {
                            roomView.getRoomNumber(), 
                            res.getBookingDate() != null ? res.getBookingDate().format(formatter) : "N/A",
                            ngayNhan,
                            res.getCheckoutDate() != null ? res.getCheckoutDate().format(formatter) : "N/A",
                            ngayTra,
                            tongNgay + " (Ngày)",
                            gia + "(VNĐ)"
                        }
                    };
                    String total = gia + " (VNĐ)";
                    
                    setChiTietThuePhong(khachHang, ngayTra, gia, tableData, total);
                }
            }
            
            loadAvailableServices();
            loadServiceUsages();
            // TODO: Load thanh toán
        }
    }

    private void loadAvailableServices() {
        if (availableServiceModel == null) {
            return;
        }
        availableServiceModel.setRowCount(0);
        for (Service s : serviceDAO.getAll()) {
            availableServiceModel.addRow(new Object[]{s.getServiceName(), String.format("%.0f", s.getPrice()), s.getServiceId()});
        }
    }

    private void loadServiceUsages() {
        if (addedServiceModel == null) {
            return;
        }
        addedServiceModel.setRowCount(0);
        if (currentCheckinId <= 0) {
            return;
        }

        for (ServiceUsage su : serviceUsageDAO.getByCheckinId(currentCheckinId)) {
            Service s = serviceDAO.getById(su.getServiceId());
            if (s == null) {
                continue;
            }
            double price = s.getPrice();
            int qty = su.getQuantity();
            double total = price * qty;
            addedServiceModel.addRow(new Object[]{s.getServiceName(), su.getCreatedAt() != null ? su.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A", qty, String.format("%.0f", price), String.format("%.0f", total), su.getUsageId()});
        }
    }
    
    private void deleteSelectedServiceUsage() {
        int selectedRow = tableDichVuDaChon.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int row = tableDichVuDaChon.convertRowIndexToModel(selectedRow);
        Object usageIdObj = addedServiceModel.getValueAt(row, 5); // ID is at column 5 (hidden)

        int usageId;
        try {
            usageId = Integer.parseInt(String.valueOf(usageIdObj));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ID sử dụng dịch vụ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa dịch vụ này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = serviceUsageDAO.delete(usageId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa dịch vụ thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Xóa dịch vụ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadServiceUsages();
    }
    
    public void setChiTietThuePhong(String khachHang, String ngayTra, String gia, Object[][] tableData, String total) {
        lblKhachHang.setText("Khách Hàng: " + khachHang);
        lblNgayTraPhong.setText("Ngày Trả Phòng: " + ngayTra);
        lblGiaPhongTheoNgay.setText("Giá Phòng Theo Ngày: " + gia + " (VNĐ)");
        lblTotal.setText("Tổng Tiền Phòng: " + total);
        
        // Cập nhật table
        if (tableData != null && tableData.length > 0) {
            tableChiTiet.setModel(new javax.swing.table.DefaultTableModel(tableData, new String[]{"Phòng", "Ngày Đặt", "Ngày Nhận Phòng", "Ngày Trả Phòng Dự Kiến", "Ngày Trả Phòng", "Tổng Thời Gian", "Tiền Phòng"}));
        }
    }
    
    public void setChiTietDichVu(Object[][] serviceData) {
        // Set data cho bảng dịch vụ
    }
    
    public void setThanhToan(String tongTien, String daThanhToan, String conLai) {
        // Set data cho panel thanh toán
    }
    
}