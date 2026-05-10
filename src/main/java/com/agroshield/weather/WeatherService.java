package com.agroshield.weather;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

public class WeatherService {
    // OpenWeatherMap API Key
    private static final String API_KEY = "d9baba01364472d02fc8ce407efff4ff";
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    
    private double temp = 0.0;
    private double humidity = 0.0;
    private String forecast = "Loading...";
    private double lat = 20.5937; // Default India lat
    private double lon = 78.9629; // Default India lon
    private boolean locationFetched = false;
    private String currentLocation = "Default (India)";
    
    // Allow users to set custom location
    public void setLocation(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
        this.locationFetched = true;
    }
    
    public void setLocationName(String name) {
        this.currentLocation = name;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }

    public void updateWeather() {
        try {
            if (!locationFetched) {
                // Fetch Location
                URL urlLoc = new URI("http://ip-api.com/csv/").toURL();
                HttpURLConnection connLoc = (HttpURLConnection) urlLoc.openConnection();
                connLoc.setRequestMethod("GET");
                connLoc.setConnectTimeout(5000);
                connLoc.setReadTimeout(5000);
                
                try (BufferedReader inLoc = new BufferedReader(new InputStreamReader(connLoc.getInputStream()))) {
                    String locLine = inLoc.readLine();
                    
                    if (locLine != null && locLine.startsWith("success")) {
                        String[] parts = locLine.split(",");
                        if (parts.length > 8) {
                            lat = Double.parseDouble(parts[7]);
                            lon = Double.parseDouble(parts[8]);
                            locationFetched = true;
                        }
                    }
                }
            }
            
            // Fetch Weather from OpenWeatherMap API
            String weatherUrl = API_BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";
            URL urlW = new URI(weatherUrl).toURL();
            HttpURLConnection connW = (HttpURLConnection) urlW.openConnection();
            connW.setRequestMethod("GET");
            connW.setConnectTimeout(5000);
            connW.setReadTimeout(5000);
            
            try (BufferedReader inW = new BufferedReader(new InputStreamReader(connW.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = inW.readLine()) != null) {
                    content.append(inputLine);
                }
                
                String response = content.toString();
                
                // Parse JSON response from OpenWeatherMap
                // Temperature is in "main":{"temp": value
                temp = extractDouble(response, "\"temp\":");
                
                // Humidity is in "main":{"temp":..., "humidity": value
                humidity = extractDouble(response, "\"humidity\":");
                
                // Weather description is in "weather":[{"main":"description"
                String description = extractString(response, "\"weather\":[{\"main\":\"", "\"");
                if (description != null && !description.isEmpty()) {
                    forecast = description;
                } else {
                    forecast = "Unknown";
                }
            }
            
        } catch (Exception e) {
            if (forecast.equals("Loading...")) {
                forecast = "Offline/Error";
            }
        }
    }
    
    private double extractDouble(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return 0.0;
        idx += key.length();
        int endIdx = json.indexOf(",", idx);
        if (endIdx == -1) endIdx = json.indexOf("}", idx);
        if (endIdx == -1) return 0.0;
        try {
            return Double.parseDouble(json.substring(idx, endIdx).trim());
        } catch(Exception e) {
            return 0.0;
        }
    }
    
    private String extractString(String json, String startKey, String endKey) {
        int idx = json.indexOf(startKey);
        if (idx == -1) return null;
        idx += startKey.length();
        int endIdx = json.indexOf(endKey, idx);
        if (endIdx == -1) return null;
        return json.substring(idx, endIdx).trim();
    }

    public double getTemp() { return temp; }
    public double getHumidity() { return humidity; }
    public String getForecast() { return forecast; }
}