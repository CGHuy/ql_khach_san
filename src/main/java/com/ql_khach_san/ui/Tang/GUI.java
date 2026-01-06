package com.ql_khach_san.ui.Tang;

import com.ql_khach_san.dao.FloorDAO;
import com.ql_khach_san.dao.RoomDAO;
import com.ql_khach_san.model.Floor;
import com.ql_khach_san.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GUI extends JFrame {

    private JTextField txtFloorNumber, txtDescription, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnReset;
    private JTable tableFloor, tableRoom;
    private DefaultTableModel tableFloorModel, tableRoomModel;
    private FloorDAO floorDAO = new FloorDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private int selectedFloorId = -1;

    public GUI() {
        initComponents();
        setTitle("QUẢN LÝ TẦNG");
        setPreferredSize(new Dimension(1000, 700));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadData();
    }

    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel lblTitle = new JLabel("QUẢN LÝ TẦNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Color.WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Dòng 0 - cột 0 đến 3
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblFloorNumber = new JLabel("Số Tầng:"); lblFloorNumber.setFont(labelFont);
        formPanel.add(lblFloorNumber, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFloorNumber = new JTextField(); txtFloorNumber.setFont(fieldFont);
        txtFloorNumber.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtFloorNumber, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblDescription = new JLabel("Mô Tả:"); lblDescription.setFont(labelFont);
        formPanel.add(lblDescription, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.35; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDescription = new JTextField(); txtDescription.setFont(fieldFont);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        formPanel.add(txtDescription, gbc);

        // Panel buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); btnPanel.setBackground(Color.WHITE);
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnReset = new JButton("Reset");
        Dimension btnSize = new Dimension(110, 34);
        for (JButton b : new JButton[]{btnThem, btnSua, btnXoa, btnReset}) {
             b.setPreferredSize(btnSize);
             b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        btnPanel.add(btnThem); btnPanel.add(btnSua); btnPanel.add(btnXoa); btnPanel.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(btnPanel, gbc);
        gbc.gridwidth = 1;

        // Search panel above table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));
        JLabel lblTimKiem = new JLabel("Tìm kiếm:"); lblTimKiem.setFont(labelFont);
        txtTimKiem = new JTextField(24); txtTimKiem.setFont(fieldFont);
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createEmptyBorder(6,8,6,8)));
        searchPanel.add(lblTimKiem); searchPanel.add(txtTimKiem);

        // Table Floor
        String[] columnNamesFloor = {"ID Tầng", "Số Tầng", "Mô Tả"};
        tableFloorModel = new DefaultTableModel(columnNamesFloor, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tableFloor = new JTable(tableFloorModel);
        tableFloor.setFillsViewportHeight(true);
        tableFloor.setRowHeight(24);
        tableFloor.setShowGrid(false);
        tableFloor.setIntercellSpacing(new Dimension(0,0));
        tableFloor.setSelectionBackground(new Color(204,229,255));
        tableFloor.getTableHeader().setBackground(new Color(245,245,245));
        tableFloor.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableFloor.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableFloor.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableFloor.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scrollFloor = new JScrollPane(tableFloor);
        scrollFloor.setPreferredSize(new Dimension(400, 200));

        // Table Room
        String[] columnNamesRoom = {"ID Phòng", "Số Phòng", "Loại", "Trạng Thái"};
        tableRoomModel = new DefaultTableModel(columnNamesRoom, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tableRoom = new JTable(tableRoomModel);
        tableRoom.setFillsViewportHeight(true);
        tableRoom.setRowHeight(24);
        tableRoom.setShowGrid(false);
        tableRoom.setIntercellSpacing(new Dimension(0,0));
        tableRoom.setSelectionBackground(new Color(204,229,255));
        tableRoom.getTableHeader().setBackground(new Color(245,245,245));
        tableRoom.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableRoom.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableRoom.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableRoom.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableRoom.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane scrollRoom = new JScrollPane(tableRoom);
        scrollRoom.setPreferredSize(new Dimension(450, 260));

        // Table container (2 tables side by side)
        JPanel tableContainer = new JPanel(new GridLayout(1, 2, 12, 0));
        tableContainer.setBackground(Color.WHITE);

        JPanel leftTablePanel = new JPanel(new BorderLayout());
        leftTablePanel.setBackground(Color.WHITE);
        JLabel lblFloorList = new JLabel("Danh Sách Tầng");
        lblFloorList.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        leftTablePanel.add(lblFloorList, BorderLayout.NORTH);
        leftTablePanel.add(scrollFloor, BorderLayout.CENTER);

        JPanel rightTablePanel = new JPanel(new BorderLayout());
        rightTablePanel.setBackground(Color.WHITE);
        JLabel lblRoomList = new JLabel("Danh Sách Phòng");
        lblRoomList.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        rightTablePanel.add(lblRoomList, BorderLayout.NORTH);
        rightTablePanel.add(scrollRoom, BorderLayout.CENTER);

        tableContainer.add(leftTablePanel);
        tableContainer.add(rightTablePanel);

        JPanel middlePanel = new JPanel(new BorderLayout()); middlePanel.setBackground(Color.WHITE);
        middlePanel.add(searchPanel, BorderLayout.NORTH);
        middlePanel.add(tableContainer, BorderLayout.CENTER);

        content.add(formPanel, BorderLayout.NORTH);
        content.add(middlePanel, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);

        add(mainPanel);
        addEventHandlers();
    }

    // Load data
    private void loadData() {
        tableFloorModel.setRowCount(0);
        List<Floor> list = floorDAO.getAll();
        for (Floor f : list) {
            tableFloorModel.addRow(new Object[]{
                f.getFloor_id(),
                f.getFloor_number(),
                f.getDescription()
            });
        }
    }

    private void loadRoomsForFloor(int floorId) {
        tableRoomModel.setRowCount(0);
        List<Room> allRooms = roomDAO.getAll();
        for (Room r : allRooms) {
            if (r.getFloorId() == floorId) {
                tableRoomModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomNumber(),
                    r.getTypeId(),
                    r.getStatus()
                });
            }
        }
    }

    // Event
    private void addEventHandlers() {

        tableFloor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = tableFloor.getSelectedRow();
                if (r == -1) return;

                selectedFloorId = (int) tableFloorModel.getValueAt(r, 0);
                txtFloorNumber.setText(tableFloorModel.getValueAt(r, 1).toString());
                txtDescription.setText(tableFloorModel.getValueAt(r, 2).toString());

                loadRoomsForFloor(selectedFloorId);
            }
        });

        btnReset.addActionListener(e -> {
            clearFields();
            loadData();
            tableRoomModel.setRowCount(0);
        });

        btnThem.addActionListener(e -> {
            if (!validateInput()) return;

            try {
                int floorNumber = Integer.parseInt(txtFloorNumber.getText().trim());

                // Kiểm tra xem số tầng đã tồn tại hay không
                if (floorDAO.isFloorNumberExists(floorNumber, -1)) {
                     JOptionPane.showMessageDialog(this, "Số tầng " + floorNumber + " đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                     return;
                }

                Floor floor = new Floor(0, floorNumber, txtDescription.getText().trim());

                if (floorDAO.insert(floor)) {
                     JOptionPane.showMessageDialog(this, "Thêm tầng thành công!");
                     loadData();
                     clearFields();
                     tableRoomModel.setRowCount(0);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tầng phải là số!");
            }
        });

        btnSua.addActionListener(e -> {
            if (selectedFloorId == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tầng cần sửa!");
                return;
            }

            if (floorDAO.hasRooms(selectedFloorId)) {
                JOptionPane.showMessageDialog(this, "Tầng này còn phòng! Không thể sửa số tầng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!validateInput()) return;

            try {
                int floorNumber = Integer.parseInt(txtFloorNumber.getText().trim());

                if (floorDAO.isFloorNumberExists(floorNumber, selectedFloorId)) {
                    JOptionPane.showMessageDialog(this, "Số tầng " + floorNumber + " đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Floor floor = new Floor(selectedFloorId, floorNumber, txtDescription.getText().trim());

                if (floorDAO.update(floor)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật tầng thành công!");
                    loadData();
                    clearFields();
                    tableRoomModel.setRowCount(0);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tầng phải là số!");
            }
        });

        btnXoa.addActionListener(e -> {
            if (selectedFloorId == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tầng cần xóa!");
                return;
            }

            if (floorDAO.hasRooms(selectedFloorId)) {
                JOptionPane.showMessageDialog(this, "Tầng này còn phòng! Không thể xóa tầng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tầng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (floorDAO.delete(selectedFloorId)) {
                JOptionPane.showMessageDialog(this, "Xóa tầng thành công!");
                loadData();
                clearFields();
                tableRoomModel.setRowCount(0);
                }
            }
        });

        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = txtTimKiem.getText().trim().toLowerCase();
                tableFloorModel.setRowCount(0);

                List<Floor> allFloors = floorDAO.getAll();
                for (Floor f : allFloors) {
                    String floorNumber = String.valueOf(f.getFloor_number());
                    String description = f.getDescription() != null ? f.getDescription().toLowerCase() : "";

                    if (floorNumber.contains(searchText) || description.contains(searchText)) {
                        tableFloorModel.addRow(new Object[]{
                            f.getFloor_id(),
                            f.getFloor_number(),
                            f.getDescription()
                        });
                    }
                }
            }
        });
    }

    // Check input
    private boolean validateInput() {
         if (txtFloorNumber.getText().trim().isEmpty()) {
              JOptionPane.showMessageDialog(this, "Vui lòng nhập số tầng!");
              return false;
         }
         return true;
    }

    private void clearFields() {
         txtFloorNumber.setText("");
         txtDescription.setText("");
         txtTimKiem.setText("");
         selectedFloorId = -1;
         tableFloor.clearSelection();
    }

    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
