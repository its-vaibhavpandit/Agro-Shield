package com.agroshield.ui.components;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;

public class RoundedPanel extends JPanel {
    public RoundedPanel() {
        setOpaque(false);
        setBackground(UIConstants.SURFACE_DARK);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g);
        g2.dispose();
    }
}