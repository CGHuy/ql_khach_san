package com.ql_khach_san.ui.PhanCong;

import com.ql_khach_san.dao.WorkTimeDAO;
import com.ql_khach_san.dao.EmployeeDAO;
import com.ql_khach_san.model.WorkTime;
import com.ql_khach_san.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkTimeGUI extends JFrame {
    private JComboBox<Employee> cbEmployee;
    private JSpinner spinFromDate;
    private JSpinner spinToDate;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private WorkTimeDAO workTimeDAO = new WorkTimeDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnCheckIn, btnCheckOut;
    private JLabel lblStatus, lblTotalHours;

    public WorkTimeGUI() {
        setTitle("Quản lý giờ làm nhân viên");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        // Panel filter
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        // Panel table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Panel buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));

        // Nhân viên
        panel.add(new JLabel("Nhân viên:"));
        cbEmployee = new JComboBox<>();
        cbEmployee.addItem(new Employee()); // Item null - "Tất cả"
        for (Employee e : employeeDAO.getAll()) {
            cbEmployee.addItem(e);
        }
        cbEmployee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Employee) {
                    Employee emp = (Employee) value;
                    if (emp.getEmployeeId() == 0) {
                        value = "Tất cả";
                    } else {
                        value = emp.getFullName() != null ? emp.getFullName() : "Không xác định";
                    }
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        panel.add(cbEmployee);

        // Từ ngày
        panel.add(new JLabel("Từ ngày:"));
        spinFromDate = new JSpinner(new SpinnerDateModel(
                java.sql.Timestamp.valueOf(LocalDate.now().atStartOfDay()),
                null, null, java.util.Calendar.DAY_OF_MONTH));
        spinFromDate.setEditor(new JSpinner.DateEditor(spinFromDate, "dd/MM/yyyy"));
        spinFromDate.setPreferredSize(new Dimension(120, 25));
        panel.add(spinFromDate);

        // Đến ngày
        panel.add(new JLabel("Đến ngày:"));
        spinToDate = new JSpinner(new SpinnerDateModel(
                java.sql.Timestamp.valueOf(LocalDate.now().atStartOfDay()),
                null, null, java.util.Calendar.DAY_OF_MONTH));
        spinToDate.setEditor(new JSpinner.DateEditor(spinToDate, "dd/MM/yyyy"));
        spinToDate.setPreferredSize(new Dimension(120, 25));
        panel.add(spinToDate);

        // Nút lọc
        JButton btnFilter = new JButton("Lọc");
        btnFilter.addActionListener(e -> filterData());
        panel.add(btnFilter);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tạo table model
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nhân viên", "Ngày", "Giờ vào", "Giờ ra", "Giờ công", "Ghi chú"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        // Thêm sorter
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lblTotalHours = new JLabel("Tổng giờ: 0");
        lblStatus = new JLabel("Sẵn sàng");
        infoPanel.add(lblTotalHours);
        infoPanel.add(lblStatus);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Thao tác"));

        btnAdd = new JButton("Thêm");
        btnAdd.addActionListener(e -> addWorkTime());
        panel.add(btnAdd);

        btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(e -> editWorkTime());
        panel.add(btnEdit);

        btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteWorkTime());
        panel.add(btnDelete);

        btnCheckIn = new JButton("Check-in hôm nay");
        btnCheckIn.addActionListener(e -> checkIn());
        panel.add(btnCheckIn);

        btnCheckOut = new JButton("Check-out hôm nay");
        btnCheckOut.addActionListener(e -> checkOut());
        panel.add(btnCheckOut);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        panel.add(btnRefresh);

        return panel;
    }

    private void loadData() {
        filterData();
    }

    private void filterData() {
        tableModel.setRowCount(0);
        
        Employee selected = (Employee) cbEmployee.getSelectedItem();
        LocalDate fromDate = getDateFromSpinner(spinFromDate);
        LocalDate toDate = getDateFromSpinner(spinToDate);
        
        List<WorkTime> list;
        
        if (selected != null && selected.getEmployeeId() > 0) {
            list = workTimeDAO.getByEmployeeAndDateRange(selected.getEmployeeId(), fromDate, toDate);
        } else {
            list = workTimeDAO.getByDateRange(fromDate, toDate);
        }
        
        double totalHours = 0;
        for (WorkTime w : list) {
            tableModel.addRow(new Object[]{
                    w.getWorkId(),
                    getEmployeeName(w.getEmployeeId()),
                    w.getWorkDate(),
                    w.getTimeIn() != null ? w.getTimeIn().format(DateTimeFormatter.ofPattern("HH:mm")) : "",
                    w.getTimeOut() != null ? w.getTimeOut().format(DateTimeFormatter.ofPattern("HH:mm")) : "",
                    String.format("%.2f", w.getHoursWorked()),
                    w.getNote() != null ? w.getNote() : ""
            });
            totalHours += w.getHoursWorked();
        }
        
        lblTotalHours.setText(String.format("Tổng giờ: %.2f", totalHours));
        lblStatus.setText("Hiển thị " + tableModel.getRowCount() + " bản ghi");
    }

    private void addWorkTime() {
        WorkTimeDialog dialog = new WorkTimeDialog(this, null, employeeDAO);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            WorkTime w = dialog.getWorkTime();
            if (workTimeDAO.insert(w)) {
                JOptionPane.showMessageDialog(this, "Thêm giờ làm thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm giờ làm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editWorkTime() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi để sửa!");
            return;
        }
        
        int workId = (Integer) tableModel.getValueAt(selectedRow, 0);
        WorkTime w = workTimeDAO.getById(workId);
        
        WorkTimeDialog dialog = new WorkTimeDialog(this, w, employeeDAO);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            WorkTime updated = dialog.getWorkTime();
            if (workTimeDAO.update(updated)) {
                JOptionPane.showMessageDialog(this, "Cập nhật giờ làm thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật giờ làm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteWorkTime() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi để xóa!");
            return;
        }
        
        int workId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (workTimeDAO.delete(workId)) {
                JOptionPane.showMessageDialog(this, "Xóa giờ làm thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa giờ làm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkIn() {
        Employee selected = (Employee) cbEmployee.getSelectedItem();
        if (selected == null || selected.getEmployeeId() <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
            return;
        }
        
        LocalDate today = LocalDate.now();
        WorkTime existing = workTimeDAO.getByEmployeeAndDate(selected.getEmployeeId(), today);
        
        if (existing != null && existing.getTimeIn() != null) {
            JOptionPane.showMessageDialog(this, "Nhân viên này đã check-in hôm nay!");
            return;
        }
        
        WorkTime w = new WorkTime();
        w.setEmployeeId(selected.getEmployeeId());
        w.setWorkDate(today);
        w.setTimeIn(LocalTime.now());
        
        if (workTimeDAO.insert(w)) {
            JOptionPane.showMessageDialog(this, "Check-in thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Check-in thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkOut() {
        Employee selected = (Employee) cbEmployee.getSelectedItem();
        if (selected == null || selected.getEmployeeId() <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
            return;
        }
        
        LocalDate today = LocalDate.now();
        WorkTime existing = workTimeDAO.getByEmployeeAndDate(selected.getEmployeeId(), today);
        
        if (existing == null || existing.getTimeIn() == null) {
            JOptionPane.showMessageDialog(this, "Nhân viên này chưa check-in hôm nay!");
            return;
        }
        
        if (existing.getTimeOut() != null) {
            JOptionPane.showMessageDialog(this, "Nhân viên này đã check-out hôm nay!");
            return;
        }
        
        existing.setTimeOut(LocalTime.now());
        if (workTimeDAO.update(existing)) {
            JOptionPane.showMessageDialog(this, "Check-out thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Check-out thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate getDateFromSpinner(JSpinner spinner) {
        java.util.Date date = (java.util.Date) spinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String getEmployeeName(int employeeId) {
        Employee e = employeeDAO.getById(employeeId);
        return e != null ? e.getFullName() : "Không xác định";
    }
}
