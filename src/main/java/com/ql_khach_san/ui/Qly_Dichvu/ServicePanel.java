package com.ql_khach_san.ui.Qly_Dichvu;

import com.ql_khach_san.dao.ServiceDAO;
import com.ql_khach_san.model.Service;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class ServicePanel extends JPanel {

    private JTextField txtSearch;
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPrice;

    private JButton btnSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    private JTable table;
    private DefaultTableModel tableModel;

    private ServiceDAO serviceDAO = new ServiceDAO();
    private DecimalFormat priceFormat = new DecimalFormat("#,##0");

    public ServicePanel() {
        initComponents();
        loadTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(245, 245, 250));

        // Top: Title + Search
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BorderLayout());
        topWrapper.setBackground(new Color(230, 240, 250));

        JLabel lblTitle = new JLabel("Quản lý Dịch vụ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(40, 70, 130));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
        topWrapper.add(lblTitle, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        top.setBackground(new Color(230, 240, 250));
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnSearch = new JButton(" Tìm kiếm");
        btnRefresh = new JButton(" Làm mới");
        styleButton(btnSearch);
        styleButton(btnRefresh);
        top.add(txtSearch);
        top.add(btnSearch);
        top.add(btnRefresh);
        topWrapper.add(top, BorderLayout.SOUTH);
        add(topWrapper, BorderLayout.NORTH);

        // Center: Table
        tableModel = new DefaultTableModel(new Object[]{"Mã Dịch Vụ", "Tên dịch vụ", "Giá"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 2));
        add(scroll, BorderLayout.CENTER);

        // Right: Form & Buttons
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(new Color(245, 245, 250));
        right.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        JLabel lblId = new JLabel("Mã dịch vụ:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtId = new JTextField(15);
        txtId.setEnabled(false);
        txtId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        right.add(lblId);
        right.add(txtId);

        right.add(Box.createVerticalStrut(10));
        JLabel lblName = new JLabel("Tên dịch vụ:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtName = new JTextField(15);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        right.add(lblName);
        right.add(txtName);

        right.add(Box.createVerticalStrut(10));
        JLabel lblPrice = new JLabel("Giá:");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtPrice = new JTextField(15);
        txtPrice.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        right.add(lblPrice);
        right.add(txtPrice);

        right.add(Box.createVerticalStrut(18));
        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        styleButton(btnAdd);
        styleButton(btnEdit);
        styleButton(btnDelete);
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnEdit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnDelete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        right.add(btnAdd);
        right.add(Box.createVerticalStrut(8));
        right.add(btnEdit);
        right.add(Box.createVerticalStrut(8));
        right.add(btnDelete);

        add(right, BorderLayout.EAST);
   

        // Actions
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAction();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadTable();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAction();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editAction();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAction();
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();
                    if (row >= 0) populateFormFromRow(row);
                }
            }
        });
    }
     private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(220, 230, 250));
        btn.setForeground(new Color(40, 70, 130));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void populateFormFromRow(int row) {
        txtId.setText(table.getValueAt(row, 0).toString());
        txtName.setText(table.getValueAt(row, 1).toString());
        txtPrice.setText(table.getValueAt(row, 2).toString().replaceAll("\u00A0", ""));
    }

    private void searchAction() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadTable();
            return;
        }
        List<Service> all = serviceDAO.getAll();
        tableModel.setRowCount(0);
        for (Service s : all) {
            if (String.valueOf(s.getServiceId()).equals(q) || s.getServiceName().toLowerCase().contains(q.toLowerCase())) {
                tableModel.addRow(new Object[]{s.getServiceId(), s.getServiceName(), priceFormat.format(s.getPrice())});
            }
        }
    }

    private void loadTable() {
        List<Service> list = serviceDAO.getAll();
        tableModel.setRowCount(0);
        for (Service s : list) {
            tableModel.addRow(new Object[]{s.getServiceId(), s.getServiceName(), priceFormat.format(s.getPrice())});
        }
    }

    private void addAction() {
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và giá không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double price;
        try {
            double tmp = Double.parseDouble(priceText.replaceAll(",", ""));
            price = (double) ((long) tmp); // remove decimal part by truncation
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Service s = new Service();
        s.setServiceName(name);
        s.setPrice(price);
        boolean ok = serviceDAO.insert(s);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công", "Message", JOptionPane.INFORMATION_MESSAGE);
            txtId.setText(String.valueOf(s.getServiceId()));
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thất bại", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAction() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ để sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = Integer.parseInt(idText);
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và giá không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double price;
        try {
            double tmp = Double.parseDouble(priceText.replaceAll(",", ""));
            price = (double) ((long) tmp); // remove decimal part by truncation
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Service s = new Service(id, name, price);
        boolean ok = serviceDAO.update(s);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thành công", "Message", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAction() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = Integer.parseInt(idText);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = serviceDAO.delete(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa thành công", "Message", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
    }
}
