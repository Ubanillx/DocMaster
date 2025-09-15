package com.ubanillx.pdfconverter.controller;

import com.ubanillx.pdfconverter.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/files")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            File file = fileStorageService.getFile(fileName);
            
            if (!file.exists()) {
                logger.warn("File not found: {}", fileName);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 确定内容类型
            String contentType = determineContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("Error downloading file: {}", fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private String determineContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "odt":
                return "application/vnd.oasis.opendocument.text";
            case "ods":
                return "application/vnd.oasis.opendocument.spreadsheet";
            case "odp":
                return "application/vnd.oasis.opendocument.presentation";
            case "rtf":
                return "application/rtf";
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            default:
                return "application/octet-stream";
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}
