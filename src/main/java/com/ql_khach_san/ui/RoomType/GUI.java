/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ql_khach_san.ui.RoomType;

import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.model.RoomType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GUI extends JFrame {

    private JTextField txtTypeId, txtTypeName, txtPrice;
    private JTextField txtDescription;
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private RoomTypeDAO dao = new RoomTypeDAO();

    public GUI() {
        setTitle("QUẢN LÝ LOẠI PHÒNG");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // 1. Giữ nguyên màu nền gốc từ code đầu tiên bạn gửi
        Color bg = new Color(224, 236, 243);
        getContentPane().setBackground(bg);
        getContentPane().setLayout(new BorderLayout(0, 10));

        // --- PHẦN TRÊN (NORTH): Chứa Tìm kiếm và Form ---
        JPanel topContainer = new JPanel(new BorderLayout(0, 5));
        topContainer.setOpaque(false);
        topContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Thanh tìm kiếm (Giữ nguyên từ code đầu)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(28);
        txtSearch.setToolTipText("Tìm theo tên loại phòng...");
        searchPanel.add(new JLabel("Tìm kiếm: "));
        searchPanel.add(txtSearch);
        topContainer.add(searchPanel, BorderLayout.NORTH);

        // Panel chứa Form và Nút bấm (Dàn hàng ngang)
        JPanel mainInputPanel = new JPanel(new BorderLayout());
        mainInputPanel.setOpaque(false);
        mainInputPanel.setPreferredSize(new Dimension(0, 220)); // Cố định chiều cao để không bị bảng đè

        // Form 2 hàng, kéo dài thanh viết
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 15, 5, 15);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Hàng 1
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Mã loại phòng"), c);
        c.gridx = 1; c.gridy = 0; form.add(new JLabel("Giá"), c);
        
        c.gridx = 0; c.gridy = 1; 
        txtTypeId = new JTextField(); 
        txtTypeId.setPreferredSize(new Dimension(0, 30));
        txtTypeId.setEditable(false); form.add(txtTypeId, c);

        c.gridx = 1; c.gridy = 1; 
        txtPrice = new JTextField(); 
        txtPrice.setPreferredSize(new Dimension(0, 30));
        form.add(txtPrice, c);

        // Hàng 2
        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Tên loại phòng"), c);
        c.gridx = 1; c.gridy = 2; form.add(new JLabel("Mô tả"), c);

        c.gridx = 0; c.gridy = 3; 
        txtTypeName = new JTextField(); 
        txtTypeName.setPreferredSize(new Dimension(0, 30));
        form.add(txtTypeName, c);

        c.gridx = 1; c.gridy = 3; 
        txtDescription = new JTextField(); 
        txtDescription.setPreferredSize(new Dimension(0, 30));
        form.add(txtDescription, c);

        // Cụm nút bấm (Màu xám trắng giống hình Room)
        JPanel buttons = new JPanel(new GridLayout(4, 1, 0, 8));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        JButton btnAdd = createStyledButton("Thêm");
        JButton btnEdit = createStyledButton("Sửa");
        JButton btnDelete = createStyledButton("Xóa");
        JButton btnClear = createStyledButton("Mới");

        buttons.add(btnAdd); buttons.add(btnEdit); buttons.add(btnDelete); buttons.add(btnClear);

        mainInputPanel.add(form, BorderLayout.CENTER);
        mainInputPanel.add(buttons, BorderLayout.EAST);
        
        topContainer.add(mainInputPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // --- PHẦN DƯỚI (CENTER): Bảng dữ liệu ---
        tableModel = new DefaultTableModel(new Object[]{"Mã loại phòng","Tên loại phòng","Giá","Mô tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(scroll, BorderLayout.CENTER); // Center sẽ tự động co giãn phần còn lại

        // --- LOGIC XỬ LÝ (Giữ nguyên 100% từ code đầu tiên của bạn) ---
        btnAdd.addActionListener(this::onAdd);
        btnEdit.addActionListener(this::onEdit);
        btnDelete.addActionListener(this::onDelete);
        btnClear.addActionListener(e -> {
            txtTypeId.setText(""); txtTypeName.setText(""); txtPrice.setText(""); txtDescription.setText("");
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtTypeId.setText(tableModel.getValueAt(r,0).toString());
                    txtTypeName.setText(tableModel.getValueAt(r,1).toString());
                    txtPrice.setText(tableModel.getValueAt(r,2)==null?"":tableModel.getValueAt(r,2).toString());
                    txtDescription.setText(tableModel.getValueAt(r,3)==null?"":tableModel.getValueAt(r,3).toString());
                }
            }
        });

        loadTable();

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadTable(); }
            public void removeUpdate(DocumentEvent e) { loadTable(); }
            public void changedUpdate(DocumentEvent e) { loadTable(); }
        });

        setSize(1000, 650);
    }

    // Hàm tạo Button style xám trắng (Room style)
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setBackground(new Color(240, 240, 240));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return btn;
    }

    // --- CÁC HÀM DAO (Giữ nguyên từ code đầu tiên) ---
    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<RoomType> list = dao.getAll();
            String q = txtSearch == null ? "" : txtSearch.getText().trim().toLowerCase();
            for (RoomType rt : list) {
                if (!q.isEmpty() && !rt.getTypeName().toLowerCase().contains(q)) continue;
                tableModel.addRow(new Object[]{rt.getTypeId(), rt.getTypeName(), rt.getPrice(), rt.getDescription()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void onAdd(ActionEvent e) {
        try {
            RoomType rt = new RoomType();
            rt.setTypeName(txtTypeName.getText().trim());
            rt.setPrice(Double.parseDouble(txtPrice.getText().trim()));
            rt.setDescription(txtDescription.getText().trim());
            if (dao.insert(rt)) { JOptionPane.showMessageDialog(this, "Thêm thành công"); loadTable(); }
            else JOptionPane.showMessageDialog(this, "Thêm thất bại");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage()); }
    }

    private void onEdit(ActionEvent e) {
        try {
            RoomType rt = new RoomType();
            rt.setTypeId(Integer.parseInt(txtTypeId.getText().trim()));
            rt.setTypeName(txtTypeName.getText().trim());
            rt.setPrice(Double.parseDouble(txtPrice.getText().trim()));
            rt.setDescription(txtDescription.getText().trim());
            if (dao.update(rt)) { JOptionPane.showMessageDialog(this, "Cập nhật thành công"); loadTable(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi sửa: " + ex.getMessage()); }
    }

    private void onDelete(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtTypeId.getText().trim());
            if (dao.delete(id)) { JOptionPane.showMessageDialog(this, "Xóa thành công"); loadTable(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi xóa!"); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */