package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.CustomButton;

public class DashboardPanel extends JPanel {
    private JPanel contentPanel;
    private CardLayout contentLayout;

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_DARK);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.SURFACE_DARK);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🌱 AgroShield AI");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.PRIMARY);
        sidebar.add(title);
        
        JLabel dateTimeLbl = new JLabel();
        dateTimeLbl.setFont(UIConstants.FONT_REGULAR);
        dateTimeLbl.setForeground(UIConstants.TEXT_LIGHT);
        sidebar.add(dateTimeLbl);
        
        Timer dtTimer = new Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateTimeLbl.setText(dtf.format(now));
        });
        dtTimer.start();
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menus = {"Home", "Disease Scanner", "Soil Analysis", "Weather", "Chatbot", "Scan History"};
        
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(UIConstants.BG_DARK);

        ScanHistoryPanel historyPanel = new ScanHistoryPanel();

        // Add sub-panels
        contentPanel.add(createHomePanel(), "Home");
        contentPanel.add(new DiseaseScannerPanel(), "Disease Scanner");
        contentPanel.add(new SoilAnalysisPanel(), "Soil Analysis");
        contentPanel.add(new WeatherPanel(), "Weather");
        contentPanel.add(new ChatbotPanel(), "Chatbot");
        contentPanel.add(historyPanel, "Scan History");

        for (String m : menus) {
            JButton btn = new JButton(m);
            btn.setForeground(UIConstants.TEXT_LIGHT);
            btn.setBackground(UIConstants.SURFACE_DARK);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(UIConstants.FONT_REGULAR);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if ("Scan History".equals(m)) {
                    historyPanel.refreshData();
                }
                contentLayout.show(contentPanel, m);
            });
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());
        CustomButton logoutBtn = new CustomButton("Logout");
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.addActionListener(e -> frame.logout());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.BG_DARK);
        JLabel lbl = new JLabel("Welcome to AgroShield Dashboard!");
        lbl.setFont(UIConstants.FONT_LARGE);
        lbl.setForeground(UIConstants.TEXT_LIGHT);
        p.add(lbl);
        return p;
    }
}