package com.ql_khach_san.ui.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class NhanVienGUI extends JFrame {
    
    // Khai báo các thành phần giao diện
   private JTextField txtEmployeeID, txtUsername, txtPassword, txtFullName, txtTimKiem;
    private JComboBox<String> cboRole;
    private JButton btnThem, btnSua, btnXoa, btnXuatExcel, btnNhapExcel;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Kết nối database
    private Connection connection;
    private final String DB_URL = "jdbc:mysql://localhost:3306/db_ql_khach_san";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";
    
    public NhanVienGUI() {
        connectDatabase();
        initComponents();
        loadData();
        setTitle("QUẢN LÝ NHÂN VIÊN");
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
        mainPanel.setBackground(new Color(0, 188, 212));
        mainPanel.setLayout(null);
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(600, 10, 300, 30);
        mainPanel.add(lblTitle);
        
        // Hàng 1: Mã nhân viên, Username
        JLabel lblEmployeeID = new JLabel("Mã nhân viên");
        lblEmployeeID.setForeground(Color.WHITE);
        lblEmployeeID.setBounds(50, 60, 120, 25);
        mainPanel.add(lblEmployeeID);
        
        txtEmployeeID = new JTextField();
        txtEmployeeID.setBounds(180, 60, 300, 30);
        txtEmployeeID.setEditable(false);
        txtEmployeeID.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(txtEmployeeID);
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setBounds(520, 60, 100, 25);
        mainPanel.add(lblUsername);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(620, 60, 300, 30);
        mainPanel.add(txtUsername);
        
        // Hàng 2: Họ tên, Password
        JLabel lblFullName = new JLabel("Họ và tên");
        lblFullName.setForeground(Color.WHITE);
        lblFullName.setBounds(50, 110, 120, 25);
        mainPanel.add(lblFullName);
        
        txtFullName = new JTextField();
        txtFullName.setBounds(180, 110, 300, 30);
        mainPanel.add(txtFullName);
        
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(520, 110, 100, 25);
        mainPanel.add(lblPassword);
        
        txtPassword = new JTextField();
        txtPassword.setBounds(620, 110, 300, 30);
        mainPanel.add(txtPassword);
        
        // Hàng 3: Vai trò (Role)
        JLabel lblRole = new JLabel("Vai trò");
        lblRole.setForeground(Color.WHITE);
        lblRole.setBounds(50, 160, 120, 25);
        mainPanel.add(lblRole);
        
        cboRole = new JComboBox<>(new String[]{"Nhân viên", "Quản lý", "Lễ tân", "Kế toán"});
        cboRole.setBounds(180, 160, 300, 30);
        mainPanel.add(cboRole);
        
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
        btnThem.setBackground(new Color(173, 216, 230));
        mainPanel.add(btnThem);
        
        btnSua = new JButton("Sửa");
        btnSua.setBounds(700, 240, 100, 35);
        btnSua.setBackground(new Color(173, 216, 230));
        mainPanel.add(btnSua);
        
        btnXoa = new JButton("Xóa");
        btnXoa.setBounds(820, 240, 100, 35);
        btnXoa.setBackground(new Color(173, 216, 230));
        mainPanel.add(btnXoa);
        
        btnXuatExcel = new JButton("Xuất Excel");
        btnXuatExcel.setBounds(940, 240, 120, 35);
        btnXuatExcel.setBackground(new Color(173, 216, 230));
        mainPanel.add(btnXuatExcel);
        
        btnNhapExcel = new JButton("Nhập Excel");
        btnNhapExcel.setBounds(1080, 240, 120, 35);
        btnNhapExcel.setBackground(new Color(173, 216, 230));
        mainPanel.add(btnNhapExcel);
        
        // Bảng dữ liệu
        String[] columnNames = {"ID", "Username","Password", "Họ và tên", "Role"};
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
            tableModel.setRowCount(0);
            String sql = "SELECT employee_id, username, password, full_name, role FROM employee ORDER BY employee_id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                   rs.getInt("employee_id"),
                   rs.getString("username"),    
                   rs.getString("password"),   
                   rs.getString("full_name"),  
                   rs.getString("role")         
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
        // Click vào bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
               if (selectedRow != -1) {
    txtEmployeeID.setText(tableModel.getValueAt(selectedRow, 0).toString()); // Cột 0: ID
    txtUsername.setText(tableModel.getValueAt(selectedRow, 1).toString());   // Cột 1: Username
    txtPassword.setText(tableModel.getValueAt(selectedRow, 2).toString());   // Cột 2: Password
    txtFullName.setText(tableModel.getValueAt(selectedRow, 3).toString());   // Cột 3: Họ và tên
    cboRole.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString()); // Cột 4: Role
            }
            }
        });
        
        // Nút Thêm
        btnThem.addActionListener(e -> {
            if (validateInput()) {
                try {
                    String sql = "INSERT INTO employee (username, password, full_name, role) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, txtUsername.getText());
                    pstmt.setString(2, txtPassword.getText()); // Nên mã hóa password trong thực tế
                    pstmt.setString(3, txtFullName.getText());
                    pstmt.setString(4, cboRole.getSelectedItem().toString());
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
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
                        String sql;
                        PreparedStatement pstmt;
                        
                        // Nếu có nhập password mới thì cập nhật, không thì không cập nhật password
                        if (txtPassword.getText().isEmpty()) {
                            sql = "UPDATE employee SET username=?, full_name=?, role=? WHERE employee_id=?";
                            pstmt = connection.prepareStatement(sql);
                            pstmt.setString(1, txtUsername.getText());
                            pstmt.setString(2, txtFullName.getText());
                            pstmt.setString(3, cboRole.getSelectedItem().toString());
                            pstmt.setInt(4, Integer.parseInt(txtEmployeeID.getText()));
                        } else {
                            sql = "UPDATE employee SET username=?, password=?, full_name=?, role=? WHERE employee_id=?";
                            pstmt = connection.prepareStatement(sql);
                            pstmt.setString(1, txtUsername.getText());
                            pstmt.setString(2, txtPassword.getText());
                            pstmt.setString(3, txtFullName.getText());
                            pstmt.setString(4, cboRole.getSelectedItem().toString());
                            pstmt.setInt(5, Integer.parseInt(txtEmployeeID.getText()));
                        }
                        
                        int result = pstmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!");
            }
        });
        
        // Nút Xóa
        btnXoa.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc muốn xóa nhân viên này?", 
                    "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        String sql = "DELETE FROM employee WHERE employee_id=?";
                        PreparedStatement pstmt = connection.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(txtEmployeeID.getText()));
                        
                        int result = pstmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
            }
        });
        
       
        
        // Tìm kiếm theo tên
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = txtTimKiem.getText().toLowerCase();
                try {
                    tableModel.setRowCount(0);
                    String sql = "SELECT employee_id, username, password, full_name, role FROM employee WHERE LOWER(full_name) LIKE ? ORDER BY employee_id";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, "%" + searchText + "%");
                    ResultSet rs = pstmt.executeQuery();
                    
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getInt("employee_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getString("password")
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
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập username!");
            txtUsername.requestFocus();
            return false;
        }
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            txtFullName.requestFocus();
            return false;
        }
        // Chỉ validate password khi thêm mới (txtEmployeeID rỗng)
        if (txtEmployeeID.getText().trim().isEmpty() && txtPassword.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập password!");
            txtPassword.requestFocus();
            return false;
        }
        return true;
    }
    
    private void clearFields() {
        txtEmployeeID.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        cboRole.setSelectedIndex(0);
        txtTimKiem.setText("");
        table.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NhanVienGUI().setVisible(true);
        });
    }
}