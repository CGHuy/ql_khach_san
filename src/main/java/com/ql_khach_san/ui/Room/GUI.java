/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ql_khach_san.ui.Room;

import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.model.Room;
import com.ql_khach_san.model.RoomType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

	private JTextField txtRoomNumber;
	private JTextField txtSearch;
	private JTextArea txtNote;
	private JComboBox<String> cmbPrefix;
	private JComboBox<String> cmbFloor;
	private JComboBox<String> cmbRoomType;
	private JComboBox<String> cmbStatus;
	private JCheckBox chkMany;
	private JButton btnAdd, btnEdit, btnDelete, btnNew;

	private JTable table;
	private DefaultTableModel tableModel;

	private RoomDAO roomDao = new RoomDAO();
	private RoomTypeDAO typeDao = new RoomTypeDAO();

	private List<Room> roomList = new ArrayList<>();
	private List<RoomType> typeList = new ArrayList<>();

	public GUI() {
		setTitle("Quản Lý Phòng");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initComponents();
		setLocationRelativeTo(null);
	}

	private void initComponents() {
		// set overall background color similar to mock
		Color bg = new Color(224, 236, 243);
		JPanel left = new JPanel();
		left.setBackground(bg);
		left.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(12, 14, 12, 14);
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblTitle = new JLabel("Quản Lý Phòng");
		lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setBackground(bg);
		titlePanel.add(lblTitle);

		// Search panel on top
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
		searchPanel.setBackground(bg);
		JLabel lblSearch = new JLabel("Tìm kiếm:"); lblSearch.setForeground(Color.DARK_GRAY);
		txtSearch = new JTextField(28);
		txtSearch.setToolTipText("Nhập tên hoặc mã phòng...");
		searchPanel.add(lblSearch);
		searchPanel.add(txtSearch);

		c.gridx = 0; c.gridy = 0; left.add(new JLabel("Phòng"), c);
		c.gridx = 1; txtRoomNumber = new JTextField(12); left.add(txtRoomNumber, c);

		c.gridx = 0; c.gridy = 1; left.add(new JLabel("Ký Hiệu"), c);
		c.gridx = 1; cmbPrefix = new JComboBox<>(new String[]{"A","B","C","D"}); left.add(cmbPrefix, c);

		c.gridx = 0; c.gridy = 2; left.add(new JLabel("Tầng"), c);
		c.gridx = 1; cmbFloor = new JComboBox<>(new String[]{"Tầng 1","Tầng 2","Tầng 3","Tầng 4"}); left.add(cmbFloor, c);

		c.gridx = 0; c.gridy = 3; left.add(new JLabel("Loại Phòng"), c);
		c.gridx = 1; cmbRoomType = new JComboBox<>(); left.add(cmbRoomType, c);

		c.gridx = 0; c.gridy = 4; left.add(new JLabel("Trạng Thái"), c);
		c.gridx = 1; cmbStatus = new JComboBox<>(new String[]{"Trống","Đã có người ở"}); left.add(cmbStatus, c);

		c.gridx = 0; c.gridy = 5; left.add(new JLabel("Ghi Chú"), c);
		c.gridx = 1; txtNote = new JTextArea(6, 16); JScrollPane noteScroll = new JScrollPane(txtNote); left.add(noteScroll, c);

		c.gridx = 0; c.gridy = 6; chkMany = new JCheckBox("Thêm Nhiều"); left.add(chkMany, c);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
		btnAdd = new JButton("Thêm");
		btnEdit = new JButton("Sửa");
		btnDelete = new JButton("Xóa");
		btnNew = new JButton("Mới");
		buttons.add(btnAdd); buttons.add(btnEdit); buttons.add(btnDelete); buttons.add(btnNew);

		// Table on right (add column "Tầng" after Phòng)
		tableModel = new DefaultTableModel(new Object[]{"Phòng","Tầng","Loại Phòng","Giá","Trạng Thái","Ghi Chú"},0) {
			public boolean isCellEditable(int row, int column) { return false; }
		};
		table = new JTable(tableModel);
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		table.setRowHeight(26);
		table.getTableHeader().setReorderingAllowed(false);
		if (table.getColumnModel().getColumnCount() >= 5) {
			table.getColumnModel().getColumn(0).setPreferredWidth(90);
			table.getColumnModel().getColumn(1).setPreferredWidth(220);
			table.getColumnModel().getColumn(2).setPreferredWidth(120);
			table.getColumnModel().getColumn(3).setPreferredWidth(120);
			table.getColumnModel().getColumn(4).setPreferredWidth(260);
		}

		// layout main
		JPanel formPanel = new JPanel(new BorderLayout());
		formPanel.add(titlePanel, BorderLayout.NORTH);
		formPanel.add(left, BorderLayout.CENTER);
		formPanel.add(buttons, BorderLayout.SOUTH);

		getContentPane().setLayout(new BorderLayout(10,10));
		try { ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(12,12,12,12)); } catch (Exception ex) {}
		getContentPane().add(searchPanel, BorderLayout.NORTH);
		getContentPane().add(formPanel, BorderLayout.WEST);
		getContentPane().add(tableScroll, BorderLayout.CENTER);

		// load types and data
		loadRoomTypes();
		loadTable();

		// search & sort listeners
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { loadTable(); }
			public void removeUpdate(DocumentEvent e) { loadTable(); }
			public void changedUpdate(DocumentEvent e) { loadTable(); }
		});



		// listeners
		btnAdd.addActionListener(e -> onAdd());
		btnEdit.addActionListener(this::onEdit);
		btnDelete.addActionListener(this::onDelete);
		btnNew.addActionListener(e -> clearForm());

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int r = table.getSelectedRow();
				if (r >= 0 && r < roomList.size()) {
					Room sel = roomList.get(r);
					txtRoomNumber.setText(sel.getRoomNumber());
					// select type
					int typeId = sel.getTypeId();
					for (int i=0;i<typeList.size();i++) if (typeList.get(i).getTypeId()==typeId) { cmbRoomType.setSelectedIndex(i); break; }
							cmbStatus.setSelectedItem(sel.getStatus());
							// set floor selection (assume floor ids are 1-based)
							if (sel.getFloorId() > 0 && sel.getFloorId() - 1 < cmbFloor.getItemCount()) cmbFloor.setSelectedIndex(sel.getFloorId() - 1);
							txtNote.setText(sel.getNote() == null ? "" : sel.getNote());
				}
			}
		});

		setSize(1000,600);
	}

	private void loadRoomTypes() {
		typeList = typeDao.getAll();
		cmbRoomType.removeAllItems();
		for (RoomType t: typeList) {
			cmbRoomType.addItem(t.getTypeName());
		}
	}

	private void loadTable() {
		tableModel.setRowCount(0);
		roomList = roomDao.getAll();
		String q = txtSearch == null ? "" : txtSearch.getText().trim().toLowerCase();
		for (Room r : roomList) {
			RoomType rt = typeDao.getById(r.getTypeId());
			String typeName = rt == null ? "" : rt.getTypeName();
			Object price = rt == null ? "" : rt.getPrice();
			Object note = r.getNote() == null ? "" : r.getNote();
			String floor = r.getFloorId() > 0 ? ("Tầng " + r.getFloorId()) : "";
			String combined = (r.getRoomNumber() + " " + typeName + " " + floor).toLowerCase();
			if (!q.isEmpty() && !combined.contains(q)) continue;
			tableModel.addRow(new Object[]{r.getRoomNumber(), floor, typeName, price, r.getStatus(), note});
		}
	}

    private void onAdd() {
		try {
			Room r = new Room();
			r.setRoomNumber(txtRoomNumber.getText().trim());
			int idx = cmbRoomType.getSelectedIndex();
			if (idx >= 0) r.setTypeId(typeList.get(idx).getTypeId());
			// floor selection -> floorId (assuming floors are 1-based in DB)
			r.setFloorId(cmbFloor.getSelectedIndex() + 1);
			r.setStatus((String) cmbStatus.getSelectedItem());
			r.setNote(txtNote.getText().trim());
			if (roomDao.insert(r)) {
				JOptionPane.showMessageDialog(this, "Thêm thành công");
				loadTable();
				if (chkMany.isSelected()) {
					txtRoomNumber.setText("");
					txtRoomNumber.requestFocus();
				} else clearForm();
			} else JOptionPane.showMessageDialog(this, "Thêm thất bại");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
	}

	private void onEdit(ActionEvent e) {
		int r = table.getSelectedRow();
		if (r < 0 || r >= roomList.size()) { JOptionPane.showMessageDialog(this, "Chọn phòng để sửa"); return; }
		try {
			Room rr = roomList.get(r);
			rr.setRoomNumber(txtRoomNumber.getText().trim());
			int idx = cmbRoomType.getSelectedIndex();
			if (idx >= 0) rr.setTypeId(typeList.get(idx).getTypeId());
			rr.setFloorId(cmbFloor.getSelectedIndex() + 1);
			rr.setStatus((String) cmbStatus.getSelectedItem());
			rr.setNote(txtNote.getText().trim());
			if (roomDao.update(rr)) { JOptionPane.showMessageDialog(this, "Cập nhật thành công"); loadTable(); }
			else JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
	}

	private void onDelete(ActionEvent e) {
		int r = table.getSelectedRow();
		if (r < 0 || r >= roomList.size()) { JOptionPane.showMessageDialog(this, "Chọn phòng để xóa"); return; }
		int ok = JOptionPane.showConfirmDialog(this, "Xóa phòng " + roomList.get(r).getRoomNumber() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			if (roomDao.delete(roomList.get(r).getRoomId())) { JOptionPane.showMessageDialog(this, "Xóa thành công"); loadTable(); }
			else JOptionPane.showMessageDialog(this, "Xóa thất bại");
		}
	}

	private void clearForm() {
		txtRoomNumber.setText("");
		cmbPrefix.setSelectedIndex(0);
		cmbFloor.setSelectedIndex(0);
		if (cmbRoomType.getItemCount()>0) cmbRoomType.setSelectedIndex(0);
		cmbStatus.setSelectedIndex(0);
		txtNote.setText("");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
	}

}
