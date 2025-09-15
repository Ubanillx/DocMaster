package com.ubanillx.pdfconverter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通过 Docker 容器使用 LibreOffice 的服务
 * 用于开发环境，避免在本地安装 LibreOffice
 */
@Service
public class DockerLibreOfficeService {
    
    private static final Logger logger = LoggerFactory.getLogger(DockerLibreOfficeService.class);
    
    @Value("${docker.libreoffice.container.name:pdf-converter-libreoffice}")
    private String containerName;
    
    @Value("${docker.libreoffice.timeout:30}")
    private int timeoutSeconds;
    
    /**
     * 通过 Docker 容器转换文件
     */
    public File convertFile(String sourceUrl, String targetFormat, String outputDir) throws Exception {
        logger.info("Converting file via Docker LibreOffice: {} to {}", sourceUrl, targetFormat);
        
        // 检查容器是否运行
        if (!isContainerRunning()) {
            throw new Exception("LibreOffice Docker container is not running. Please start it with: docker-compose up -d");
        }
        
        // 如果是 URL，先下载文件
        String localFilePath = sourceUrl;
        if (sourceUrl.startsWith("http://") || sourceUrl.startsWith("https://")) {
            localFilePath = downloadFileFromUrl(sourceUrl);
        }
        
        // 准备转换命令
        List<String> command = buildConversionCommand(localFilePath, targetFormat, outputDir);
        
        // 执行转换
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        logger.info("Executing Docker command: {}", String.join(" ", command));
        
        Process process = processBuilder.start();
        
        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.debug("Docker output: {}", line);
            }
        }
        
        // 等待进程完成
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new Exception("LibreOffice conversion timed out after " + timeoutSeconds + " seconds");
        }
        
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new Exception("LibreOffice conversion failed with exit code " + exitCode + 
                              ". Output: " + output.toString());
        }
        
        // 查找输出文件
        File outputFile = findOutputFile(localFilePath, targetFormat, outputDir);
        if (outputFile == null || !outputFile.exists()) {
            // 提供更详细的错误信息
            String errorMsg = "Output file not found after conversion. ";
            errorMsg += "Source: " + localFilePath + ", Target format: " + targetFormat + ", Output dir: " + outputDir;
            if (outputFile != null) {
                errorMsg += ", Expected path: " + outputFile.getAbsolutePath();
            }
            
            // 列出 outputs 目录中的文件以便调试
            try {
                Path outputsDir = Paths.get("outputs");
                if (Files.exists(outputsDir)) {
                    errorMsg += ", Files in outputs directory: ";
                    final StringBuilder fileList = new StringBuilder();
                    Files.list(outputsDir).forEach(path -> fileList.append(path.getFileName()).append(" "));
                    errorMsg += fileList.toString();
                } else {
                    errorMsg += ", Outputs directory does not exist";
                }
            } catch (Exception e) {
                errorMsg += ", Could not list outputs directory: " + e.getMessage();
            }
            
            throw new Exception(errorMsg);
        }
        
        logger.info("Conversion completed successfully: {}", outputFile.getAbsolutePath());
        return outputFile;
    }
    
    /**
     * 构建 Docker 转换命令
     */
    private List<String> buildConversionCommand(String sourceUrl, String targetFormat, String outputDir) {
        // 将本地路径转换为容器内路径
        String containerSourcePath = convertToContainerPath(sourceUrl);
        
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("exec");
        command.add("-i");
        command.add(containerName);
        command.add("libreoffice");
        command.add("--headless");
        command.add("--convert-to");
        command.add(targetFormat);
        command.add("--outdir");
        command.add("/app/outputs");
        command.add(containerSourcePath);
        
        return command;
    }
    
    /**
     * 将本地文件路径转换为容器内路径
     */
    private String convertToContainerPath(String localPath) {
        // 提取文件名
        String fileName = Paths.get(localPath).getFileName().toString();
        // 返回容器内的路径
        return "/app/uploads/" + fileName;
    }
    
    /**
     * 从 URL 下载文件到本地
     */
    private String downloadFileFromUrl(String url) throws Exception {
        logger.info("Downloading file from URL: {}", url);
        
        try {
            // 创建临时文件名
            String fileName = "downloaded_" + System.currentTimeMillis() + ".tmp";
            String localPath = Paths.get("uploads", fileName).toString();
            
            // 下载文件
            java.net.URL fileUrl = new java.net.URL(url);
            try (InputStream in = fileUrl.openStream();
                 FileOutputStream out = new FileOutputStream(localPath)) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
            logger.info("File downloaded successfully: {}", localPath);
            return localPath;
            
        } catch (Exception e) {
            logger.error("Failed to download file from URL: {}", e.getMessage());
            throw new Exception("Failed to download file from URL: " + e.getMessage());
        }
    }
    
    /**
     * 检查 Docker 容器是否运行
     */
    private boolean isContainerRunning() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "--filter", 
                "name=" + containerName, "--format", "{{.Names}}");
            Process process = processBuilder.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            
            int exitCode = process.waitFor();
            return exitCode == 0 && output.toString().trim().equals(containerName);
            
        } catch (Exception e) {
            logger.error("Failed to check container status: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 查找转换后的输出文件
     */
    private File findOutputFile(String sourceUrl, String targetFormat, String outputDir) {
        try {
            // 从源 URL 中提取文件名
            String sourceFileName = Paths.get(sourceUrl).getFileName().toString();
            String baseName = sourceFileName.substring(0, sourceFileName.lastIndexOf('.'));
            String outputFileName = baseName + "." + targetFormat.toLowerCase();
            
            // 确保本地 outputs 目录存在
            Path outputsDir = Paths.get("outputs");
            if (!Files.exists(outputsDir)) {
                Files.createDirectories(outputsDir);
                logger.info("Created outputs directory: {}", outputsDir.toAbsolutePath());
            }
            
            // 使用本地的 outputs 目录，因为 Docker 卷映射到了这里
            Path outputPath = Paths.get("outputs", outputFileName);
            File outputFile = outputPath.toFile();
            
            // 如果文件不存在，等待一下再检查（Docker 卷同步可能需要时间）
            if (!outputFile.exists()) {
                logger.info("Output file not found immediately, waiting for Docker volume sync...");
                Thread.sleep(2000); // 等待2秒让Docker卷同步
            }
            
            return outputFile;
            
        } catch (Exception e) {
            logger.error("Failed to find output file: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 启动 LibreOffice Docker 容器
     */
    public void startContainer() throws Exception {
        logger.info("Starting LibreOffice Docker container...");
        
        ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "up", "-d", "libreoffice");
        Process process = processBuilder.start();
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Failed to start LibreOffice container");
        }
        
        // 等待容器启动
        Thread.sleep(5000);
        
        if (!isContainerRunning()) {
            throw new Exception("LibreOffice container failed to start");
        }
        
        logger.info("LibreOffice Docker container started successfully");
    }
    
    /**
     * 停止 LibreOffice Docker 容器
     */
    public void stopContainer() throws Exception {
        logger.info("Stopping LibreOffice Docker container...");
        
        ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "down");
        Process process = processBuilder.start();
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Failed to stop LibreOffice container");
        }
        
        logger.info("LibreOffice Docker container stopped");
    }
}
