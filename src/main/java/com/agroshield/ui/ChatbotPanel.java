package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.CustomButton;
import com.agroshield.chatbot.ChatbotService;

public class ChatbotPanel extends JPanel {
    private ChatbotService bot = new ChatbotService();

    public ChatbotPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("AI AgriBot");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(UIConstants.SURFACE_DARK);
        chatArea.setForeground(UIConstants.TEXT_LIGHT);
        chatArea.setFont(UIConstants.FONT_REGULAR);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setOpaque(false);
        JTextField input = new JTextField();
        CustomButton send = new CustomButton("Send");
        CustomButton voiceBtn = new CustomButton("🎙️ Voice");
        
        send.addActionListener(e -> {
            String text = input.getText().trim();
            if(!text.isEmpty()) {
                chatArea.append("You: " + text + "\n");
                chatArea.append("AgriBot: " + bot.getResponse(text) + "\n\n");
                input.setText("");
            }
        });

        String[] languages = {"English (en-US)", "Hindi (hi-IN)", "Punjabi (pa-IN)"};
        JComboBox<String> langCombo = new JComboBox<>(languages);
        langCombo.setBackground(UIConstants.SURFACE_DARK);
        langCombo.setForeground(UIConstants.TEXT_LIGHT);

        voiceBtn.addActionListener(e -> {
            voiceBtn.setEnabled(false);
            voiceBtn.setText("Listening...");
            String selectedLang = (String) langCombo.getSelectedItem();
            String culture = selectedLang.substring(selectedLang.indexOf("(") + 1, selectedLang.indexOf(")"));
            
            new Thread(() -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder("python", "speech.py", culture);
                    Process p = pb.start();
                    String result;
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream(), "UTF-8"))) {
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append(" ");
                        }
                        result = sb.toString().trim();
                    }
                    p.waitFor();
                    final String finalResult = result;
                    SwingUtilities.invokeLater(() -> {
                        voiceBtn.setEnabled(true);
                        voiceBtn.setText("🎙️ Voice");
                        if (!finalResult.isEmpty() && !finalResult.equals("NO_SPEECH") && !finalResult.startsWith("ERROR:")) {
                            input.setText(finalResult);
                        } else if (finalResult.equals("NO_SPEECH")) {
                            JOptionPane.showMessageDialog(ChatbotPanel.this, "No speech detected. Please check your microphone and speak clearly.");
                        } else if (finalResult.startsWith("ERROR:")) {
                            JOptionPane.showMessageDialog(ChatbotPanel.this, "Voice Service Error: " + finalResult.substring(6));
                        } else {
                            JOptionPane.showMessageDialog(ChatbotPanel.this, "Could not process speech input.");
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        voiceBtn.setEnabled(true);
                        voiceBtn.setText("🎙️ Voice");
                    });
                }
            }).start();
        });

        input.addActionListener(e -> send.doClick());

        JPanel btns = new JPanel(new GridLayout(1, 3, 5, 0));
        btns.setOpaque(false);
        btns.add(langCombo);
        btns.add(voiceBtn);
        btns.add(send);

        bottom.add(input, BorderLayout.CENTER);
        bottom.add(btns, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }
}