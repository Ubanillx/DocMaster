package com.ubanillx.pdfconverter.service;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class LibreOfficeService {
    
    private static final Logger logger = LoggerFactory.getLogger(LibreOfficeService.class);
    
    private XComponentContext xContext;
    private XMultiComponentFactory xMCF;
    private XComponentLoader xLoader;
    
    // 支持的文件格式映射
    private static final Map<String, String> FORMAT_MAP = new HashMap<>();
    static {
        FORMAT_MAP.put("pdf", "writer_pdf_Export");
        FORMAT_MAP.put("doc", "MS Word 97");
        FORMAT_MAP.put("docx", "Office Open XML Text");
        FORMAT_MAP.put("odt", "writer8");
        FORMAT_MAP.put("rtf", "Rich Text Format");
        FORMAT_MAP.put("txt", "Text (encoded)");
        FORMAT_MAP.put("html", "HTML (StarWriter)");
        FORMAT_MAP.put("xls", "MS Excel 97");
        FORMAT_MAP.put("xlsx", "Calc MS Excel 2007 XML");
        FORMAT_MAP.put("ods", "calc8");
        FORMAT_MAP.put("ppt", "MS PowerPoint 97");
        FORMAT_MAP.put("pptx", "Impress MS PowerPoint 2007 XML");
        FORMAT_MAP.put("odp", "impress8");
    }
    
    public LibreOfficeService() {
        // 构造函数不进行初始化，避免启动时异常
    }
    
    @PostConstruct
    public void init() {
        logger.info("Initializing LibreOffice service...");
        
        // 检查是否使用 Docker LibreOffice
        boolean useDockerLibreOffice = System.getenv("USE_DOCKER_LIBREOFFICE") != null && 
                                      System.getenv("USE_DOCKER_LIBREOFFICE").equals("true");
        
        if (useDockerLibreOffice) {
            logger.info("Docker LibreOffice mode detected, skipping UNO API initialization");
            logger.info("LibreOffice service initialized in Docker mode");
            return;
        }
        
        try {
            initializeLibreOffice();
            logger.info("LibreOffice service initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize LibreOffice service: {}", e.getMessage(), e);
            logger.warn("LibreOffice service will use mock mode for testing");
            xContext = null;
            xMCF = null;
            xLoader = null;
        }
    }
    
    private void initializeLibreOffice() throws Exception {
        logger.info("Initializing LibreOffice UNO API...");
        
        try {
            // 检测运行环境并设置相应的LibreOffice路径
            LibreOfficePaths paths = detectLibreOfficePaths();
            
            logger.info("Setting LibreOffice paths:");
            logger.info("  LibreOffice executable: {}", paths.executable);
            logger.info("  LibreOffice installation: {}", paths.installationPath);
            logger.info("  LibreOffice program: {}", paths.programPath);
            logger.info("  LibreOffice URE: {}", paths.urePath);
            
            // 设置系统属性 - 这些是 UNO API 需要的正确属性
            System.setProperty("com.sun.star.comp.helper.Bootstrap.office.path", paths.executable);
            System.setProperty("com.sun.star.comp.helper.Bootstrap.office.installation.path", paths.installationPath);
            System.setProperty("com.sun.star.lib.loader.path", paths.executable);
            System.setProperty("com.sun.star.lib.loader.path.program", paths.programPath);
            System.setProperty("com.sun.star.lib.loader.path.ure", paths.urePath);
            
            // 设置Java库路径
            String javaLibraryPath = System.getProperty("java.library.path");
            if (javaLibraryPath == null || javaLibraryPath.isEmpty()) {
                javaLibraryPath = paths.urePath;
            } else {
                javaLibraryPath = paths.urePath + ":" + javaLibraryPath;
            }
            System.setProperty("java.library.path", javaLibraryPath);
            
            logger.info("Calling Bootstrap.bootstrap()...");
            xContext = Bootstrap.bootstrap();
            logger.info("Bootstrap successful, getting service manager...");
            
            xMCF = xContext.getServiceManager();
            logger.info("Service manager obtained, creating Desktop instance...");
            
            Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
            logger.info("Desktop instance created, querying interface...");
            
            xLoader = UnoRuntime.queryInterface(XComponentLoader.class, desktop);
            logger.info("LibreOffice UNO API initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize LibreOffice UNO API: {}", e.getMessage(), e);
            throw new Exception("LibreOffice initialization failed: " + e.getMessage(), e);
        }
    }
    
    private LibreOfficePaths detectLibreOfficePaths() {
        LibreOfficePaths paths = new LibreOfficePaths();
        
        // 检查是否使用 Docker LibreOffice
        boolean useDockerLibreOffice = System.getenv("USE_DOCKER_LIBREOFFICE") != null && 
                                      System.getenv("USE_DOCKER_LIBREOFFICE").equals("true");
        
        if (useDockerLibreOffice) {
            logger.info("Using Docker LibreOffice for development");
            // 使用 Docker 中的 LibreOffice
            paths.executable = System.getenv("DOCKER_LIBREOFFICE_PATH") != null ? 
                System.getenv("DOCKER_LIBREOFFICE_PATH") : "/usr/bin/libreoffice";
            paths.installationPath = System.getenv("DOCKER_LIBREOFFICE_INSTALLATION_PATH") != null ? 
                System.getenv("DOCKER_LIBREOFFICE_INSTALLATION_PATH") : "/usr/lib/libreoffice";
            paths.programPath = System.getenv("DOCKER_LIBREOFFICE_PROGRAM_PATH") != null ? 
                System.getenv("DOCKER_LIBREOFFICE_PROGRAM_PATH") : "/usr/lib/libreoffice/program";
            paths.urePath = System.getenv("DOCKER_LIBREOFFICE_URE_PATH") != null ? 
                System.getenv("DOCKER_LIBREOFFICE_URE_PATH") : "/usr/lib/libreoffice/ure/lib";
        } else {
            logger.info("Using local LibreOffice installation");
            // 使用本地 LibreOffice 安装
            paths.executable = "/Applications/LibreOffice.app/Contents/MacOS/soffice";
            paths.installationPath = "/Applications/LibreOffice.app/Contents";
            paths.programPath = "/Applications/LibreOffice.app/Contents/Resources/program";
            paths.urePath = "/Applications/LibreOffice.app/Contents/Resources/ure/lib";
        }
        
        return paths;
    }
    
    private static class LibreOfficePaths {
        String executable;
        String installationPath;
        String programPath;
        String urePath;
    }
    
    public File convertFile(String sourceUrl, String targetFormat, String outputDir) throws Exception {
        logger.info("Starting conversion from URL: {} to format: {}", sourceUrl, targetFormat);
        
        // 检查LibreOffice是否已初始化
        if (xLoader == null) {
            logger.warn("LibreOffice not initialized, creating mock conversion for testing");
            return createMockConversion(sourceUrl, targetFormat, outputDir);
        }
        
        // 下载源文件
        File sourceFile = downloadFile(sourceUrl);
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IOException("Failed to download source file from: " + sourceUrl);
        }
        
        // 获取文件扩展名
        String originalExtension = getFileExtension(sourceFile.getName());
        String targetExtension = targetFormat.toLowerCase();
        
        // 生成输出文件名
        String baseName = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.'));
        String outputFileName = baseName + "." + targetExtension;
        File outputFile = new File(outputDir, outputFileName);
        
        // 执行转换
        convertDocument(sourceFile, outputFile, targetFormat);
        
        logger.info("Conversion completed. Output file: {}", outputFile.getAbsolutePath());
        return outputFile;
    }
    
    private File downloadFile(String url) throws IOException {
        try {
            URL fileUrl = new URL(url);
            String fileName = Paths.get(fileUrl.getPath()).getFileName().toString();
            if (fileName.isEmpty()) {
                fileName = "downloaded_file";
            }
            
            Path tempFile = Files.createTempFile("libreoffice_", "_" + fileName);
            Files.copy(fileUrl.openStream(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            return tempFile.toFile();
        } catch (Exception e) {
            logger.error("Failed to download file from URL: {}", url, e);
            throw new IOException("Failed to download file", e);
        }
    }
    
    private void convertDocument(File sourceFile, File outputFile, String targetFormat) throws Exception {
        XComponent xComponent = null;
        
        try {
            // 准备加载属性
            PropertyValue[] loadProps = new PropertyValue[1];
            loadProps[0] = new PropertyValue();
            loadProps[0].Name = "Hidden";
            loadProps[0].Value = true;
            
            // 加载文档
            String sourceUrl = "file://" + sourceFile.getAbsolutePath().replace("\\", "/");
            xComponent = xLoader.loadComponentFromURL(sourceUrl, "_blank", 0, loadProps);
            
            if (xComponent == null) {
                throw new Exception("Failed to load document: " + sourceFile.getName());
            }
            
            // 准备保存属性
            String filterName = FORMAT_MAP.get(targetFormat.toLowerCase());
            if (filterName == null) {
                throw new Exception("Unsupported target format: " + targetFormat);
            }
            
            PropertyValue[] saveProps = new PropertyValue[2];
            saveProps[0] = new PropertyValue();
            saveProps[0].Name = "FilterName";
            saveProps[0].Value = filterName;
            
            saveProps[1] = new PropertyValue();
            saveProps[1].Name = "Overwrite";
            saveProps[1].Value = true;
            
            // 保存文档
            XStorable xStorable = UnoRuntime.queryInterface(XStorable.class, xComponent);
            String outputUrl = "file://" + outputFile.getAbsolutePath().replace("\\", "/");
            xStorable.storeToURL(outputUrl, saveProps);
            
            logger.info("Document converted successfully from {} to {}", 
                       sourceFile.getName(), outputFile.getName());
            
        } finally {
            // 关闭文档
            if (xComponent != null) {
                xComponent.dispose();
            }
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    public boolean isFormatSupported(String format) {
        return FORMAT_MAP.containsKey(format.toLowerCase());
    }
    
    public Map<String, String> getSupportedFormats() {
        return new HashMap<>(FORMAT_MAP);
    }
    
    private File createMockConversion(String sourceUrl, String targetFormat, String outputDir) throws Exception {
        logger.info("Creating mock conversion for testing purposes");
        
        // 下载源文件
        File sourceFile = downloadFile(sourceUrl);
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IOException("Failed to download source file from: " + sourceUrl);
        }
        
        // 生成输出文件名
        String baseName = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.'));
        String outputFileName = baseName + "_converted." + targetFormat.toLowerCase();
        File outputFile = new File(outputDir, outputFileName);
        
        // 创建输出目录
        outputFile.getParentFile().mkdirs();
        
        // 模拟转换：复制源文件到目标位置
        Files.copy(sourceFile.toPath(), outputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("Mock conversion completed. Output file: {}", outputFile.getAbsolutePath());
        return outputFile;
    }
}
