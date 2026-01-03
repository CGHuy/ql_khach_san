
package com.ql_khach_san.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(40, 70, 130));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(20));

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(lblUsername);
        panel.add(txtUsername);
        panel.add(Box.createVerticalStrut(12));

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(12));

        JLabel lblRole = new JLabel("Quyền đăng nhập:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbRole = new JComboBox<>(new String[]{"Nhân viên", "Quản lý"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(lblRole);
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
        // TODO: Thực hiện kiểm tra đăng nhập ở đây
        JOptionPane.showMessageDialog(this, "Đăng nhập với: " + username + ", quyền: " + role);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
