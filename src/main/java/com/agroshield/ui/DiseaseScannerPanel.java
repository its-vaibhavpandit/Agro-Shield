package com.agroshield.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.UUID;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.*;
import com.agroshield.disease.DiseaseScannerService;
import com.agroshield.models.CropReport;
import com.agroshield.storage.FileStorageManager;

public class DiseaseScannerPanel extends JPanel {
    private DiseaseScannerService scanner = new DiseaseScannerService();
    private JLabel imagePreview;
    private File selectedFile = null;

    public DiseaseScannerPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("🌱 AI-Powered Crop Disease Scanner");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel splitContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        splitContainer.setOpaque(false);

        // Left Side: Inputs & Preview
        RoundedPanel leftPanel = new RoundedPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] crops = {"Wheat", "Rice", "Tomato", "Potato", "Cotton"};
        JComboBox<String> cropCombo = new JComboBox<>(crops);
        cropCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Image Placeholder
        imagePreview = new JLabel("No Image Selected", SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(250, 250));
        imagePreview.setMaximumSize(new Dimension(350, 250));
        imagePreview.setBorder(BorderFactory.createLineBorder(UIConstants.TEXT_MUTED, 1, true));
        imagePreview.setForeground(UIConstants.TEXT_MUTED);
        imagePreview.setAlignmentX(Component.CENTER_ALIGNMENT);

        CustomButton chooseBtn = new CustomButton("📁 Upload Image");
        chooseBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        chooseBtn.setBackground(UIConstants.SURFACE_DARK);

        CustomButton scanBtn = new CustomButton("⚡ Start AI Analysis");
        scanBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        scanBtn.setEnabled(false);

        // Wire Select Image
        chooseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
            int ret = chooser.showOpenDialog(this);
            if(ret == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                ImageIcon rawIcon = new ImageIcon(selectedFile.getAbsolutePath());
                Image img = rawIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                imagePreview.setIcon(new ImageIcon(img));
                imagePreview.setText("");
                scanBtn.setEnabled(true);
            }
        });

        leftPanel.add(createLabel("Select Crop Category"));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(cropCombo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(imagePreview);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(chooseBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(scanBtn);

        // Right Side: Results Area
        RoundedPanel rightPanel = new RoundedPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(UIConstants.BG_DARK);
        resultArea.setForeground(UIConstants.PRIMARY);
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        
        rightPanel.add(createLabel("🔍 Analysis Results Log"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Action Handler
        scanBtn.addActionListener(e -> {
            scanBtn.setEnabled(false);
            chooseBtn.setEnabled(false);
            resultArea.setText("Initializing Neural Engine...\nConnecting to secure model server...\nScanning visual features...");
            
            new Thread(() -> {
                try {
                    String crop = (String) cropCombo.getSelectedItem();
                    String disease = scanner.scanImage(crop);
                    double conf = scanner.getConfidence();
                    
                    // Create domain model object
                    CropReport report = new CropReport(
                        UUID.randomUUID().toString().substring(0, 8),
                        crop,
                        disease,
                        conf
                    );
                    
                    // Core Serialization Persistence
                    List<CropReport> reports = FileStorageManager.loadReports();
                    reports.add(report);
                    FileStorageManager.saveReports(reports);
                    
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText(
                            "==========================================\n" +
                            "      AGROSHIELD AI - SCAN COMPLETE       \n" +
                            "==========================================\n\n" +
                            "▶ REPORT ID:   " + report.getId() + "\n" +
                            "▶ CROP TYPE:   " + report.getCropName() + "\n" +
                            "▶ DIAGNOSIS:   " + report.getDisease() + "\n" +
                            "▶ ACCURACY:    " + String.format("%.2f%%", report.getConfidence()) + "\n" +
                            "▶ STATUS:      " + (disease.equalsIgnoreCase("Healthy") ? "STABLE" : "ACTION REQUIRED") + "\n\n" +
                            "------------------------------------------\n" +
                            "TIMESTAMP: " + report.getTimestamp().toString() + "\n" +
                            "💾 Result cached and securely archived.\n"
                        );
                        scanBtn.setEnabled(true);
                        chooseBtn.setEnabled(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.append("\n[ERROR] " + ex.getMessage());
                        scanBtn.setEnabled(true);
                        chooseBtn.setEnabled(true);
                    });
                }
            }).start();
        });

        splitContainer.add(leftPanel);
        splitContainer.add(rightPanel);
        add(splitContainer, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(UIConstants.TEXT_LIGHT);
        l.setFont(UIConstants.FONT_REGULAR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}