package com.ubanillx.pdfconverter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversionRequest {
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("targetFormat")
    private String targetFormat;
    
    public ConversionRequest() {}
    
    public ConversionRequest(String url, String targetFormat) {
        this.url = url;
        this.targetFormat = targetFormat;
    }
    
    // Getters and Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getTargetFormat() {
        return targetFormat;
    }
    
    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }
}
