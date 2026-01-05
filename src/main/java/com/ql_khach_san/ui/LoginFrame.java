package com.ql_khach_san.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Tài khoản:"), c);
        c.gridx = 1; txtUser = new JTextField(16); form.add(txtUser, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Mật khẩu:"), c);
        c.gridx = 1; txtPass = new JPasswordField(16); form.add(txtPass, c);

        p.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnExit = new JButton("Thoát");
        buttons.add(btnLogin); buttons.add(btnExit);
        p.add(buttons, BorderLayout.SOUTH);

        getContentPane().add(p);

        btnLogin.addActionListener((ActionEvent e) -> doLogin());
        btnExit.addActionListener((ActionEvent e) -> System.exit(0));
    }

    private void doLogin() {
        String user = txtUser.getText() == null ? "" : txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản"); return; }
        // Simple demo: accept any non-empty user. Integrate real auth later.
        SwingUtilities.invokeLater(() -> {
            dispose();
            new MainMenu(user).setVisible(true);
        });
    }

    // Simple main menu after login for testing
    private static class MainMenu extends JFrame {
        public MainMenu(String user) {
            setTitle("Quản lý - " + user);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            init(user);
            pack();
            setLocationRelativeTo(null);
        }
        private void init(String user) {
            JPanel p = new JPanel(new BorderLayout(8,8));
            p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            JLabel lbl = new JLabel("Xin chào, " + user);
            lbl.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
            p.add(lbl, BorderLayout.NORTH);
            JPanel buttons = new JPanel(new GridLayout(3,1,8,8));
            JButton bRoom = new JButton("Quản Lý Phòng");
            JButton bType = new JButton("Quản Lý Loại Phòng");
            JButton bLogout = new JButton("Đăng xuất");
            buttons.add(bRoom); buttons.add(bType); buttons.add(bLogout);
            p.add(buttons, BorderLayout.CENTER);
            getContentPane().add(p);

            bRoom.addActionListener(e -> { try { new com.ql_khach_san.ui.Room.GUI().setVisible(true); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi mở Room: " + ex.getMessage()); } });
            bType.addActionListener(e -> { try { new com.ql_khach_san.ui.RoomType.GUI().setVisible(true); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi mở RoomType: " + ex.getMessage()); } });
            bLogout.addActionListener(e -> { dispose(); SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)); });
        }
    }

}
