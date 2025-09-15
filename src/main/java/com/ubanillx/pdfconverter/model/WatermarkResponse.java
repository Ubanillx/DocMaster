package com.ubanillx.pdfconverter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WatermarkResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("originalUrl")
    private String originalUrl;
    
    @JsonProperty("watermarkedUrl")
    private String watermarkedUrl;
    
    @JsonProperty("watermarkText")
    private String watermarkText;
    
    @JsonProperty("fileSize")
    private long fileSize;
    
    public WatermarkResponse() {}
    
    public WatermarkResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public WatermarkResponse(boolean success, String message, String originalUrl, 
                           String watermarkedUrl, String watermarkText, long fileSize) {
        this.success = success;
        this.message = message;
        this.originalUrl = originalUrl;
        this.watermarkedUrl = watermarkedUrl;
        this.watermarkText = watermarkText;
        this.fileSize = fileSize;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public String getWatermarkedUrl() {
        return watermarkedUrl;
    }
    
    public void setWatermarkedUrl(String watermarkedUrl) {
        this.watermarkedUrl = watermarkedUrl;
    }
    
    public String getWatermarkText() {
        return watermarkText;
    }
    
    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}

