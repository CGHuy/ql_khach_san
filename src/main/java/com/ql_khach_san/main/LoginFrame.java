package com.ql_khach_san.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.ql_khach_san.dao.EmployeeDAO;
import com.ql_khach_san.model.Employee;
import com.ql_khach_san.ui.TrangChu.MainFrame;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(new Color(230, 240, 250));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 2, true),
            BorderFactory.createEmptyBorder(24, 36, 24, 36)));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(40, 70, 130));
        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(20));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userPanel.setOpaque(false);
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userPanel.add(lblUsername);
        panel.add(userPanel);
        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(txtUsername);
        panel.add(Box.createVerticalStrut(12));

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passPanel.setOpaque(false);
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passPanel.add(lblPassword);
        panel.add(passPanel);
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(12));

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.setOpaque(false);
        JLabel lblRole = new JLabel("Quyền đăng nhập:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        rolePanel.add(lblRole);
        panel.add(rolePanel);
        cmbRole = new JComboBox<>(new String[]{"Nhân viên", "Quản lý"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(cmbRole);
        panel.add(Box.createVerticalStrut(20));

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(40, 70, 130));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        bg.add(panel);
        setContentPane(bg);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = dao.findByUsername(username);
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!emp.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "Sai mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Quyền trong DB có thể là "Nhân viên" hoặc "Quản lý" (so sánh không phân biệt hoa thường)
        if (!emp.getRole().equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền đăng nhập với vai trò này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Xin chào " + emp.getFullName() + " (" + emp.getRole() + ")");
        // Chuyển sang giao diện chính, truyền role và employeeId
        java.awt.EventQueue.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(emp.getRole(), emp.getEmployeeId());
            mainFrame.setVisible(true);
        });
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
