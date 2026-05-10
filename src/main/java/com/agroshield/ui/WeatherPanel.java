package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.RoundedPanel;
import com.agroshield.weather.WeatherService;

public class WeatherPanel extends JPanel {
    private WeatherService weatherService = new WeatherService();
    private JLabel tempLbl, humLbl, forecastLbl;

    public WeatherPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Live Weather Forecast");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new GridLayout(3, 1));
        
        tempLbl = new JLabel(); tempLbl.setFont(UIConstants.FONT_TITLE); tempLbl.setForeground(UIConstants.PRIMARY);
        humLbl = new JLabel(); humLbl.setFont(UIConstants.FONT_TITLE); humLbl.setForeground(UIConstants.TEXT_LIGHT);
        forecastLbl = new JLabel(); forecastLbl.setFont(UIConstants.FONT_TITLE); forecastLbl.setForeground(UIConstants.TEXT_LIGHT);

        card.add(tempLbl); card.add(humLbl); card.add(forecastLbl);
        add(card, BorderLayout.CENTER);

        // Use Timer with background thread to prevent UI freeze during HTTP request
        Timer timer = new Timer(60000, e -> {
            new Thread(() -> {
                weatherService.updateWeather();
                SwingUtilities.invokeLater(() -> {
                    tempLbl.setText(String.format("Temperature: %.1f °C", weatherService.getTemp()));
                    humLbl.setText(String.format("Humidity: %.1f %%", weatherService.getHumidity()));
                    forecastLbl.setText("Forecast: " + weatherService.getForecast());
                });
            }).start();
        });
        timer.setInitialDelay(0);
        timer.start();
    }
}