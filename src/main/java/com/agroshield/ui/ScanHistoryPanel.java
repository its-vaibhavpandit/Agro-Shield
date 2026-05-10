package com.agroshield.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Comparator;
import java.time.format.DateTimeFormatter;
import com.agroshield.utils.UIConstants;
import com.agroshield.models.CropReport;
import com.agroshield.storage.FileStorageManager;
import com.agroshield.ui.components.CustomButton;

public class ScanHistoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ScanHistoryPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("📊 Crop Analysis History Archive");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.PRIMARY);
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Timestamp", "Crop Type", "Diagnosis", "Confidence (%)"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setBackground(UIConstants.SURFACE_DARK);
        table.setForeground(UIConstants.TEXT_LIGHT);
        table.setRowHeight(30);
        table.setFont(UIConstants.FONT_REGULAR);
        table.getTableHeader().setBackground(UIConstants.SURFACE_DARK.brighter());
        table.getTableHeader().setForeground(UIConstants.PRIMARY);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.SURFACE_DARK, 2));
        add(scroll, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setOpaque(false);

        CustomButton refreshBtn = new CustomButton("🔄 Refresh Logs");
        refreshBtn.setPreferredSize(new Dimension(200, 40));
        refreshBtn.addActionListener(e -> refreshData());
        
        toolbar.add(refreshBtn);
        add(toolbar, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);
        List<CropReport> reports = FileStorageManager.loadReports();
        // Sort them descending by default using the Comparable interface built into CropReport
        reports.sort(Comparator.naturalOrder()); 
        for (CropReport r : reports) {
            model.addRow(new Object[]{
                r.getId(),
                r.getTimestamp().format(formatter),
                r.getCropName(),
                r.getDisease(),
                String.format("%.2f", r.getConfidence())
            });
        }
    }
}
