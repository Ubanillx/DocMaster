package com.ubanillx.pdfconverter.controller;

import com.ubanillx.pdfconverter.model.ConversionRequest;
import com.ubanillx.pdfconverter.model.ConversionResponse;
import com.ubanillx.pdfconverter.model.WatermarkRequest;
import com.ubanillx.pdfconverter.model.WatermarkResponse;
import com.ubanillx.pdfconverter.service.FileStorageService;
import com.ubanillx.pdfconverter.service.LibreOfficeService;
import com.ubanillx.pdfconverter.service.DockerLibreOfficeService;
import com.ubanillx.pdfconverter.service.WatermarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/conversion")
@CrossOrigin(origins = "*")
public class ConversionController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversionController.class);
    
    @Autowired
    private LibreOfficeService libreOfficeService;
    
    @Autowired
    private DockerLibreOfficeService dockerLibreOfficeService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private WatermarkService watermarkService;
    
    // 配置：是否使用 Docker LibreOffice
    private boolean useDockerLibreOffice = System.getenv("USE_DOCKER_LIBREOFFICE") != null && 
                                          System.getenv("USE_DOCKER_LIBREOFFICE").equals("true");
    
    @PostMapping("/convert-url")
    public ResponseEntity<ConversionResponse> convertFromUrl(@RequestBody ConversionRequest request) {
        try {
            logger.info("Received conversion request: URL={}, TargetFormat={}", 
                       request.getUrl(), request.getTargetFormat());
            
            // 验证请求参数
            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "URL不能为空"));
            }
            
            if (request.getTargetFormat() == null || request.getTargetFormat().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "目标格式不能为空"));
            }
            
            // 检查目标格式是否支持
            if (!libreOfficeService.isFormatSupported(request.getTargetFormat())) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "不支持的目标格式: " + request.getTargetFormat()));
            }
            
            // 执行转换
            File convertedFile;
            if (useDockerLibreOffice) {
                convertedFile = dockerLibreOfficeService.convertFile(
                        request.getUrl(), 
                        request.getTargetFormat(), 
                        fileStorageService.getStorageDirectory()
                );
            } else {
                convertedFile = libreOfficeService.convertFile(
                        request.getUrl(), 
                        request.getTargetFormat(), 
                        fileStorageService.getStorageDirectory()
                );
            }
            
            if (convertedFile == null || !convertedFile.exists()) {
                return ResponseEntity.internalServerError()
                        .body(new ConversionResponse(false, "文件转换失败"));
            }
            
            // 存储转换后的文件并生成URL
            String convertedUrl = fileStorageService.storeFile(convertedFile, convertedFile.getName());
            // 从URL中提取文件名
            String storedFileName = convertedUrl.substring(convertedUrl.lastIndexOf('/') + 1);
            long fileSize = fileStorageService.getFileSize(storedFileName);
            
            // 获取原始文件格式
            String originalFormat = getFileExtensionFromUrl(request.getUrl());
            
            ConversionResponse response = new ConversionResponse(
                    true,
                    "转换成功",
                    request.getUrl(),
                    convertedUrl,
                    originalFormat,
                    request.getTargetFormat(),
                    fileSize
            );
            
            logger.info("Conversion completed successfully: {}", convertedUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Conversion failed", e);
            return ResponseEntity.internalServerError()
                    .body(new ConversionResponse(false, "转换失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/convert-upload")
    public ResponseEntity<ConversionResponse> convertFromUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetFormat") String targetFormat) {
        
        try {
            logger.info("Received upload conversion request: File={}, TargetFormat={}", 
                       file.getOriginalFilename(), targetFormat);
            
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "上传文件不能为空"));
            }
            
            // 验证目标格式
            if (targetFormat == null || targetFormat.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "目标格式不能为空"));
            }
            
            if (!libreOfficeService.isFormatSupported(targetFormat)) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "不支持的目标格式: " + targetFormat));
            }
            
            // 保存上传的文件
            String uploadedUrl = fileStorageService.storeUploadedFile(file);
            
            // 执行转换
            File convertedFile;
            if (useDockerLibreOffice) {
                convertedFile = dockerLibreOfficeService.convertFile(
                        uploadedUrl, 
                        targetFormat, 
                        fileStorageService.getStorageDirectory()
                );
            } else {
                convertedFile = libreOfficeService.convertFile(
                        uploadedUrl, 
                        targetFormat, 
                        fileStorageService.getStorageDirectory()
                );
            }
            
            if (convertedFile == null || !convertedFile.exists()) {
                return ResponseEntity.internalServerError()
                        .body(new ConversionResponse(false, "文件转换失败"));
            }
            
            // 存储转换后的文件并生成URL
            String convertedUrl = fileStorageService.storeFile(convertedFile, convertedFile.getName());
            // 从URL中提取文件名
            String storedFileName = convertedUrl.substring(convertedUrl.lastIndexOf('/') + 1);
            long fileSize = fileStorageService.getFileSize(storedFileName);
            
            // 获取原始文件格式
            String originalFormat = getFileExtension(file.getOriginalFilename());
            
            ConversionResponse response = new ConversionResponse(
                    true,
                    "转换成功",
                    uploadedUrl,
                    convertedUrl,
                    originalFormat,
                    targetFormat,
                    fileSize
            );
            
            logger.info("Upload conversion completed successfully: {}", convertedUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Upload conversion failed", e);
            return ResponseEntity.internalServerError()
                    .body(new ConversionResponse(false, "转换失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/formats")
    public ResponseEntity<Map<String, String>> getSupportedFormats() {
        Map<String, String> formats = libreOfficeService.getSupportedFormats();
        return ResponseEntity.ok(formats);
    }
    
    @PostMapping("/watermark")
    public ResponseEntity<WatermarkResponse> addWatermark(@RequestBody WatermarkRequest request) {
        try {
            logger.info("收到水印请求: URL={}, 水印文字={}", request.getUrl(), request.getWatermarkText());
            
            // 验证请求参数
            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new WatermarkResponse(false, "URL不能为空"));
            }
            
            if (request.getWatermarkText() == null || request.getWatermarkText().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new WatermarkResponse(false, "水印文字不能为空"));
            }
            
            // 检查是否为PDF文件
            if (!watermarkService.isPdfFile(request.getUrl())) {
                return ResponseEntity.badRequest()
                        .body(new WatermarkResponse(false, "只支持PDF文件添加水印"));
            }
            
            // 执行水印添加
            File watermarkedFile = watermarkService.addWatermarkToPdf(
                    request.getUrl(), 
                    request.getWatermarkText(), 
                    new File(fileStorageService.getStorageDirectory())
            );
            
            if (watermarkedFile == null || !watermarkedFile.exists()) {
                return ResponseEntity.internalServerError()
                        .body(new WatermarkResponse(false, "水印添加失败"));
            }
            
            // 存储加水印后的文件并生成URL
            String watermarkedUrl = fileStorageService.storeFile(watermarkedFile, watermarkedFile.getName());
            // 从URL中提取文件名
            String storedFileName = watermarkedUrl.substring(watermarkedUrl.lastIndexOf('/') + 1);
            long fileSize = fileStorageService.getFileSize(storedFileName);
            
            WatermarkResponse response = new WatermarkResponse(
                    true,
                    "水印添加成功",
                    request.getUrl(),
                    watermarkedUrl,
                    request.getWatermarkText(),
                    fileSize
            );
            
            logger.info("水印添加完成: {}", watermarkedUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("水印添加失败", e);
            return ResponseEntity.internalServerError()
                    .body(new WatermarkResponse(false, "水印添加失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<ConversionResponse> healthCheck() {
        return ResponseEntity.ok(new ConversionResponse(true, "服务运行正常"));
    }
    
    @PostMapping("/docker/start")
    public ResponseEntity<ConversionResponse> startDockerLibreOffice() {
        try {
            if (!useDockerLibreOffice) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "Docker LibreOffice 模式未启用"));
            }
            
            dockerLibreOfficeService.startContainer();
            return ResponseEntity.ok(new ConversionResponse(true, "Docker LibreOffice 容器已启动"));
            
        } catch (Exception e) {
            logger.error("Failed to start Docker LibreOffice", e);
            return ResponseEntity.internalServerError()
                    .body(new ConversionResponse(false, "启动失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/docker/stop")
    public ResponseEntity<ConversionResponse> stopDockerLibreOffice() {
        try {
            if (!useDockerLibreOffice) {
                return ResponseEntity.badRequest()
                        .body(new ConversionResponse(false, "Docker LibreOffice 模式未启用"));
            }
            
            dockerLibreOfficeService.stopContainer();
            return ResponseEntity.ok(new ConversionResponse(true, "Docker LibreOffice 容器已停止"));
            
        } catch (Exception e) {
            logger.error("Failed to stop Docker LibreOffice", e);
            return ResponseEntity.internalServerError()
                    .body(new ConversionResponse(false, "停止失败: " + e.getMessage()));
        }
    }
    
    private String getFileExtensionFromUrl(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            return getFileExtension(fileName);
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unknown";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "unknown";
    }
}
