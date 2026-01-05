package com.ql_khach_san.ui.NhanVien;

import com.ql_khach_san.dao.EmployeeDAO;
import com.ql_khach_san.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class NhanVienGUI extends JFrame {

    // ================= UI COMPONENTS =================
    private JTextField txtEmployeeID, txtUsername, txtPassword, txtFullName, txtTimKiem;
    private JComboBox<String> cboRole;
    private JButton btnThem, btnSua, btnXoa,btnReset;
    private JTable table;
    private DefaultTableModel tableModel;

    // ================= DAO =================
    private EmployeeDAO employeeDAO = new EmployeeDAO();

    // ================= CONSTRUCTOR =================
    public NhanVienGUI() {
        initComponents();
        loadData();

        setTitle("QUẢN LÝ NHÂN VIÊN");
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ================= INIT UI =================
    private void initComponents() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(0, 188, 212));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(600, 10, 300, 30);
        mainPanel.add(lblTitle);

        // ===== LABELS =====
        JLabel lblEmployeeID = new JLabel("Mã nhân viên");
        lblEmployeeID.setForeground(Color.WHITE);
        lblEmployeeID.setBounds(50, 60, 120, 25);
        mainPanel.add(lblEmployeeID);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setBounds(520, 60, 100, 25);
        mainPanel.add(lblUsername);

        JLabel lblFullName = new JLabel("Họ và tên");
        lblFullName.setForeground(Color.WHITE);
        lblFullName.setBounds(50, 110, 120, 25);
        mainPanel.add(lblFullName);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(520, 110, 100, 25);
        mainPanel.add(lblPassword);

        JLabel lblRole = new JLabel("Vai trò");
        lblRole.setForeground(Color.WHITE);
        lblRole.setBounds(50, 160, 120, 25);
        mainPanel.add(lblRole);

        JLabel lblTimKiem = new JLabel("Tìm kiếm theo tên");
        lblTimKiem.setForeground(Color.WHITE);
        lblTimKiem.setBounds(50, 240, 120, 25);
        mainPanel.add(lblTimKiem);

        // ===== FIELDS =====
        txtEmployeeID = new JTextField();
        txtEmployeeID.setBounds(180, 60, 300, 30);
        txtEmployeeID.setEditable(false);
        txtEmployeeID.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(txtEmployeeID);

        txtUsername = new JTextField();
    txtUsername.setBounds(620, 60, 300, 30);
        mainPanel.add(txtUsername);

        txtFullName = new JTextField();
        txtFullName.setBounds(180, 110, 300, 30);
        mainPanel.add(txtFullName);

        txtPassword = new JTextField();
        txtPassword.setBounds(620, 110, 300, 30);
        mainPanel.add(txtPassword);

        cboRole = new JComboBox<>(new String[]{
            "Nhân viên", "Quản lý"
        });
        cboRole.setBounds(180, 160, 300, 30);
        mainPanel.add(cboRole);

        txtTimKiem = new JTextField();
        txtTimKiem.setBounds(180, 240, 300, 30);
        mainPanel.add(txtTimKiem);

        // ===== BUTTONS =====
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

        btnReset = new JButton("Reset");
        btnReset.setBounds(940, 240, 100, 35);
        btnReset.setBackground(new Color(173, 216, 230));
        mainPanel.add(btnReset);

        // ===== TABLE =====
        String[] columnNames = {"ID", "Username", "Password", "Họ và tên", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Cấu hình độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Username
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Password
        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Họ và tên
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Role

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 300, 1290, 330);
        mainPanel.add(scrollPane);

        add(mainPanel);
        addEventHandlers();
    }

    // ================= LOAD DATA =================
    private void loadData() {
        tableModel.setRowCount(0);
        List<Employee> list = employeeDAO.getAll();
        System.out.println("Loaded " + list.size() + " employees");
        for (Employee e : list) {
            System.out.println("Employee: ID=" + e.getEmployeeId() + ", Name=" + e.getFullName() + ", Role=" + e.getRole());
            tableModel.addRow(new Object[]{
                e.getEmployeeId(),
                e.getUsername(),
                e.getPassword(),
                e.getFullName(),
                e.getRole()
            });
        }
    }

    // ================= EVENTS =================
    private void addEventHandlers() {

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r == -1) return;
                txtEmployeeID.setText(tableModel.getValueAt(r, 0).toString());
                txtUsername.setText(tableModel.getValueAt(r, 1).toString());
                txtPassword.setText(tableModel.getValueAt(r, 2).toString());
                txtFullName.setText(tableModel.getValueAt(r, 3).toString());
                cboRole.setSelectedItem(tableModel.getValueAt(r, 4));
            }
                   
        });
                btnReset.addActionListener(e -> {
                clearFields();
                loadData();
        });

        // THÊM
        btnThem.addActionListener(e -> {
            if (!validateInput(true)) return;

            Employee emp = new Employee(
                0,
                txtUsername.getText(),
                txtPassword.getText(),
                txtFullName.getText(),
                cboRole.getSelectedItem().toString()
            );

            if (employeeDAO.insert(emp)) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                loadData();
                clearFields();
            }
        });

        // SỬA
        btnSua.addActionListener(e -> {
            if (!validateInput(false)) return;

            boolean updatePassword = !txtPassword.getText().trim().isEmpty();

            Employee emp = new Employee(
                Integer.parseInt(txtEmployeeID.getText()),
                txtUsername.getText(),
                txtPassword.getText(),
                txtFullName.getText(),
                cboRole.getSelectedItem().toString()
            );

            if (employeeDAO.update(emp, updatePassword)) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
                loadData();
                clearFields();
            }
        });

        // XÓA
        btnXoa.addActionListener(e -> {
            if (txtEmployeeID.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhân viên này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (employeeDAO.delete(Integer.parseInt(txtEmployeeID.getText()))) {
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                    loadData();
                    clearFields();
                }
            }
        });

        // TÌM KIẾM
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                tableModel.setRowCount(0);
                for (Employee emp : employeeDAO.searchByName(txtTimKiem.getText())) {
                    tableModel.addRow(new Object[]{
                        emp.getEmployeeId(),
                        emp.getUsername(),
                        emp.getPassword(),
                        emp.getFullName(),
emp.getRole()
                    });
                }
            }
        });
    }

    // ================= VALIDATE =================
    private boolean validateInput(boolean isInsert) {
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập username!");
            return false;
        }
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            return false;
        }
        if (isInsert && txtPassword.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập password!");
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

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanVienGUI().setVisible(true));
    }
}