package com.agroshield.soil;
public class SoilAnalyzerService {
    public String analyze(double ph, double moisture, double nitrogen) {
        StringBuilder result = new StringBuilder("Soil Analysis Result:\n");
        if(ph < 5.5) result.append("- High Acidity. Consider adding lime.\n");
        else if(ph > 7.5) result.append("- High Alkalinity. Add sulfur.\n");
        else result.append("- Perfect pH.\n");

        if(moisture < 30) result.append("- Low Moisture. Needs irrigation.\n");
        if(nitrogen < 20) result.append("- Low Nitrogen. Suggest NPK fertilizer.\n");

        result.append("\nSuggested Crop: ");
        if(ph >= 6 && ph <= 7.5 && moisture > 40) result.append("Rice or Wheat.");
        else result.append("Potato or Cotton.");
        
        return result.toString();
    }
}