package com.agroshield.weather;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

public class WeatherService {
    private double temp = 0.0;
    private double humidity = 0.0;
    private String forecast = "Loading...";
    private double lat = 20.5937; // Default India lat
    private double lon = 78.9629; // Default India lon
    private boolean locationFetched = false;

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
            
            // Fetch Weather
            URL urlW = new URI("https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current=temperature_2m,relative_humidity_2m,weather_code").toURL();
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
                
                // Parse JSON fields manually
                temp = extractDouble(response, "\"temperature_2m\":");
                humidity = extractDouble(response, "\"relative_humidity_2m\":");
                int code = (int) extractDouble(response, "\"weather_code\":");
                
                forecast = mapWeatherCode(code);
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
    
    private String mapWeatherCode(int code) {
        if (code == 0) return "Clear Sky";
        if (code == 1 || code == 2 || code == 3) return "Cloudy";
        if (code >= 45 && code <= 48) return "Foggy";
        if (code >= 51 && code <= 67) return "Rainy";
        if (code >= 71 && code <= 77) return "Snowy";
        if (code >= 95) return "Thunderstorm";
        return "Variable";
    }

    public double getTemp() { return temp; }
    public double getHumidity() { return humidity; }
    public String getForecast() { return forecast; }
}