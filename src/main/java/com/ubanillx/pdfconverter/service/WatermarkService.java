package com.ubanillx.pdfconverter.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WatermarkService {
    
    private static final Logger logger = LoggerFactory.getLogger(WatermarkService.class);
    
    /**
     * 为PDF文件添加水印
     * @param sourceUrl 源文件URL
     * @param watermarkText 水印文字
     * @param outputDirectory 输出目录
     * @return 加水印后的文件
     */
    public File addWatermarkToPdf(String sourceUrl, String watermarkText, File outputDirectory) {
        try {
            logger.info("开始为PDF添加水印: URL={}, 水印文字={}", sourceUrl, watermarkText);
            
            // 验证输入参数
            if (sourceUrl == null || sourceUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("源文件URL不能为空");
            }
            
            if (watermarkText == null || watermarkText.trim().isEmpty()) {
                throw new IllegalArgumentException("水印文字不能为空");
            }
            
            // 生成输出文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outputFileName = "watermarked_" + timestamp + "_" + System.currentTimeMillis() + ".pdf";
            File outputFile = new File(outputDirectory, outputFileName);
            
            // 下载源文件到临时位置
            File tempSourceFile = downloadFile(sourceUrl, outputDirectory);
            
            try {
                // 创建PDF读取器和写入器
                PdfReader reader = new PdfReader(tempSourceFile);
                PdfWriter writer = new PdfWriter(outputFile);
                PdfDocument pdfDoc = new PdfDocument(reader, writer);
                Document document = new Document(pdfDoc);
                
                // 获取字体
                PdfFont font = PdfFontFactory.createFont();
                
                // 为每一页添加水印
                int numberOfPages = pdfDoc.getNumberOfPages();
                for (int i = 1; i <= numberOfPages; i++) {
                    Rectangle pageSize = pdfDoc.getPage(i).getPageSize();
                    
                    // 创建水印文本
                    Text watermark = new Text(watermarkText)
                            .setFont(font)
                            .setFontSize(50)
                            .setFontColor(ColorConstants.LIGHT_GRAY)
                            .setOpacity(0.3f);
                    
                    Paragraph watermarkParagraph = new Paragraph(watermark)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setHorizontalAlignment(HorizontalAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE);
                    
                    // 在页面中心添加水印
                    Canvas canvas = new Canvas(pdfDoc.getPage(i), pageSize);
                    canvas.showTextAligned(watermarkParagraph, 
                            pageSize.getWidth() / 2, 
                            pageSize.getHeight() / 2, 
                            i, 
                            TextAlignment.CENTER, 
                            VerticalAlignment.MIDDLE, 
                            45); // 45度旋转
                    canvas.close();
                }
                
                document.close();
                pdfDoc.close();
                
                logger.info("水印添加成功: {}", outputFile.getAbsolutePath());
                return outputFile;
                
            } finally {
                // 清理临时文件
                if (tempSourceFile.exists()) {
                    tempSourceFile.delete();
                }
            }
            
        } catch (Exception e) {
            logger.error("添加水印失败", e);
            throw new RuntimeException("添加水印失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 下载文件到本地临时位置
     */
    private File downloadFile(String url, File outputDirectory) throws IOException {
        try {
            URL fileUrl = new URL(url);
            String fileName = "temp_" + System.currentTimeMillis() + ".pdf";
            File tempFile = new File(outputDirectory, fileName);
            
            // 使用Java NIO下载文件
            Path targetPath = Paths.get(tempFile.getAbsolutePath());
            Files.copy(fileUrl.openStream(), targetPath);
            
            logger.info("文件下载完成: {}", tempFile.getAbsolutePath());
            return tempFile;
            
        } catch (Exception e) {
            logger.error("下载文件失败: {}", url, e);
            throw new IOException("下载文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查文件是否为PDF格式
     */
    public boolean isPdfFile(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".pdf") || lowerUrl.contains(".pdf?");
    }
}

