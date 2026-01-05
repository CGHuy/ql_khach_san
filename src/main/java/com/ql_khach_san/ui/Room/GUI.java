/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ql_khach_san.ui.Room;

import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.dao.RoomTypeDAO;
import com.ql_khach_san.model.Room;
import com.ql_khach_san.model.RoomType;
import com.ql_khach_san.model.Floor;
import com.ql_khach_san.dao.FloorDAO;

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
	private JComboBox<String> cmbFloor;
	private JComboBox<String> cmbRoomType;
	private JComboBox<String> cmbStatus;
	private JButton btnAdd, btnEdit, btnDelete, btnNew;

	private JTable table;
	private DefaultTableModel tableModel;

	private RoomDAO roomDao = new RoomDAO();
	private RoomTypeDAO typeDao = new RoomTypeDAO();
	private FloorDAO floorDao = new FloorDAO();

	private List<Room> roomList = new ArrayList<>();
	private List<Room> displayedList = new ArrayList<>();
	private List<RoomType> typeList = new ArrayList<>();
	private List<Floor> floorList = new ArrayList<>();

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

		c.gridx = 0; c.gridy = 1; left.add(new JLabel("Tầng"), c);
		c.gridx = 1; cmbFloor = new JComboBox<>(); left.add(cmbFloor, c);

		c.gridx = 0; c.gridy = 2; left.add(new JLabel("Loại Phòng"), c);
		c.gridx = 1; cmbRoomType = new JComboBox<>(); left.add(cmbRoomType, c);

		c.gridx = 0; c.gridy = 3; left.add(new JLabel("Trạng Thái"), c);
		c.gridx = 1; cmbStatus = new JComboBox<>(); left.add(cmbStatus, c);



		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
		btnAdd = new JButton("Thêm");
		btnEdit = new JButton("Sửa");
		btnDelete = new JButton("Xóa");
		btnNew = new JButton("Mới");
		buttons.add(btnAdd); buttons.add(btnEdit); buttons.add(btnDelete); buttons.add(btnNew);

		// Table on right (add column "Tầng" after Phòng)
		tableModel = new DefaultTableModel(new Object[]{"Phòng","Tầng","Loại Phòng","Giá","Trạng Thái"},0) {
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

		// load types, floors, statuses and data
		loadRoomTypes();
		loadFloors();
		loadStatuses();
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
				if (r >= 0 && r < displayedList.size()) {
					Room sel = displayedList.get(r);
					txtRoomNumber.setText(sel.getRoomNumber());
					// select type
					int typeId = sel.getTypeId();
					for (int i=0;i<typeList.size();i++) if (typeList.get(i).getTypeId()==typeId) { cmbRoomType.setSelectedIndex(i); break; }
							cmbStatus.setSelectedItem(sel.getStatus());
							// set floor selection by matching floorId
							if (sel.getFloorId() > 0) {
								for (int i = 0; i < floorList.size(); i++) {
									if (floorList.get(i).getFloorId() == sel.getFloorId()) { cmbFloor.setSelectedIndex(i); break; }
								}
							}
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

	private void loadFloors() {
		floorList = floorDao.getAll();
		cmbFloor.removeAllItems();
		for (Floor f : floorList) {
			cmbFloor.addItem("Tầng " + f.getFloorNumber());
		}
	}

	private void loadStatuses() {
		List<String> statuses = roomDao.getDistinctStatuses();
		cmbStatus.removeAllItems();
		if (statuses == null || statuses.isEmpty()) {
			cmbStatus.addItem("Trống");
			cmbStatus.addItem("Đã có người ở");
			return;
		}
		for (String s : statuses) cmbStatus.addItem(s);
	}

	private void loadTable() {
		tableModel.setRowCount(0);
		roomList = roomDao.getAll();
		displayedList.clear();
		if (roomDao.getLastError() != null && !roomDao.getLastError().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Lỗi kết nối Database:\n" + roomDao.getLastError(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String q = txtSearch == null ? "" : txtSearch.getText().trim().toLowerCase();
		for (Room r : roomList) {
			RoomType rt = typeDao.getById(r.getTypeId());
			String typeName = rt == null ? "" : rt.getTypeName();
			Object price = rt == null ? "" : rt.getPrice();
			Floor fl = null;
			for (Floor f : floorList) if (f.getFloorId() == r.getFloorId()) { fl = f; break; }
			String floor = fl == null ? (r.getFloorId() > 0 ? ("Tầng " + r.getFloorId()) : "") : ("Tầng " + fl.getFloorNumber());
			String combined = (r.getRoomNumber() + " " + typeName + " " + floor).toLowerCase();
			if (!q.isEmpty() && !combined.contains(q)) continue;
			tableModel.addRow(new Object[]{r.getRoomNumber(), floor, typeName, price, r.getStatus()});
			displayedList.add(r);
		}
	}

    private void onAdd() {
		try {
			String roomNum = txtRoomNumber.getText() == null ? "" : txtRoomNumber.getText().trim();
			if (roomNum.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập số phòng"); return; }
			int idx = cmbRoomType.getSelectedIndex();
			if (idx < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn loại phòng"); return; }
			// check duplicate room number
			if (roomDao.existsByRoomNumber(roomNum)) { JOptionPane.showMessageDialog(this, "Số phòng đã tồn tại"); return; }
			Room r = new Room();
			r.setRoomNumber(roomNum);
			if (idx >= 0) r.setTypeId(typeList.get(idx).getTypeId());
			// floor selection -> floorId using floorList
			int fidx = cmbFloor.getSelectedIndex();
			if (fidx >= 0 && fidx < floorList.size()) r.setFloorId(floorList.get(fidx).getFloorId());
			r.setStatus((String) cmbStatus.getSelectedItem());
			if (roomDao.insert(r)) {
				JOptionPane.showMessageDialog(this, "Thêm thành công");
				loadTable();
				clearForm();
			} else JOptionPane.showMessageDialog(this, "Thêm thất bại");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
	}

	private void onEdit(ActionEvent e) {
		int r = table.getSelectedRow();
		if (r < 0 || r >= displayedList.size()) { JOptionPane.showMessageDialog(this, "Chọn phòng để sửa"); return; }
		try {
			Room rr = displayedList.get(r);
			String roomNum = txtRoomNumber.getText() == null ? "" : txtRoomNumber.getText().trim();
			if (roomNum.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập số phòng"); return; }
			// check duplicate excluding current
			if (roomDao.existsByRoomNumberExcludingId(roomNum, rr.getRoomId())) { JOptionPane.showMessageDialog(this, "Số phòng đã tồn tại"); return; }
			rr.setRoomNumber(roomNum);
			int idx = cmbRoomType.getSelectedIndex();
			if (idx >= 0) rr.setTypeId(typeList.get(idx).getTypeId());
			int fidx2 = cmbFloor.getSelectedIndex();
			if (fidx2 >= 0 && fidx2 < floorList.size()) rr.setFloorId(floorList.get(fidx2).getFloorId());
			rr.setStatus((String) cmbStatus.getSelectedItem());
			if (roomDao.update(rr)) { JOptionPane.showMessageDialog(this, "Cập nhật thành công"); loadTable(); }
			else JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
	}

	private void onDelete(ActionEvent e) {
		int r = table.getSelectedRow();
		if (r < 0 || r >= displayedList.size()) { JOptionPane.showMessageDialog(this, "Chọn phòng để xóa"); return; }
		Room sel = displayedList.get(r);
		int ok = JOptionPane.showConfirmDialog(this, "Xóa phòng " + sel.getRoomNumber() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) return;
		try {
			boolean success = roomDao.delete(sel.getRoomId());
			if (success) { JOptionPane.showMessageDialog(this, "Xóa thành công"); loadTable(); }
			else {
				String err = roomDao.getLastError();
				String msg = "Xóa thất bại. Kiểm tra ràng buộc dữ liệu.";
				if (err != null && !err.isEmpty()) msg += "\nChi tiết: " + err;
				JOptionPane.showMessageDialog(this, msg);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
		}
	}

	private void clearForm() {
		txtRoomNumber.setText("");
		if (cmbFloor.getItemCount() > 0) cmbFloor.setSelectedIndex(0);
		if (cmbRoomType.getItemCount()>0) cmbRoomType.setSelectedIndex(0);
		if (cmbStatus.getItemCount() > 0) cmbStatus.setSelectedIndex(0);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
	}

}
