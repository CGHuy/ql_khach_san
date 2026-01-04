package com.ql_khach_san.ui.TrangChu;

import com.ql_khach_san.dao.*;
import com.ql_khach_san.model.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DialogChiTiet extends JDialog {

    private RoomView roomView;
    private RoomDAO roomDAO = new RoomDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private CheckinDAO checkinDAO = new CheckinDAO();
    
    private JLabel lblRoomNumber;
    private JPanel panelChiTietThuePhong;
    private JPanel panelChiTietDichVu;
    private JPanel panelThanhToan;
    
    private JLabel lblKhachHang, lblNgayTraPhong, lblGiaPhongTheoNgay, lblTotal;
    private JTable tableChiTiet;

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
        
        lblKhachHang = new JLabel("Khách Hàng: Quang Hưng");
        lblNgayTraPhong = new JLabel("Ngày Trả Phòng: 10-12-2021 23:23:01 PM");
        lblGiaPhongTheoNgay = new JLabel("Giá Phòng Theo Ngày: 200 (VNĐ)");
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
        Object[][] data = {
            {"115", "10-12-2021 23:22:10", "10-12-2021 23:22:10", "12-12-2021 23:22:10", 
             "10-12-2021 23:23:01", "1 (Ngày)", "200(VNĐ)"}
        };
        
        tableChiTiet = new JTable(data, columns);
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
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 128), 2));
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
            
            // TODO: Load dịch vụ và thanh toán
        }
    }
    
    // Methods để truyền dữ liệu vào các label và components
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