package com.ubanillx.pdfconverter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WatermarkRequest {
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("watermarkText")
    private String watermarkText;
    
    public WatermarkRequest() {}
    
    public WatermarkRequest(String url, String watermarkText) {
        this.url = url;
        this.watermarkText = watermarkText;
    }
    
    // Getters and Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getWatermarkText() {
        return watermarkText;
    }
    
    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }
}

