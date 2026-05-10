package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.services.AuthService;
import com.agroshield.ui.components.*;
import com.agroshield.utils.UIConstants;

public class LoginPanel extends JPanel {
    public LoginPanel(MainFrame frame, AuthService auth) {
        setBackground(UIConstants.BG_DARK);
        setLayout(new GridBagLayout());

        RoundedPanel box = new RoundedPanel();
        box.setPreferredSize(new Dimension(400, 400));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setMaximumSize(new Dimension(300, 40));
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(300, 40));

        CustomButton loginBtn = new CustomButton("Login");
        loginBtn.setMaximumSize(new Dimension(300, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton regBtn = new JButton("Create new account");
        regBtn.setForeground(UIConstants.PRIMARY);
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            try {
                auth.login(userField.getText(), new String(passField.getPassword()));
                frame.navigateToDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        regBtn.addActionListener(e -> frame.showPanel("REGISTER"));

        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(new JLabel("Username")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(userField);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(new JLabel("Password")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(passField);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(loginBtn);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(regBtn);

        add(box);
    }
}