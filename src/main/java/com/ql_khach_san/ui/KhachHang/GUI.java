/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ql_khach_san.ui.KhachHang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class GUI extends JFrame {
    
    // Khai báo các thành phần giao diện
    private JTextField txtCustomerID, txtFullName, txtPhone, txtCCCD, txtAddress, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnXuatExcel, btnNhapExcel;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Kết nối database
    private Connection connection;
    private final String DB_URL = "jdbc:mysql://localhost:3306/db_ql_khach_san";
    private final String DB_USER = "root"; // Thay bằng username của bạn
    private final String DB_PASSWORD = ""; // Thay bằng password của bạn
    
    public GUI() {
        connectDatabase();
        initComponents();
        loadData();
        setTitle("QUẢN LÝ KHÁCH HÀNG");
        setSize(1400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Kết nối database thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Panel chính với màu cyan
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255, 182, 193));
        mainPanel.setLayout(null);
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(600, 10, 300, 30);
        mainPanel.add(lblTitle);
        
        // Hàng 1: Customer ID, CCCD
        JLabel lblCustomerID = new JLabel("Mã khách hàng");
        lblCustomerID.setForeground(Color.WHITE);
        lblCustomerID.setBounds(50, 60, 120, 25);
        mainPanel.add(lblCustomerID);
        
        txtCustomerID = new JTextField();
        txtCustomerID.setBounds(180, 60, 300, 30);
        txtCustomerID.setEnabled(false); 
        mainPanel.add(txtCustomerID);
        
        JLabel lblCCCD = new JLabel("CCCD");
        lblCCCD.setForeground(Color.WHITE);
        lblCCCD.setBounds(520, 60, 100, 25);
        mainPanel.add(lblCCCD);
        
        txtCCCD = new JTextField();
        txtCCCD.setBounds(620, 60, 300, 30);
        mainPanel.add(txtCCCD);
        
        // Hàng 2: Họ tên, Số điện thoại
        JLabel lblFullName = new JLabel("Họ và tên");
        lblFullName.setForeground(Color.WHITE);
        lblFullName.setBounds(50, 110, 120, 25);
        mainPanel.add(lblFullName);
        
        txtFullName = new JTextField();
        txtFullName.setBounds(180, 110, 300, 30);
        mainPanel.add(txtFullName);
        
        JLabel lblPhone = new JLabel("Số điện thoại");
        lblPhone.setForeground(Color.WHITE);
        lblPhone.setBounds(520, 110, 100, 25);
        mainPanel.add(lblPhone);
        
        txtPhone = new JTextField();
        txtPhone.setBounds(620, 110, 300, 30);
        mainPanel.add(txtPhone);
        
        // Hàng 3: Địa chỉ
        JLabel lblAddress = new JLabel("Địa chỉ");
        lblAddress.setForeground(Color.WHITE);
        lblAddress.setBounds(50, 160, 120, 25);
        mainPanel.add(lblAddress);
        
        txtAddress = new JTextField();
        txtAddress.setBounds(180, 160, 740, 30);
        mainPanel.add(txtAddress);
        
        // Tìm kiếm
        JLabel lblTimKiem = new JLabel("Tìm kiếm theo tên");
        lblTimKiem.setForeground(Color.WHITE);
        lblTimKiem.setBounds(50, 240, 120, 25);
        mainPanel.add(lblTimKiem);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setBounds(180, 240, 300, 30);
        mainPanel.add(txtTimKiem);
        
        // Các nút chức năng
        btnThem = new JButton("Thêm");
        btnThem.setBounds(580, 240, 100, 35);
        btnThem.setBackground(new Color(135, 206, 250));
        mainPanel.add(btnThem);
        
        btnSua = new JButton("Sửa");
        btnSua.setBounds(700, 240, 100, 35);
        btnSua.setBackground(new Color(135, 206, 250));
        mainPanel.add(btnSua);
        
        btnXoa = new JButton("Xóa");
        btnXoa.setBounds(820, 240, 100, 35);
        btnXoa.setBackground(new Color(135, 206, 250));
        mainPanel.add(btnXoa);
        
        btnXuatExcel = new JButton("Xuất Excel");
        btnXuatExcel.setBounds(940, 240, 120, 35);
        btnXuatExcel.setBackground(new Color(135, 206, 250));
        mainPanel.add(btnXuatExcel);
        
        btnNhapExcel = new JButton("Nhập Excel");
        btnNhapExcel.setBounds(1080, 240, 120, 35);
        btnNhapExcel.setBackground(new Color(135, 206, 250));
        mainPanel.add(btnNhapExcel);
        
        // Bảng dữ liệu
        String[] columnNames = {"ID", "Họ và tên", "Số điện thoại", "CCCD", "Địa chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 300, 1290, 330);
        mainPanel.add(scrollPane);
        
        // Thêm sự kiện
        addEventHandlers();
        
        add(mainPanel);
    }
    
    private void loadData() {
        try {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            String sql = "SELECT * FROM customer ORDER BY customer_id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("cccd"),
                    rs.getString("address")
                });
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void addEventHandlers() {
        // Sự kiện click vào bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtCustomerID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtFullName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtCCCD.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtAddress.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });
        
        // Nút Thêm
        btnThem.addActionListener(e -> {
            if (validateInput()) {
                try {
                    String sql = "INSERT INTO customer (full_name, phone, cccd, address) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, txtFullName.getText());
                    pstmt.setString(2, txtPhone.getText());
                    pstmt.setString(3, txtCCCD.getText());
                    pstmt.setString(4, txtAddress.getText());
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                        loadData();
                        clearFields();
                    }
                    pstmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi thêm dữ liệu: " + ex.getMessage());
                }
            }
        });
        
        // Nút Sửa
        btnSua.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                if (validateInput()) {
                    try {
                        String sql = "UPDATE customer SET full_name=?, phone=?, cccd=?, address=? WHERE customer_id=?";
                        PreparedStatement pstmt = connection.prepareStatement(sql);
                        pstmt.setString(1, txtFullName.getText());
                        pstmt.setString(2, txtPhone.getText());
                        pstmt.setString(3, txtCCCD.getText());
                        pstmt.setString(4, txtAddress.getText());
                        pstmt.setInt(5, Integer.parseInt(txtCustomerID.getText()));
                        
                        int result = pstmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
                            loadData();
                            clearFields();
                        }
                        pstmt.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Lỗi cập nhật dữ liệu: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
            }
        });
        
        // Nút Xóa
        btnXoa.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc muốn xóa khách hàng này?", 
                    "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        String sql = "DELETE FROM customer WHERE customer_id=?";
                        PreparedStatement pstmt = connection.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(txtCustomerID.getText()));
                        
                        int result = pstmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
                            loadData();
                            clearFields();
                        }
                        pstmt.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            }
        });
        
        
        // Tìm kiếm
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = txtTimKiem.getText().toLowerCase();
                try {
                    tableModel.setRowCount(0);
                    String sql = "SELECT * FROM customer WHERE LOWER(full_name) LIKE ? ORDER BY customer_id";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, "%" + searchText + "%");
                    ResultSet rs = pstmt.executeQuery();
                    
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getInt("customer_id"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            rs.getString("cccd"),
                            rs.getString("address")
                        });
                    }
                    rs.close();
                    pstmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    private boolean validateInput() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            txtFullName.requestFocus();
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
            txtPhone.requestFocus();
            return false;
        }
        if (txtCCCD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD!");
            txtCCCD.requestFocus();
            return false;
        }
        return true;
    }
    
    private void clearFields() {
       txtCustomerID.setEnabled(true);  
        txtCustomerID.setText("");
        txtCustomerID.setEnabled(false);
        txtFullName.setText("");
        txtPhone.setText("");
        txtCCCD.setText("");
        txtAddress.setText("");
        txtTimKiem.setText("");
        table.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}

