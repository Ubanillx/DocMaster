package com.ubanillx.pdfconverter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    @Value("${app.storage.directory:./uploads}")
    private String storageDirectory;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;
    
    public String storeFile(File file, String originalFileName) throws IOException {
        // 创建存储目录
        Path storagePath = Paths.get(storageDirectory);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
        
        // 生成唯一文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);
        String newFileName = timestamp + "_" + uniqueId + "." + extension;
        
        // 移动文件到存储目录
        Path targetPath = storagePath.resolve(newFileName);
        Files.move(file.toPath(), targetPath);
        
        // 生成访问URL
        String fileUrl = baseUrl + "/files/" + newFileName;
        
        logger.info("File stored successfully: {} -> {}", originalFileName, fileUrl);
        return fileUrl;
    }
    
    public String storeUploadedFile(MultipartFile file) throws IOException {
        // 创建存储目录
        Path storagePath = Paths.get(storageDirectory);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
        
        // 生成唯一文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(file.getOriginalFilename());
        String newFileName = timestamp + "_" + uniqueId + "." + extension;
        
        // 保存文件
        Path targetPath = storagePath.resolve(newFileName);
        Files.copy(file.getInputStream(), targetPath);
        
        // 生成访问URL
        String fileUrl = baseUrl + "/files/" + newFileName;
        
        logger.info("Uploaded file stored successfully: {} -> {}", file.getOriginalFilename(), fileUrl);
        return fileUrl;
    }
    
    public File getFile(String fileName) {
        Path filePath = Paths.get(storageDirectory, fileName);
        return filePath.toFile();
    }
    
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(storageDirectory, fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", fileName, e);
            return false;
        }
    }
    
    public long getFileSize(String fileName) {
        try {
            Path filePath = Paths.get(storageDirectory, fileName);
            return Files.size(filePath);
        } catch (IOException e) {
            logger.error("Failed to get file size: {}", fileName, e);
            return 0;
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    public String getStorageDirectory() {
        return storageDirectory;
    }
}
