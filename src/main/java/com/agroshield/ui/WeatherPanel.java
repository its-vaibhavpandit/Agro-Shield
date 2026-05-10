package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.RoundedPanel;
import com.agroshield.ui.components.CustomButton;
import com.agroshield.weather.WeatherService;

public class WeatherPanel extends JPanel {
    private WeatherService weatherService = new WeatherService();
    private JLabel tempLbl, humLbl, forecastLbl, locLbl;
    private JTextField latField, lonField, locNameField;
    private Timer weatherTimer;

    public WeatherPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Live Weather Forecast");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        // Center Panel: Weather Display + Location Input
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setOpaque(false);
        
        // Weather Display Card
        RoundedPanel weatherCard = new RoundedPanel();
        weatherCard.setLayout(new GridLayout(4, 1, 10, 10));
        weatherCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        locLbl = new JLabel("Location: Default (India)");
        locLbl.setFont(UIConstants.FONT_REGULAR);
        locLbl.setForeground(UIConstants.TEXT_LIGHT);
        
        tempLbl = new JLabel("Temperature: -- °C");
        tempLbl.setFont(UIConstants.FONT_TITLE);
        tempLbl.setForeground(UIConstants.PRIMARY);
        
        humLbl = new JLabel("Humidity: -- %");
        humLbl.setFont(UIConstants.FONT_TITLE);
        humLbl.setForeground(UIConstants.TEXT_LIGHT);
        
        forecastLbl = new JLabel("Forecast: Loading...");
        forecastLbl.setFont(UIConstants.FONT_TITLE);
        forecastLbl.setForeground(UIConstants.TEXT_LIGHT);

        weatherCard.add(locLbl);
        weatherCard.add(tempLbl);
        weatherCard.add(humLbl);
        weatherCard.add(forecastLbl);
        centerPanel.add(weatherCard);
        
        // Location Input Card
        RoundedPanel locCard = new RoundedPanel();
        locCard.setLayout(new BorderLayout(15, 15));
        locCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel locTitle = new JLabel("🌍 Set Your Location");
        locTitle.setFont(UIConstants.FONT_REGULAR);
        locTitle.setForeground(UIConstants.TEXT_LIGHT);
        locCard.add(locTitle, BorderLayout.NORTH);
        
        // Input Fields Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setOpaque(false);
        
        // Location Name
        JLabel nameLabel = new JLabel("Location Name:");
        nameLabel.setForeground(UIConstants.TEXT_LIGHT);
        locNameField = new JTextField("New Delhi");
        locNameField.setBackground(UIConstants.SURFACE_DARK);
        locNameField.setForeground(UIConstants.TEXT_LIGHT);
        locNameField.setCaretColor(UIConstants.TEXT_LIGHT);
        inputPanel.add(nameLabel);
        inputPanel.add(locNameField);
        
        // Latitude
        JLabel latLabel = new JLabel("Latitude:");
        latLabel.setForeground(UIConstants.TEXT_LIGHT);
        latField = new JTextField("28.6139");
        latField.setBackground(UIConstants.SURFACE_DARK);
        latField.setForeground(UIConstants.TEXT_LIGHT);
        latField.setCaretColor(UIConstants.TEXT_LIGHT);
        inputPanel.add(latLabel);
        inputPanel.add(latField);
        
        // Longitude
        JLabel lonLabel = new JLabel("Longitude:");
        lonLabel.setForeground(UIConstants.TEXT_LIGHT);
        lonField = new JTextField("77.2090");
        lonField.setBackground(UIConstants.SURFACE_DARK);
        lonField.setForeground(UIConstants.TEXT_LIGHT);
        lonField.setCaretColor(UIConstants.TEXT_LIGHT);
        inputPanel.add(lonLabel);
        inputPanel.add(lonField);
        
        locCard.add(inputPanel, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        CustomButton updateBtn = new CustomButton("Update Location");
        updateBtn.addActionListener(e -> updateLocation());
        
        CustomButton resetBtn = new CustomButton("Reset to Default");
        resetBtn.addActionListener(e -> resetLocation());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(resetBtn);
        
        locCard.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.add(locCard);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Start weather update timer
        startWeatherUpdates();
    }
    
    private void startWeatherUpdates() {
        weatherTimer = new Timer(60000, e -> updateWeatherDisplay());
        weatherTimer.setInitialDelay(0);
        weatherTimer.start();
    }
    
    private void updateWeatherDisplay() {
        new Thread(() -> {
            weatherService.updateWeather();
            SwingUtilities.invokeLater(() -> {
                tempLbl.setText(String.format("Temperature: %.1f °C", weatherService.getTemp()));
                humLbl.setText(String.format("Humidity: %.1f %%", weatherService.getHumidity()));
                forecastLbl.setText("Forecast: " + weatherService.getForecast());
                locLbl.setText("Location: " + weatherService.getCurrentLocation());
            });
        }).start();
    }
    
    private void updateLocation() {
        try {
            String locName = locNameField.getText().trim();
            double lat = Double.parseDouble(latField.getText().trim());
            double lon = Double.parseDouble(lonField.getText().trim());
            
            if (locName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a location name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            weatherService.setLocation(lat, lon);
            weatherService.setLocationName(locName);
            
            updateWeatherDisplay();
            
            JOptionPane.showMessageDialog(this, "Location updated to " + locName + "!\nFetching weather...", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid latitude and longitude values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetLocation() {
        locNameField.setText("Default (India)");
        latField.setText("20.5937");
        lonField.setText("78.9629");
        
        weatherService.setLocation(20.5937, 78.9629);
        weatherService.setLocationName("Default (India)");
        
        updateWeatherDisplay();
        
        JOptionPane.showMessageDialog(this, "Location reset to India default.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}