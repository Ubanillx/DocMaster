package com.ubanillx.pdfconverter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversionResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("originalUrl")
    private String originalUrl;
    
    @JsonProperty("convertedUrl")
    private String convertedUrl;
    
    @JsonProperty("originalFormat")
    private String originalFormat;
    
    @JsonProperty("convertedFormat")
    private String convertedFormat;
    
    @JsonProperty("fileSize")
    private long fileSize;
    
    public ConversionResponse() {}
    
    public ConversionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ConversionResponse(boolean success, String message, String originalUrl, 
                            String convertedUrl, String originalFormat, 
                            String convertedFormat, long fileSize) {
        this.success = success;
        this.message = message;
        this.originalUrl = originalUrl;
        this.convertedUrl = convertedUrl;
        this.originalFormat = originalFormat;
        this.convertedFormat = convertedFormat;
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
    
    public String getConvertedUrl() {
        return convertedUrl;
    }
    
    public void setConvertedUrl(String convertedUrl) {
        this.convertedUrl = convertedUrl;
    }
    
    public String getOriginalFormat() {
        return originalFormat;
    }
    
    public void setOriginalFormat(String originalFormat) {
        this.originalFormat = originalFormat;
    }
    
    public String getConvertedFormat() {
        return convertedFormat;
    }
    
    public void setConvertedFormat(String convertedFormat) {
        this.convertedFormat = convertedFormat;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
