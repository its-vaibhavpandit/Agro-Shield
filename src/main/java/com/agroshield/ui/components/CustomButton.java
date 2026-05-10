package com.agroshield.ui.components;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.agroshield.utils.UIConstants;

public class CustomButton extends JButton {
    private boolean isHover = false;
    private Color hoverColor;

    public CustomButton(String text) {
        super(text);
        setFont(UIConstants.FONT_TITLE);
        setForeground(UIConstants.TEXT_LIGHT);
        setBackground(UIConstants.PRIMARY);
        hoverColor = UIConstants.PRIMARY.darker();
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHover ? hoverColor : getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
        g2.dispose();
    }
}