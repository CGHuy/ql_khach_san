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
        setPreferredSize(new Dimension(1200, 700));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ================= INIT UI =================
    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Color.WHITE);

        // Form panel using GridBag
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Row 0: ID and Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblEmployeeID = new JLabel("Mã nhân viên:"); lblEmployeeID.setFont(labelFont);
        formPanel.add(lblEmployeeID, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEmployeeID = new JTextField(); txtEmployeeID.setEditable(false); txtEmployeeID.setFont(fieldFont);
        txtEmployeeID.setBackground(new Color(245,245,245)); txtEmployeeID.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtEmployeeID, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblUsername = new JLabel("Username:"); lblUsername.setFont(labelFont);
        formPanel.add(lblUsername, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsername = new JTextField(); txtUsername.setFont(fieldFont);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtUsername, gbc);

        // Row 1: Fullname and Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblFullName = new JLabel("Họ và tên:"); lblFullName.setFont(labelFont);
        formPanel.add(lblFullName, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFullName = new JTextField(); txtFullName.setFont(fieldFont);
        txtFullName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtFullName, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblPassword = new JLabel("Password:"); lblPassword.setFont(labelFont);
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtPassword = new JTextField(); txtPassword.setFont(fieldFont);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtPassword, gbc);

        // Row 2: Role and Search
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblRole = new JLabel("Vai trò:"); lblRole.setFont(labelFont);
        formPanel.add(lblRole, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        cboRole = new JComboBox<>(new String[]{"Nhân viên","Quản lý"}); cboRole.setFont(fieldFont);
        cboRole.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(cboRole, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); btnPanel.setBackground(Color.WHITE);
        btnThem = new JButton("Thêm"); btnSua = new JButton("Sửa"); btnXoa = new JButton("Xóa"); btnReset = new JButton("Reset");
        Dimension btnSize = new Dimension(110, 34);
        for (JButton b : new JButton[]{btnThem, btnSua, btnXoa, btnReset}) { b.setPreferredSize(btnSize); b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); }
        btnPanel.add(btnThem); btnPanel.add(btnSua); btnPanel.add(btnXoa); btnPanel.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(btnPanel, gbc);
        gbc.gridwidth = 1;

        // Search panel above table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));
        JLabel lblTimKiem = new JLabel("Tìm kiếm:"); lblTimKiem.setFont(labelFont);
        txtTimKiem = new JTextField(24); txtTimKiem.setFont(fieldFont);
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        searchPanel.add(lblTimKiem); searchPanel.add(txtTimKiem);

        // Table
        String[] columnNames = {"ID", "Username", "Password", "Họ và tên", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(new Color(204,229,255));
        table.getTableHeader().setBackground(new Color(245,245,245));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 260));

        JPanel middlePanel = new JPanel(new BorderLayout()); middlePanel.setBackground(Color.WHITE);
        middlePanel.add(searchPanel, BorderLayout.NORTH);
        middlePanel.add(scrollPane, BorderLayout.CENTER);

        content.add(formPanel, BorderLayout.NORTH);
        content.add(middlePanel, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);

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