/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ql_khach_san.ui.RoomType;

import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.model.RoomType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GUI extends JFrame {

	private JTextField txtTypeId, txtTypeName, txtPriceHour;
	private JTextField txtPriceDay;
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
		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(new Color(0, 175, 210));
		top.setPreferredSize(new Dimension(0, 220));

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 18, 10, 18);
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblId = new JLabel("Mã loại phòng"); lblId.setForeground(Color.WHITE);
		JLabel lblName = new JLabel("Tên loại phòng"); lblName.setForeground(Color.WHITE);
		JLabel lblPriceHour = new JLabel("Giá phòng"); lblPriceHour.setForeground(Color.WHITE);
		JLabel lblPriceDay = new JLabel("Mô tả"); lblPriceDay.setForeground(Color.WHITE);

		c.gridx = 0; c.gridy = 0; form.add(lblId, c);
		c.gridx = 1; txtTypeId = new JTextField(12); form.add(txtTypeId, c);

		c.gridx = 0; c.gridy = 1; form.add(lblName, c);
		c.gridx = 1; txtTypeName = new JTextField(20); form.add(txtTypeName, c);

		c.gridx = 2; c.gridy = 0; form.add(lblPriceHour, c);
		c.gridx = 3; txtPriceHour = new JTextField(14); form.add(txtPriceHour, c);

		c.gridx = 2; c.gridy = 1; form.add(lblPriceDay, c);
		c.gridx = 3; txtPriceDay = new JTextField(14); form.add(txtPriceDay, c);

		JPanel buttons = new JPanel(new GridLayout(3,1,10,10));
		buttons.setOpaque(false);
		JButton btnAdd = new JButton("Thêm");
		JButton btnEdit = new JButton("Sửa");
		JButton btnDelete = new JButton("Xóa");
		Dimension btnSize = new Dimension(100,34);
		for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete}) {
			b.setPreferredSize(btnSize);
			b.setBackground(new Color(60,60,60));
			b.setForeground(Color.WHITE);
			b.setFocusPainted(false);
			b.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
			buttons.add(b);
		}

		top.add(form, BorderLayout.CENTER);
		JPanel rightButtons = new JPanel(new GridBagLayout());
		rightButtons.setOpaque(false);
		rightButtons.add(buttons);
		top.add(rightButtons, BorderLayout.EAST);

		tableModel = new DefaultTableModel(new Object[]{"Mã loại phòng","Tên loại phòng","Giá theo giờ","Giá theo ngày"},0) {
			public boolean isCellEditable(int row, int column) { return false; }
		};
		table = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(table);

		btnAdd.addActionListener(this::onAdd);
		btnEdit.addActionListener(this::onEdit);
		btnDelete.addActionListener(this::onDelete);

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int r = table.getSelectedRow();
				if (r >= 0) {
					txtTypeId.setText(tableModel.getValueAt(r,0).toString());
					txtTypeName.setText(tableModel.getValueAt(r,1).toString());
					txtPriceHour.setText(tableModel.getValueAt(r,2).toString());
					txtPriceDay.setText(tableModel.getValueAt(r,3)==null?"":tableModel.getValueAt(r,3).toString());
				}
			}
		});

		getContentPane().setLayout(new BorderLayout(8,8));
		getContentPane().add(top, BorderLayout.NORTH);
		getContentPane().add(scroll, BorderLayout.CENTER);

		// style table and container footer
		scroll.getViewport().setBackground(Color.WHITE);

		loadTable();
		setSize(980, 600);
	}

	private void loadTable() {
		tableModel.setRowCount(0);
		List<RoomType> list = dao.getAll();
		for (RoomType rt : list) {
			tableModel.addRow(new Object[]{rt.getTypeId(), rt.getTypeName(), rt.getPrice(), rt.getDescription()});
		}
	}

	private void onAdd(ActionEvent e) {
		try {
			RoomType rt = new RoomType();
			rt.setTypeName(txtTypeName.getText().trim());
			rt.setPrice(Double.parseDouble(txtPriceHour.getText().trim()));
			rt.setDescription(txtPriceDay.getText().trim());
			if (dao.insert(rt)) { JOptionPane.showMessageDialog(this, "Thêm thành công"); loadTable(); txtTypeId.setText(String.valueOf(rt.getTypeId())); }
			else JOptionPane.showMessageDialog(this, "Thêm thất bại");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage()); }
	}

	private void onEdit(ActionEvent e) {
		try {
			RoomType rt = new RoomType();
			rt.setTypeId(Integer.parseInt(txtTypeId.getText().trim()));
			rt.setTypeName(txtTypeName.getText().trim());
			rt.setPrice(Double.parseDouble(txtPriceHour.getText().trim()));
			rt.setDescription(txtPriceDay.getText().trim());
			if (dao.update(rt)) { JOptionPane.showMessageDialog(this, "Cập nhật thành công"); loadTable(); }
			else JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage()); }
	}

	private void onDelete(ActionEvent e) {
		try {
			int id = Integer.parseInt(txtTypeId.getText().trim());
			int ok = JOptionPane.showConfirmDialog(this, "Xóa loại phòng " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
			if (ok == JOptionPane.YES_OPTION) {
				if (dao.delete(id)) { JOptionPane.showMessageDialog(this, "Xóa thành công"); loadTable(); }
				else JOptionPane.showMessageDialog(this, "Xóa thất bại");
			}
		} catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Mã loại phòng không hợp lệ"); }
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
	}

}
