package com.agroshield.disease;
import java.util.*;

public class DiseaseScannerService {
    private Map<String, List<String>> diseaseDb = new HashMap<>();

    public DiseaseScannerService() {
        diseaseDb.put("Wheat", Arrays.asList("Leaf Rust", "Powdery Mildew", "Healthy"));
        diseaseDb.put("Rice", Arrays.asList("Brown Spot", "Blast", "Healthy"));
        diseaseDb.put("Tomato", Arrays.asList("Late Blight", "Early Blight", "Healthy"));
    }

    public String scanImage(String cropType) {
        try {
            Thread.sleep(2000); // Simulate AI scanning delay
        } catch (InterruptedException e) {
            // Restore the interrupted status of the thread
            Thread.currentThread().interrupt();
        }
        
        List<String> diseases = diseaseDb.getOrDefault(cropType, Arrays.asList("Unknown Disease", "Healthy"));
        Random rand = new Random();
        return diseases.get(rand.nextInt(diseases.size()));
    }
    
    public double getConfidence() {
        return 75.0 + new Random().nextDouble() * 24.0;
    }
}