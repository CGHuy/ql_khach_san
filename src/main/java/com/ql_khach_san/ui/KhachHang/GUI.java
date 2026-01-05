package com.ql_khach_san.ui.KhachHang;

import com.ql_khach_san.dao.CustomerDAO;
import com.ql_khach_san.model.Customer;
import com.ql_khach_san.ui.KhachHang.ReservationHistoryDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

    private JTextField txtCustomerID, txtFullName, txtPhone, txtCCCD, txtAddress, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnReset, btnXemLichSu;
    private JTable table;
    private DefaultTableModel tableModel;

    private CustomerDAO customerDAO = new CustomerDAO();

    public GUI() {
        initComponents();
        loadData();
        setTitle("QUẢN LÝ KHÁCH HÀNG");
        setPreferredSize(new Dimension(1200, 700));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // Main panel (border layout)
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content split: top form + buttons, center table
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Color.WHITE);

        // Form panel (left) using GridBag
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Row 0: ID (read-only) and CCCD
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblCustomerID = new JLabel("Mã khách hàng:"); lblCustomerID.setFont(labelFont);
        formPanel.add(lblCustomerID, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCustomerID = new JTextField(); txtCustomerID.setEditable(false); txtCustomerID.setFont(fieldFont);
        txtCustomerID.setBackground(new Color(245,245,245)); txtCustomerID.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtCustomerID, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblCCCD = new JLabel("CCCD:"); lblCCCD.setFont(labelFont);
        formPanel.add(lblCCCD, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCCCD = new JTextField(); txtCCCD.setFont(fieldFont);
        txtCCCD.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtCCCD, gbc);

        // Row 1: Fullname and Phone
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblFullName = new JLabel("Họ và tên:"); lblFullName.setFont(labelFont);
        formPanel.add(lblFullName, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFullName = new JTextField(); txtFullName.setFont(fieldFont);
        txtFullName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtFullName, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblPhone = new JLabel("Số điện thoại:"); lblPhone.setFont(labelFont);
        formPanel.add(lblPhone, gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtPhone = new JTextField(); txtPhone.setFont(fieldFont);
        txtPhone.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtPhone, gbc);

        // Row 2: Address (spans)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblAddress = new JLabel("Địa chỉ:"); lblAddress.setFont(labelFont);
        formPanel.add(lblAddress, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtAddress = new JTextField(); txtAddress.setFont(fieldFont);
        txtAddress.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtAddress, gbc);
        gbc.gridwidth = 1; // reset

        // Buttons row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnThem = new JButton("Thêm"); btnSua = new JButton("Sửa"); btnXoa = new JButton("Xóa");
        btnReset = new JButton("Reset"); btnXemLichSu = new JButton("Xem lịch sử");
        Dimension btnSize = new Dimension(110, 34);
        for (JButton b : new JButton[]{btnThem, btnSua, btnXoa, btnReset, btnXemLichSu}) { b.setPreferredSize(btnSize); b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); }
        btnXemLichSu.setBackground(new Color(33,150,243)); btnXemLichSu.setForeground(Color.WHITE);
        btnPanel.add(btnThem); btnPanel.add(btnSua); btnPanel.add(btnXoa); btnPanel.add(btnReset); btnPanel.add(btnXemLichSu);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(btnPanel, gbc);
        gbc.gridwidth = 1; // reset

        // Search field above table (compact)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0)); // small gap below search
        JLabel lblTimKiem = new JLabel("Tìm kiếm:"); lblTimKiem.setFont(labelFont);
        txtTimKiem = new JTextField(24); txtTimKiem.setFont(fieldFont);
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        searchPanel.add(lblTimKiem); searchPanel.add(txtTimKiem);

        // Table panel
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Họ và tên", "SĐT", "CCCD", "Địa chỉ"}, 0
        ) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24); // slightly smaller rows to reduce overall table height
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(new Color(204,229,255));
        table.getTableHeader().setBackground(new Color(245,245,245));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setPreferredScrollableViewportSize(new Dimension(1000, 260)); // limit table viewport height

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(1000, 260)); // set preferred height for the scroll pane

        // Assemble center content: put searchPanel directly above the table
        content.add(formPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(searchPanel, BorderLayout.NORTH);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Color.WHITE);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); // reduced top gap
        tableWrapper.add(sp, BorderLayout.CENTER);

        middlePanel.add(tableWrapper, BorderLayout.CENTER);

        content.add(middlePanel, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);

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

        btnXemLichSu.addActionListener(e -> {
            if (txtCustomerID.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng trước khi xem lịch sử.");
                return;
            }
            int customerId = Integer.parseInt(txtCustomerID.getText());
            String customerName = txtFullName.getText();
            ReservationHistoryDialog dialog = new ReservationHistoryDialog(this, customerId, customerName);
            dialog.setVisible(true);
        });

        btnThem.addActionListener(e -> {
            if (txtFullName.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng!");
                return;
            }
            Customer c = new Customer(
                0,
                txtFullName.getText(),
                txtPhone.getText(),
                txtCCCD.getText(),
                txtAddress.getText()
            );
            if (customerDAO.insert(c)) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                loadData();
                clear();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            if (txtCustomerID.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
                return;
            }
            if (txtFullName.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng!");
                return;
            }
            Customer c = new Customer(
                Integer.parseInt(txtCustomerID.getText()),
                txtFullName.getText(),
                txtPhone.getText(),
                txtCCCD.getText(),
                txtAddress.getText()
            );
            if (customerDAO.update(c)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
                loadData();
                clear();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            if (txtCustomerID.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khách hàng: " + txtFullName.getText() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (customerDAO.delete(Integer.parseInt(txtCustomerID.getText()))) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
                    loadData();
                    clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Khách hàng đang đặt phòng không thể xoá!", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
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