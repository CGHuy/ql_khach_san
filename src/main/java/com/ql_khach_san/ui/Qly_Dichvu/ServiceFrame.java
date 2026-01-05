package com.ql_khach_san.ui.Qly_Dichvu;

import javax.swing.*;

public class ServiceFrame extends JFrame {

    public ServiceFrame() {
        super("Quản lý Dịch vụ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // so closing this window doesn't exit whole app
        setSize(900, 500);
        setLocationRelativeTo(null);
        setContentPane(new ServicePanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServiceFrame f = new ServiceFrame();
            f.setVisible(true);
        });
    }
}