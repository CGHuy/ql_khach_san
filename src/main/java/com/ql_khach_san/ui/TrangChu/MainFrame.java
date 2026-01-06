package com.ql_khach_san.ui.TrangChu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    // Constants
    private static final Color BG_MAIN = new Color(255, 255, 255);
    private static final Color BG_LEFT_PANEL = new Color(33, 150, 243);
    private static final Color BG_BUTTON = new Color(255, 255, 255);
    private static final Color BG_BUTTON_HOVER = new Color(180, 160, 160);
    private static final Color BG_TITLE = new Color(33, 150, 243);
    private static final Color BORDER_BUTTON = new Color(150, 120, 120);
    
    private static final Color COLOR_RESERVED = new Color(255, 165, 0);
    private static final Color COLOR_AVAILABLE = new Color(0, 200, 0);
    private static final Color COLOR_OCCUPIED = new Color(200, 40, 40);
    private static final Color COLOR_CLEANING = new Color(0, 100, 255);
    
    private static final Dimension BUTTON_SIZE = new Dimension(180, 50);
    private static final Dimension CARD_SIZE = new Dimension(160, 140);
    
    private static final String[] MENU_ITEMS = {
        "Loại Phòng", "Tầng", "Phòng", "Dịch Vụ", "Khách Hàng", 
        "Nhân Viên", "Thống Kê", "Thời Gian", "Đổi Mật Khẩu", "Đăng Xuất"
    };
    
    // Services
    private final RoomService roomService = new RoomService();
    private final MainService mainService = new MainService();
    
    // UI Components
    private final String userRole;
    private final int employeeId;
    private JScrollPane scrollPane;

    public MainFrame() {
        this(null, -1);
    }

    public MainFrame(String userRole) {
        this(userRole, -1);
    }

    public MainFrame(String userRole, int employeeId) {
        this.userRole = userRole;
        this.employeeId = employeeId;
        initFrame();
        initComponents();
        refreshRoomPanel();
    }
    
    private void initFrame() {
        setTitle("Quản Lý Khách Sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_MAIN);
        
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_LEFT_PANEL);
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(30, 10, 10, 10));
        
        for (String item : MENU_ITEMS) {
            if (shouldHideMenuItem(item)) continue;
            
            JButton button = createMenuButton(item);
            addMenuAction(button, item);
            
            panel.add(button);
            panel.add(Box.createVerticalStrut(12));
        }
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }
    
    private boolean shouldHideMenuItem(String item) {
        return "Nhân Viên".equals(item) && 
               userRole != null && 
               userRole.equalsIgnoreCase("Nhân viên");
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(BUTTON_SIZE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(BG_BUTTON);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(BORDER_BUTTON, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_BUTTON);
            }
        });
        
        return button;
    }
    
    private void addMenuAction(JButton button, String item) {
        switch (item) {
            case "Phòng":
                button.addActionListener(e -> openWindow(
                    new com.ql_khach_san.ui.Room.GUI()
                ));
                break;
            case "Loại Phòng":
                button.addActionListener(e -> openWindow(
                    new com.ql_khach_san.ui.RoomType.GUI()
                ));
                break;
            case "Tầng":
                button.addActionListener(e -> openWindow(
                    new com.ql_khach_san.ui.Tang.GUI()
                ));
                break;
            case "Khách Hàng":
                button.addActionListener(e -> openWindow(
                    new com.ql_khach_san.ui.KhachHang.GUI()
                ));
                break;
            case "Nhân Viên":
                button.addActionListener(e -> openWindow(
                    new com.ql_khach_san.ui.NhanVien.NhanVienGUI()
                ));
                break;
            case "Thống Kê":
                button.addActionListener(e -> openStatisticPanel());
                break;
            case "Thời Gian":
                button.addActionListener(e -> new com.ql_khach_san.ui.PhanCong.WorkTimeGUI());
                break;
            case "Dịch Vụ":
                button.addActionListener(e -> openServicePanel());
                break;
        }
    }
    
    private void openWindow(JFrame frame) {
        frame.setVisible(true);
    }
    
    private void openStatisticPanel() {
        JFrame frame = new JFrame("Thống kê");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new com.ql_khach_san.ui.ThongKe.StatisticPanel());
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }
    
    private void openServicePanel() {
        JFrame frame = new JFrame("Quản lý Dịch vụ");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new com.ql_khach_san.ui.Dichvu.ServicePanel());
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_MAIN);
        
        contentPanel.add(createTitleLabel(), BorderLayout.NORTH);
        
        scrollPane = new JScrollPane(createRoomPanel());
        scrollPane.setBackground(BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JLabel createTitleLabel() {
        JLabel label = new JLabel("DANH SÁCH CÁC PHÒNG THEO TẦNG");
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setBorder(new EmptyBorder(10, 15, 10, 15));
        label.setBackground(BG_TITLE);
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    public void refreshRoomPanel() {
        scrollPane.setViewportView(createRoomPanel());
        scrollPane.revalidate();
        scrollPane.repaint();
    }
    
    private JPanel createRoomPanel() {
        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        parent.setBorder(new EmptyBorder(10, 10, 10, 10));
        parent.setBackground(BG_MAIN);

        Map<String, List<RoomView>> floorMap = mainService.getRoomsGroupedByFloor();
        List<String> floors = getSortedFloors(floorMap);

        for (String floor : floors) {
            parent.add(createFloorLabel(floor));
            parent.add(createFloorPanel(floorMap.get(floor)));
        }

        return parent;
    }
    
    private List<String> getSortedFloors(Map<String, List<RoomView>> floorMap) {
        List<String> floors = new java.util.ArrayList<>(floorMap.keySet());
        floors.sort((a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
            } catch (NumberFormatException e) {
                return a.compareTo(b);
            }
        });
        return floors;
    }
    
    private JLabel createFloorLabel(String floor) {
        JLabel label = new JLabel("Tầng " + floor);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(BG_TITLE);
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(6, 10, 6, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createFloorPanel(List<RoomView> rooms) {
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(BG_MAIN);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(8, 8, 16, 8));

        rooms.sort((v1, v2) -> v1.getRoomNumber().compareTo(v2.getRoomNumber()));

        for (RoomView room : rooms) {
            JPanel card = createRoomCard(room);
            panel.add(card);
        }

        return panel;
    }

    private JPanel createRoomCard(RoomView room) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setBackground(room.getColor() != null ? room.getColor() : COLOR_AVAILABLE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(CARD_SIZE);
        card.setMaximumSize(CARD_SIZE);

        card.add(createCardLabel(room.getRoomNumber(), Font.BOLD, 16));
        card.add(createCardLabel("(" + (room.getTypeName() != null ? room.getTypeName() : "") + ")", Font.PLAIN, 12));
        card.add(createCardLabel(room.getStatus(), Font.PLAIN, 12));

        addCardMouseListener(card, room);

        return card;
    }
    
    private JLabel createCardLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        return label;
    }
    
    private void addCardMouseListener(JPanel card, RoomView room) {
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                originalColor = card.getBackground();
                card.setBackground(originalColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(originalColor);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JPopupMenu menu = createRoomMenu(room);
                menu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        });
    }
    
    private JPopupMenu createRoomMenu(RoomView room) {
        JPopupMenu menu = new JPopupMenu();
        String status = room.getStatus();
        
        if (status.equalsIgnoreCase("Trống")) {
            menu.add(createBookRoomItem(room));
        } else if (status.equalsIgnoreCase("Đã đặt")) {
            menu.add(createCheckInItem(room));
            menu.add(createCancelReservationItem(room));
        } else if (status.equalsIgnoreCase("Đã thuê")) {
            menu.add(createDetailItem(room));
        } else if (status.equalsIgnoreCase("Đang dọn")) {
            menu.add(createCleaningDoneItem(room));
        }
 
        return menu;
    }
    
    private JMenuItem createBookRoomItem(RoomView room) {
        JMenuItem item = new JMenuItem("Đặt phòng");
        item.addActionListener(e -> new DialogDatPhong(this, room).setVisible(true));
        return item;
    }
    
    private JMenuItem createCheckInItem(RoomView room) {
        JMenuItem item = new JMenuItem("Nhận phòng");
        item.addActionListener(e -> {
            if (confirmAction("Khách đã tới nhận phòng " + room.getRoomNumber(), "Thông báo nhận phòng")) {
                roomService.checkInRoom(room.getReservationId());
                showMessage("Đã nhận phòng " + room.getRoomNumber() + " thành công!");
                refreshRoomPanel();
            }
        });
        return item;
    }
    
    private JMenuItem createCancelReservationItem(RoomView room) {
        JMenuItem item = new JMenuItem("Hủy đặt");
        item.addActionListener(e -> {
            if (confirmAction("Bạn có chắc chắn muốn HỦY ĐẶT phòng " + room.getRoomNumber() + " không?", "Cảnh báo hủy đặt")) {
                roomService.cancelReservation(room.getReservationId());
                showMessage("Đã hủy đặt phòng " + room.getRoomNumber());
                refreshRoomPanel();
            }
        });
        return item;
    }
    
    private JMenuItem createDetailItem(RoomView room) {
        JMenuItem item = new JMenuItem("Chi tiết");
        item.addActionListener(e -> new DialogChiTiet(this, room, employeeId).setVisible(true));
        return item;
    }
    
    private JMenuItem createCleaningDoneItem(RoomView room) {
        JMenuItem item = new JMenuItem("Cập nhật dọn dẹp");
        item.addActionListener(e -> {
            if (confirmAction("Phòng " + room.getRoomNumber() + " đã được dọn dẹp xong?", "Xác nhận trạng thái phòng")) {
                roomService.checkClean(room.getRoomId());
                showMessage("Phòng " + room.getRoomNumber() + " đã được dọn dẹp");
                refreshRoomPanel();
            }
        });
        return item;
    }
    
    private boolean confirmAction(String message, String title) {
        return JOptionPane.showConfirmDialog(
            this, 
            message, 
            title, 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}