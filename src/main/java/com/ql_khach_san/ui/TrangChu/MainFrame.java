package com.ql_khach_san.ui.TrangChu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.ql_khach_san.ui.TrangChu.MainService;
import com.ql_khach_san.ui.TrangChu.RoomView;
import java.util.Map;

public class MainFrame extends JFrame {
    private JPanel leftPanel;
    private JPanel contentPanel;
    private JPanel roomPanel;
    
    // Room status colors
    private static final Color COLOR_RESERVED = new Color(255, 165, 0);       // Đã đặt
    private static final Color COLOR_AVAILABLE = new Color(0, 200, 0);       // Phòng Trống
    private static final Color COLOR_OCCUPIED = new Color(255, 0, 0);        // Đã có người ở
    private static final Color COLOR_CLEANING = new Color(0, 100, 255);      // Phòng Đang Dọn Dẹp

    public MainFrame() {
        setTitle("Quản Lý Khách Sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 235, 220));
        
        // Top panel with logo
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Left menu panel
        leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 235, 220));
        
        // Title for Floor 1
        JLabel floorLabel = new JLabel("DANH SÁCH CÁC PHÒNG THEO TẦNG");
        floorLabel.setFont(new Font("Arial", Font.BOLD, 25));
        floorLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        floorLabel.setBackground(new Color(220, 150, 150));
        floorLabel.setOpaque(true);
        floorLabel.setForeground(Color.WHITE);
        floorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(floorLabel, BorderLayout.NORTH);
        
        // Room grid panel (wrap layout)
        roomPanel = createRoomPanel();
        JScrollPane scrollPane = new JScrollPane(roomPanel);
        scrollPane.setBackground(new Color(245, 235, 220));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(245, 235, 220));
        topPanel.setPreferredSize(new Dimension(0, 80));
        topPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel logoLabel = new JLabel("Logo Nè");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(new Color(80, 80, 80));
        
        topPanel.add(logoLabel);
        return topPanel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(220, 200, 200));
        panel.setPreferredSize(new Dimension(150, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] menuItems = {
            "🏠 Phòng",
            "📊 Tầng",
            "📝 Loại Phòng",
            "🛎️ Dịch Vụ",
            "📋 Loại Dịch Vụ",
            "👥 Khách Hàng",
            "👔 Nhân Viên",
            "📈 Thống Kê",
            "⏰ Thời Gian",
            "🔐 Đổi Mật Khẩu",
            "🚪 Đăng Xuất"
        };

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(130, 40));
            button.setFont(new Font("Arial", Font.PLAIN, 12));
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

            // Thêm sự kiện cho nút Thống Kê
            if (item.equals("📈 Thống Kê")) {
                button.addActionListener(e -> {
                    try {
                        JFrame frame = new JFrame("Thống Kê Khách Sạn");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.setSize(900, 600);
                        frame.setLocationRelativeTo(null);
                        frame.setLayout(new BorderLayout());
                        frame.add(new com.ql_khach_san.ui.ThongKe.StatisticPanel(), BorderLayout.CENTER);
                        frame.setVisible(true);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Không thể mở cửa sổ Thống Kê:\n" + t.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }

            panel.add(button);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }
    
    private JPanel createRoomPanel() {
        MainService service = new MainService();

        // Parent panel stacks floors vertically
        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        parent.setBorder(new EmptyBorder(10, 10, 10, 10));
        parent.setBackground(new Color(245, 235, 220));

        Map<String, java.util.List<RoomView>> floorMap = service.getRoomsGroupedByFloor();

        java.util.List<String> floors = new java.util.ArrayList<>(floorMap.keySet());
        floors.sort((a, b) -> {
            try { return Integer.compare(Integer.parseInt(a), Integer.parseInt(b)); }
            catch (NumberFormatException e) { return a.compareTo(b); }
        });

        for (String f : floors) {
            JLabel floorLabel = new JLabel("Tầng " + f);
            floorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            floorLabel.setOpaque(true);
            floorLabel.setBackground(new Color(220, 150, 150));
            floorLabel.setForeground(Color.WHITE);
            floorLabel.setBorder(new EmptyBorder(6, 10, 6, 10));
            floorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            parent.add(floorLabel);

            JPanel floorPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
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
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        card.add(nameLabel);

        JLabel typeLabel = new JLabel("(" + (v.getTypeName() != null ? v.getTypeName() : "") + ")");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(typeLabel);

        JLabel statusLabel = new JLabel(v.getStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
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
                JOptionPane.showMessageDialog(null, "Bạn chọn: " + v.getRoomNumber());
            }
        });

        return card;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
