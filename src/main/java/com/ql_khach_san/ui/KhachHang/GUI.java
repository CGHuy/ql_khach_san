package com.ql_khach_san.ui.KhachHang;

import com.ql_khach_san.dao.CustomerDAO;
import com.ql_khach_san.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

    private JTextField txtCustomerID, txtFullName, txtPhone, txtCCCD, txtAddress, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa,btnReset;
    private JTable table;
    private DefaultTableModel tableModel;

    private CustomerDAO customerDAO = new CustomerDAO();

    public GUI() {
        initComponents();
        loadData();
        setTitle("QUẢN LÝ KHÁCH HÀNG");
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(255, 182, 193));

        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 10, 1400, 30);
        mainPanel.add(lblTitle);

        JLabel lblCustomerID = new JLabel("Mã khách hàng");
        JLabel lblFullName = new JLabel("Họ và tên");
        JLabel lblPhone = new JLabel("Số điện thoại");
        JLabel lblCCCD = new JLabel("CCCD");
        JLabel lblAddress = new JLabel("Địa chỉ");
        JLabel lblTimKiem = new JLabel("Tìm kiếm theo tên");

        JLabel[] labels = {lblCustomerID, lblFullName, lblAddress, lblTimKiem};
        int y = 60;
        for (JLabel lb : labels) {
            lb.setForeground(Color.WHITE);
            lb.setBounds(50, y, 150, 25);
            mainPanel.add(lb);
            y += 50;
        }

        lblCCCD.setForeground(Color.WHITE);
        lblCCCD.setBounds(520, 60, 100, 25);
        mainPanel.add(lblCCCD);

        lblPhone.setForeground(Color.WHITE);
        lblPhone.setBounds(520, 110, 120, 25);
        mainPanel.add(lblPhone);

        txtCustomerID = new JTextField();
        txtCustomerID.setBounds(180, 60, 300, 30);
        txtCustomerID.setEditable(false);
        mainPanel.add(txtCustomerID);

        txtCCCD = new JTextField();
        txtCCCD.setBounds(620, 60, 300, 30);
        mainPanel.add(txtCCCD);

        txtFullName = new JTextField();
        txtFullName.setBounds(180, 110, 300, 30);
        mainPanel.add(txtFullName);

        txtPhone = new JTextField();
        txtPhone.setBounds(620, 110, 300, 30);
        mainPanel.add(txtPhone);

        txtAddress = new JTextField();
        txtAddress.setBounds(180, 160, 740, 30);
        mainPanel.add(txtAddress);

        txtTimKiem = new JTextField();
        txtTimKiem.setBounds(180, 240, 300, 30);
        mainPanel.add(txtTimKiem);

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnThem.setBounds(580, 240, 100, 35);
        btnSua.setBounds(700, 240, 100, 35);
        btnXoa.setBounds(820, 240, 100, 35);

        mainPanel.add(btnThem);
        mainPanel.add(btnSua);
        mainPanel.add(btnXoa);
        
        btnReset = new JButton("Reset");
        btnReset.setBounds(940, 240, 100, 35);
        mainPanel.add(btnReset);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Họ và tên", "SĐT", "CCCD", "Địa chỉ"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(50, 300, 1290, 330);
        mainPanel.add(sp);

        add(mainPanel);
        addEvents();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Customer c : customerDAO.getAll()) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getFullName(),
                c.getPhone(),
                c.getCccd(),
                c.getAddress()
            });
        }
    }

    private void addEvents() {

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtCustomerID.setText(tableModel.getValueAt(r, 0).toString());
                txtFullName.setText(tableModel.getValueAt(r, 1).toString());
                txtPhone.setText(tableModel.getValueAt(r, 2).toString());
                txtCCCD.setText(tableModel.getValueAt(r, 3).toString());
                txtAddress.setText(tableModel.getValueAt(r, 4).toString());
            }
        });
         btnReset.addActionListener(e -> {
                clear();
                loadData();
        });

        btnThem.addActionListener(e -> {
            Customer c = new Customer(
                0,
                txtFullName.getText(),
                txtPhone.getText(),
                txtCCCD.getText(),
                txtAddress.getText()
            );
            if (customerDAO.insert(c)) {
                loadData();
                clear();
            }
        });

        btnSua.addActionListener(e -> {
            Customer c = new Customer(
                Integer.parseInt(txtCustomerID.getText()),
                txtFullName.getText(),
                txtPhone.getText(),
                txtCCCD.getText(),
                txtAddress.getText()
            );
            if (customerDAO.update(c)) {
                loadData();
                clear();
            }
        });

        btnXoa.addActionListener(e -> {
            if (customerDAO.delete(Integer.parseInt(txtCustomerID.getText()))) {
                loadData();
                clear();
            }
        });

        txtTimKiem.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                tableModel.setRowCount(0);
                for (Customer c : customerDAO.searchByName(txtTimKiem.getText())) {
                    tableModel.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getFullName(),
c.getPhone(),
                        c.getCccd(),
                        c.getAddress()
                    });
                }
            }
        });
    }

    private void clear() {
        txtCustomerID.setText("");
        txtFullName.setText("");
        txtPhone.setText("");
        txtCCCD.setText("");
        txtAddress.setText("");
        txtTimKiem.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}