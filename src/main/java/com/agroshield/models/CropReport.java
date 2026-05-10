package com.agroshield.models;
import java.io.Serializable;
import java.time.LocalDateTime;
public class CropReport implements Serializable, Comparable<CropReport> {
    private static final long serialVersionUID = 1L;
    private String id;
    private String cropName;
    private String disease;
    private double confidence;
    private LocalDateTime timestamp;
    public CropReport(String id, String cropName, String disease, double confidence) {
        this.id = id; this.cropName = cropName; this.disease = disease;
        this.confidence = confidence; this.timestamp = LocalDateTime.now();
    }
    public String getId() { return id; }
    public String getCropName() { return cropName; }
    public String getDisease() { return disease; }
    public double getConfidence() { return confidence; }
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public int compareTo(CropReport o) { return o.timestamp.compareTo(this.timestamp); }
}