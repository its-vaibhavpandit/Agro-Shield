package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.services.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.admin.AdminPanel;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AuthService authService;

    public MainFrame() {
        authService = new AuthService();
        setTitle("AgroShield AI - Smart Crop & Farm Management");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIConstants.BG_DARK);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UIConstants.BG_DARK);

        mainPanel.add(new LoginPanel(this, authService), "LOGIN");
        mainPanel.add(new RegisterPanel(this, authService), "REGISTER");

        add(mainPanel);
    }

    public void showPanel(String name) { cardLayout.show(mainPanel, name); }

    public void navigateToDashboard() {
        if (authService.getCurrentUser() != null) {
            if(authService.getCurrentUser().getRole() == com.agroshield.models.Role.ADMIN) {
                mainPanel.add(new AdminPanel(this, authService), "ADMIN");
                showPanel("ADMIN");
            } else {
                mainPanel.add(new DashboardPanel(this), "DASHBOARD");
                showPanel("DASHBOARD");
            }
        }
    }

    public void logout() {
        authService.logout();
        showPanel("LOGIN");
    }
}