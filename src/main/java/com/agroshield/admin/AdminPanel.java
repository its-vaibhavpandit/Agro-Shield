package com.agroshield.admin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import com.agroshield.utils.UIConstants;
import com.agroshield.services.AuthService;
import com.agroshield.models.User;
import com.agroshield.ui.components.CustomButton;
import com.agroshield.ui.MainFrame;

public class AdminPanel extends JPanel {
    public AdminPanel(MainFrame frame, AuthService auth) {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Admin Dashboard - User Management");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.PRIMARY);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Username", "Role"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(UIConstants.SURFACE_DARK);
        table.setForeground(UIConstants.TEXT_LIGHT);
        table.setFillsViewportHeight(true);

        Map<String, User> users = auth.getAllUsers();
        for (User u : users.values()) {
            model.addRow(new Object[]{u.getUsername(), u.getRole().name()});
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        CustomButton delBtn = new CustomButton("Delete User");
        CustomButton logoutBtn = new CustomButton("Logout");

        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String username = (String) model.getValueAt(row, 0);
                if(username.equals("admin")) {
                    JOptionPane.showMessageDialog(this, "Cannot delete admin!");
                    return;
                }
                auth.deleteUser(username);
                model.removeRow(row);
            }
        });

        logoutBtn.addActionListener(e -> frame.logout());

        bottom.add(delBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}