package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.services.AuthService;
import com.agroshield.ui.components.*;
import com.agroshield.utils.UIConstants;

public class RegisterPanel extends JPanel {
    public RegisterPanel(MainFrame frame, AuthService auth) {
        setBackground(UIConstants.BG_DARK);
        setLayout(new GridBagLayout());

        RoundedPanel box = new RoundedPanel();
        box.setPreferredSize(new Dimension(400, 400));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Register");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setMaximumSize(new Dimension(300, 40));
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(300, 40));

        CustomButton regBtn = new CustomButton("Register");
        regBtn.setMaximumSize(new Dimension(300, 40));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setForeground(UIConstants.PRIMARY);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        regBtn.addActionListener(e -> {
            try {
                auth.register(userField.getText(), new String(passField.getPassword()));
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                frame.showPanel("LOGIN");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> frame.showPanel("LOGIN"));

        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(new JLabel("Username")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(userField);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(new JLabel("Password")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(passField);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(regBtn);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(backBtn);

        add(box);
    }
}