package com.ql_khach_san.ui.PhanCong;

import com.ql_khach_san.dao.EmployeeDAO;
import com.ql_khach_san.model.WorkTime;
import com.ql_khach_san.model.Employee;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class WorkTimeDialog extends JDialog {
    private JComboBox<Employee> cbEmployee;
    private JSpinner spinDate;
    private JSpinner spinTimeIn;
    private JSpinner spinTimeOut;
    private JTextArea taNote;
    
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private WorkTime workTime;

    public WorkTimeDialog(JFrame parent, WorkTime w, EmployeeDAO employeeDAO) {
        super(parent, w == null ? "Thêm giờ làm" : "Sửa giờ làm", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.workTime = w != null ? w : new WorkTime();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Nhân viên
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        cbEmployee = new JComboBox<>();
        cbEmployee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Employee) {
                    Employee emp = (Employee) value;
                    value = emp.getFullName() != null ? emp.getFullName() : "Không xác định";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        List<Employee> employees = employeeDAO.getAll();
        for (Employee e : employees) {
            cbEmployee.addItem(e);
            if (w != null && e.getEmployeeId() == w.getEmployeeId()) {
                cbEmployee.setSelectedItem(e);
            }
        }
        panel.add(cbEmployee, gbc);

        // Ngày
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Ngày:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        LocalDate date = w != null ? w.getWorkDate() : LocalDate.now();
        spinDate = new JSpinner(new SpinnerDateModel(
                java.sql.Timestamp.valueOf(date.atStartOfDay()),
                null, null, java.util.Calendar.DAY_OF_MONTH));
        spinDate.setEditor(new JSpinner.DateEditor(spinDate, "dd/MM/yyyy"));
        panel.add(spinDate, gbc);

        // Giờ vào
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Giờ vào:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        LocalTime timeIn = w != null && w.getTimeIn() != null ? w.getTimeIn() : LocalTime.of(8, 0);
        spinTimeIn = new JSpinner(new SpinnerDateModel(
                java.sql.Timestamp.valueOf(LocalDate.now().atTime(timeIn)),
                null, null, java.util.Calendar.HOUR_OF_DAY));
        spinTimeIn.setEditor(new JSpinner.DateEditor(spinTimeIn, "HH:mm"));
        panel.add(spinTimeIn, gbc);

        // Giờ ra
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Giờ ra:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        LocalTime timeOut = w != null && w.getTimeOut() != null ? w.getTimeOut() : LocalTime.of(17, 0);
        spinTimeOut = new JSpinner(new SpinnerDateModel(
                java.sql.Timestamp.valueOf(LocalDate.now().atTime(timeOut)),
                null, null, java.util.Calendar.HOUR_OF_DAY));
        spinTimeOut.setEditor(new JSpinner.DateEditor(spinTimeOut, "HH:mm"));
        panel.add(spinTimeOut, gbc);

        // Ghi chú
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        taNote = new JTextArea(3, 20);
        taNote.setLineWrap(true);
        taNote.setWrapStyleWord(true);
        if (w != null && w.getNote() != null) {
            taNote.setText(w.getNote());
        }
        panel.add(new JScrollPane(taNote), gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> save());
        btnPanel.add(btnSave);
        
        btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);
        
        panel.add(btnPanel, gbc);

        add(panel);
        setVisible(true);
    }

    private void save() {
        Employee selected = (Employee) cbEmployee.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate date = getDateFromSpinner(spinDate);
        LocalTime timeIn = getTimeFromSpinner(spinTimeIn);
        LocalTime timeOut = getTimeFromSpinner(spinTimeOut);

        if (timeOut.isBefore(timeIn)) {
            JOptionPane.showMessageDialog(this, "Giờ ra không thể trước giờ vào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        workTime.setEmployeeId(selected.getEmployeeId());
        workTime.setWorkDate(date);
        workTime.setTimeIn(timeIn);
        workTime.setTimeOut(timeOut);
        workTime.setNote(taNote.getText().trim().isEmpty() ? null : taNote.getText());

        confirmed = true;
        dispose();
    }

    private LocalDate getDateFromSpinner(JSpinner spinner) {
        java.util.Date date = (java.util.Date) spinner.getValue();
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private LocalTime getTimeFromSpinner(JSpinner spinner) {
        java.util.Date date = (java.util.Date) spinner.getValue();
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }
}
