package com.ql_khach_san.ui.TrangChu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.ql_khach_san.ui.TrangChu.MainService;
import com.ql_khach_san.ui.TrangChu.RoomView;
import com.ql_khach_san.ui.PhanCong.WorkTimeGUI;
import java.util.Map;

public class MainFrame extends JFrame {
    private String userRole;
    private JPanel leftPanel;
    private JPanel contentPanel;
    private JPanel roomPanel;
    private JScrollPane scrollPane;
    
    // Khởi tạo Service để dùng chung
    private RoomService roomService = new RoomService();
    private MainService mainService = new MainService();
    
    // Room status colors
    private static final Color COLOR_RESERVED = new Color(255, 165, 0);       // Đã đặt
    private static final Color COLOR_AVAILABLE = new Color(0, 200, 0);       // Phòng Trống
    private static final Color COLOR_OCCUPIED = new Color(200, 40, 40);        // Đã có người ở
    private static final Color COLOR_CLEANING = new Color(0, 100, 255);      // Phòng Đang Dọn Dẹp

    public MainFrame() {
        this(null);
    }

    public MainFrame(String userRole) {
        this.userRole = userRole;
        setTitle("Quản Lý Khách Sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 235, 220));
        
        // Left menu panel
        leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 235, 220));
        
        // Title
        JLabel floorLabel = new JLabel("DANH SÁCH CÁC PHÒNG THEO TẦNG");
        floorLabel.setFont(new Font("Arial", Font.BOLD, 25));
        floorLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        floorLabel.setBackground(new Color(220, 150, 150));
        floorLabel.setOpaque(true);
        floorLabel.setForeground(Color.WHITE);
        floorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(floorLabel, BorderLayout.NORTH);
        
        // Khởi tạo khu vực chứa phòng
        roomPanel = createRoomPanel();
        scrollPane = new JScrollPane(roomPanel);
        scrollPane.setBackground(new Color(245, 235, 220));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        refreshRoomPanel();
    }
    
    public void refreshRoomPanel() {
        JPanel container = createRoomPanel();
        scrollPane.setViewportView(container);
        scrollPane.revalidate();
        scrollPane.repaint();
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(220, 200, 200));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(30, 10, 10, 10));
        
        String[] menuItems = {"Phòng", "Loại Phòng", "Dịch Vụ", "Khách Hàng", "Nhân Viên", "Thống Kê", "Thời Gian", "Đổi Mật Khẩu", "Đăng Xuất"};
        for (String item : menuItems) {
            // Nếu là nhân viên thì ẩn mục "Nhân Viên"
            if ("Nhân Viên".equals(item) && userRole != null && userRole.equalsIgnoreCase("Nhân viên")) {
                continue;
            }
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 50));
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(new Color(200, 180, 180));
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createLineBorder(new Color(150, 120, 120), 1));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(180, 160, 160));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(200, 180, 180));
                }
            });

            // Thêm action listener cho nút "Thời Gian"
            if (item.equals("Thời Gian")) {
                button.addActionListener(e -> new WorkTimeGUI());
            }
            // Thêm action listener cho nút "Dịch Vụ"
            if (item.equals("Dịch Vụ")) {
                button.addActionListener(e -> {
                    JFrame f = new JFrame("Quản lý Dịch vụ");
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setContentPane(new com.ql_khach_san.ui.Dichvu.ServicePanel());
                    f.setSize(900, 500);
                    f.setLocationRelativeTo(MainFrame.this);
                    f.setVisible(true);
                });
            }

            panel.add(button);
            panel.add(Box.createVerticalStrut(12));
        }
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }
    
    private JPanel createRoomPanel() {
        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        parent.setBorder(new EmptyBorder(10, 10, 10, 10));
        parent.setBackground(new Color(245, 235, 220));

        Map<String, java.util.List<RoomView>> floorMap = mainService.getRoomsGroupedByFloor();
        
        java.util.List<String> floors = new java.util.ArrayList<>(floorMap.keySet());
        floors.sort((a, b) -> {
            try { return Integer.compare(Integer.parseInt(a), Integer.parseInt(b)); }
            catch (NumberFormatException e) { return a.compareTo(b); }
        });

        for (String f : floors) {
            JLabel floorLabel = new JLabel("Tầng " + f);
            floorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            floorLabel.setOpaque(true);
            floorLabel.setBackground(new Color(220, 150, 150));
            floorLabel.setForeground(Color.WHITE);
            floorLabel.setBorder(new EmptyBorder(6, 10, 6, 10));
            floorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            parent.add(floorLabel);

            JPanel floorPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
            floorPanel.setBackground(new Color(245, 235, 220));
            floorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            floorPanel.setBorder(new EmptyBorder(8, 8, 16, 8));

            java.util.List<RoomView> list = floorMap.get(f);
            list.sort((v1, v2) -> v1.getRoomNumber().compareTo(v2.getRoomNumber()));

            for (RoomView v : list) {
                JPanel roomCard = createRoomCardFromView(v);
                roomCard.setPreferredSize(new Dimension(160, 140));
                roomCard.setMaximumSize(new Dimension(160, 140));
                floorPanel.add(roomCard);
            }

            parent.add(floorPanel);
        }

        return parent;
    }

    private JPanel createRoomCardFromView(RoomView v) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        Color bg = v.getColor() != null ? v.getColor() : COLOR_AVAILABLE;
        card.setBackground(bg);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(v.getRoomNumber());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        card.add(nameLabel);

        JLabel typeLabel = new JLabel("(" + (v.getTypeName() != null ? v.getTypeName() : "") + ")");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(typeLabel);

        JLabel statusLabel = new JLabel(v.getStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        card.add(statusLabel);

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
                JPopupMenu actionMenu = createRoomMenu(v);
                actionMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        });

        return card;
    }
    
    private JPopupMenu createRoomMenu(RoomView v) {
        JPopupMenu menu = new JPopupMenu();
        String status = v.getStatus();
        
        if (status.equalsIgnoreCase("Trống")) {
            
            // Đặt phòng
            JMenuItem itemDatPhong = new JMenuItem("Đặt phòng");
            itemDatPhong.addActionListener(e -> {
                new DialogDatPhong(this, v).setVisible(true);
            });
            menu.add(itemDatPhong);
            
        } else if (status.equalsIgnoreCase("Đã đặt")) {
            
            // Nhận phòng
            JMenuItem itemNhanPhong = new JMenuItem("Nhận phòng");
            itemNhanPhong.addActionListener(e -> { 
                new DialogNhanPhong(this, v.getRoomNumber()).setVisible(true);
            });
            menu.add(itemNhanPhong);
            
            // Hủy đặt
            JMenuItem itemHuyDat = new JMenuItem("Hủy đặt");
            itemHuyDat.addActionListener(e -> { 
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Bạn có chắc chắn muốn HỦY ĐẶT phòng " + v.getRoomNumber() + " không?", 
                    "Cảnh báo hủy đặt", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    roomService.cancelReservation(v.getRoomId(), v.getReservationId());
                    JOptionPane.showMessageDialog(this, "Đã hủy đặt phòng " + v.getRoomNumber());
                    refreshRoomPanel();
                }
            });
            menu.add(itemHuyDat);
            
        } else if (status.equalsIgnoreCase("Đã thuê")) {
            
            // Chi tiết phòng
            JMenuItem itemChiTiet = new JMenuItem("Chi tiết");
            menu.add(itemChiTiet);
            
        } else if (status.equalsIgnoreCase("Đang dọn")) {
            
            // Dọn phòng
            JMenuItem itemDonDep = new JMenuItem("Cập nhật dọn dẹp");
            itemDonDep.addActionListener(e -> { 
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Phòng " + v.getRoomNumber() + " đã được dọn dẹp xong?", 
                    "Xác nhận trạng thái phòng", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    roomService.checkClean(v.getRoomId());
                    JOptionPane.showMessageDialog(this, "Phòng " + v.getRoomNumber() + " đã được dọn dẹp");
                    refreshRoomPanel();
                }
            });
            menu.add(itemDonDep);
        }
 
        return menu;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
