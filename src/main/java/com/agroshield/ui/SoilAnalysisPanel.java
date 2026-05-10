package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.*;
import com.agroshield.soil.SoilAnalyzerService;

public class SoilAnalysisPanel extends JPanel {
    private SoilAnalyzerService soilAnalyzer = new SoilAnalyzerService();

    public SoilAnalysisPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Soil Analysis Module");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(4, 2, 20, 20));
        form.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextField phField = new JTextField();
        JTextField moistField = new JTextField();
        JTextField nitroField = new JTextField();

        JLabel phLbl = new JLabel("pH Level (1-14):"); phLbl.setForeground(UIConstants.TEXT_LIGHT);
        JLabel mLbl = new JLabel("Moisture (%):"); mLbl.setForeground(UIConstants.TEXT_LIGHT);
        JLabel nLbl = new JLabel("Nitrogen (mg/kg):"); nLbl.setForeground(UIConstants.TEXT_LIGHT);

        form.add(phLbl); form.add(phField);
        form.add(mLbl); form.add(moistField);
        form.add(nLbl); form.add(nitroField);

        CustomButton analyzeBtn = new CustomButton("Analyze Soil");
        form.add(analyzeBtn);

        JTextArea resArea = new JTextArea();
        resArea.setEditable(false);
        resArea.setBackground(UIConstants.BG_DARK);
        resArea.setForeground(UIConstants.PRIMARY);

        analyzeBtn.addActionListener(e -> {
            try {
                double ph = Double.parseDouble(phField.getText());
                double moist = Double.parseDouble(moistField.getText());
                double nitro = Double.parseDouble(nitroField.getText());
                resArea.setText(soilAnalyzer.analyze(ph, moist, nitro));
            } catch (Exception ex) {
                resArea.setText("Please enter valid numeric values!");
            }
        });

        add(form, BorderLayout.CENTER);
        add(new JScrollPane(resArea), BorderLayout.SOUTH);
    }
}