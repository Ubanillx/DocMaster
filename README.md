# PDF转换器 - LibreOffice Docker 版本

这是一个基于Spring Boot和Docker化LibreOffice的文档转换服务，支持多种文档格式之间的相互转换。使用Docker容器化的LibreOffice，无需在本地安装LibreOffice即可运行。

## 功能特性

- 📄 **全面格式支持**: 支持13种文档格式的相互转换
  - 文档格式: PDF, DOC, DOCX, ODT, RTF, TXT, HTML
  - 表格格式: PDF, XLS, XLSX, ODS  
  - 演示文稿格式: PDF, PPT, PPTX, ODP
- 🌐 **URL转换**: 支持从URL下载文件进行转换
- 📤 **文件上传**: 支持本地文件上传转换
- 🔗 **直接下载**: 提供转换后文件的直接下载链接
- 🎨 **Web界面**: 提供美观易用的Web界面
- ⚡ **强大引擎**: 基于LibreOffice强大的转换引擎
- 🐳 **容器化部署**: 使用Docker容器化LibreOffice，无需本地安装
- 🚀 **多环境支持**: 支持开发和生产环境部署
- 🔄 **同类型转换**: 支持文档、表格、演示文稿各自类型内的格式转换
- 📊 **转换矩阵**: 提供完整的格式转换支持矩阵

## 系统要求

### 必需软件
- Java 21+
- Maven 3.6+
- Docker 20.10+
- Docker Compose 2.0+

### 无需安装LibreOffice
由于使用Docker容器化的LibreOffice，您无需在本地安装LibreOffice。所有转换操作都在Docker容器中执行。

## 快速开始

### 1. 克隆项目
```bash
git clone <your-repo-url>
cd PdfConverter
```

### 2. 启动Docker LibreOffice服务
```bash
# 启动LibreOffice Docker容器
docker-compose -f docker-compose.libreoffice.yml up -d

# 验证容器运行状态
docker ps
```

### 3. 编译并运行应用
```bash
# 编译项目
mvn clean compile

# 运行应用（开发模式）
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. 访问Web界面
打开浏览器访问: http://localhost:8080

## 部署方式

### 开发环境
使用开发配置文件，连接Docker中的LibreOffice：
```bash
# 启动LibreOffice容器
docker-compose -f docker-compose.libreoffice.yml up -d

# 运行应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 生产环境
使用生产配置文件，连接Docker中的LibreOffice：
```bash
# 启动LibreOffice容器
docker-compose -f docker-compose.libreoffice.yml up -d

# 运行应用
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Docker容器管理
```bash
# 查看容器状态
docker ps

# 查看容器日志
docker logs pdf-converter-libreoffice

# 停止容器
docker-compose -f docker-compose.libreoffice.yml down

# 重启容器
docker-compose -f docker-compose.libreoffice.yml restart
```

## API接口

### 1. URL转换
**POST** `/api/conversion/convert-url`

请求体:
```json
{
    "url": "https://example.com/document.pdf",
    "targetFormat": "docx"
}
```

响应:
```json
{
    "success": true,
    "message": "转换成功",
    "originalUrl": "https://example.com/document.pdf",
    "convertedUrl": "http://localhost:8080/files/20241201_143022_a1b2c3d4.docx",
    "originalFormat": "pdf",
    "convertedFormat": "docx",
    "fileSize": 1024000
}
```

### 2. 文件上传转换
**POST** `/api/conversion/convert-upload`

表单数据:
- `file`: 上传的文件
- `targetFormat`: 目标格式

### 3. 获取支持格式
**GET** `/api/conversion/formats`

响应:
```json
{
    "pdf": "writer_pdf_Export",
    "doc": "MS Word 97",
    "docx": "Office Open XML Text",
    "odt": "writer8",
    "rtf": "Rich Text Format",
    "txt": "Text (encoded)",
    "html": "HTML (StarWriter)",
    "xls": "MS Excel 97",
    "xlsx": "Calc MS Excel 2007 XML",
    "ods": "calc8",
    "ppt": "MS PowerPoint 97",
    "pptx": "Impress MS PowerPoint 2007 XML",
    "odp": "impress8"
}
```

### 4. 健康检查
**GET** `/api/conversion/health`

### 5. 文件下载
**GET** `/files/{fileName}`

## 配置说明

### 开发环境配置 (`application-dev.properties`)
```properties
# 服务器端口
server.port=8080

# 文件上传大小限制
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# 文件存储目录
app.storage.directory=./uploads

# 基础URL（用于生成下载链接）
app.base.url=http://localhost:8080

# Docker LibreOffice配置
app.libreoffice.docker.enabled=true
app.libreoffice.docker.container.name=pdf-converter-libreoffice
app.libreoffice.docker.upload.path=/app/uploads
app.libreoffice.docker.output.path=/app/outputs

# 日志级别
logging.level.com.ubanillx.pdfconverter=INFO
logging.level.com.sun.star=WARN
```

### 生产环境配置 (`application-prod.properties`)
```properties
# 服务器端口
server.port=8080

# 文件上传大小限制
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# 文件存储目录
app.storage.directory=./uploads

# 基础URL（用于生成下载链接）
app.base.url=http://localhost:8080

# Docker LibreOffice配置
app.libreoffice.docker.enabled=true
app.libreoffice.docker.container.name=pdf-converter-libreoffice
app.libreoffice.docker.upload.path=/app/uploads
app.libreoffice.docker.output.path=/app/outputs

# 日志级别
logging.level.com.ubanillx.pdfconverter=INFO
logging.level.com.sun.star=WARN
```

### Docker Compose配置 (`docker-compose.libreoffice.yml`)
```yaml
version: '3.8'
services:
  libreoffice:
    image: linuxserver/libreoffice:latest
    container_name: pdf-converter-libreoffice
    environment:
      - PUID=1000
      - PGID=1000
      - TZ=Asia/Shanghai
    volumes:
      - ./uploads:/app/uploads
      - ./outputs:/app/outputs
    ports:
      - "3000:3000"
    restart: unless-stopped
```

## 支持的文件格式

| 格式 | 扩展名 | 描述 |
|------|--------|------|
| PDF | .pdf | Adobe PDF文档 |
| Word | .doc, .docx | Microsoft Word文档 |
| OpenDocument | .odt | OpenDocument文本 |
| RTF | .rtf | 富文本格式 |
| 纯文本 | .txt | 纯文本文件 |
| HTML | .html | 网页文档 |
| Excel | .xls, .xlsx | Microsoft Excel表格 |
| Calc | .ods | OpenDocument表格 |
| PowerPoint | .ppt, .pptx | Microsoft PowerPoint演示文稿 |
| Impress | .odp | OpenDocument演示文稿 |

## 格式转换支持矩阵

本服务支持以下格式之间的相互转换：

### 文档格式转换
| 源格式 | PDF | DOC | DOCX | ODT | RTF | TXT | HTML |
|--------|-----|-----|------|-----|-----|-----|------|
| **PDF** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **DOC** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **DOCX** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **ODT** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **RTF** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **TXT** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **HTML** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

### 表格格式转换
| 源格式 | PDF | XLS | XLSX | ODS |
|--------|-----|-----|------|-----|
| **PDF** | ✅ | ✅ | ✅ | ✅ |
| **XLS** | ✅ | ✅ | ✅ | ✅ |
| **XLSX** | ✅ | ✅ | ✅ | ✅ |
| **ODS** | ✅ | ✅ | ✅ | ✅ |

### 演示文稿格式转换
| 源格式 | PDF | PPT | PPTX | ODP |
|--------|-----|-----|------|-----|
| **PDF** | ✅ | ✅ | ✅ | ✅ |
| **PPT** | ✅ | ✅ | ✅ | ✅ |
| **PPTX** | ✅ | ✅ | ✅ | ✅ |
| **ODP** | ✅ | ✅ | ✅ | ✅ |

### 跨类型转换
| 源类型 | 目标类型 | 支持情况 | 说明 |
|--------|----------|----------|------|
| 文档 | 表格 | ❌ | 不支持跨类型转换 |
| 文档 | 演示文稿 | ❌ | 不支持跨类型转换 |
| 表格 | 文档 | ❌ | 不支持跨类型转换 |
| 表格 | 演示文稿 | ❌ | 不支持跨类型转换 |
| 演示文稿 | 文档 | ❌ | 不支持跨类型转换 |
| 演示文稿 | 表格 | ❌ | 不支持跨类型转换 |

> **注意**: 目前服务仅支持同类型文件格式之间的转换（文档↔文档、表格↔表格、演示文稿↔演示文稿）。跨类型转换功能正在开发中。

## 使用示例

### 使用curl进行URL转换

#### 文档格式转换示例
```bash
# PDF转Word文档
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.pdf",
    "targetFormat": "docx"
  }'

# Word文档转PDF
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.docx",
    "targetFormat": "pdf"
  }'

# DOC转DOCX
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.doc",
    "targetFormat": "docx"
  }'

# ODT转PDF
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.odt",
    "targetFormat": "pdf"
  }'

# RTF转Word
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.rtf",
    "targetFormat": "docx"
  }'

# 纯文本转PDF
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.txt",
    "targetFormat": "pdf"
  }'

# HTML转PDF
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/document.html",
    "targetFormat": "pdf"
  }'
```

#### 表格格式转换示例
```bash
# Excel转PDF
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/spreadsheet.xlsx",
    "targetFormat": "pdf"
  }'

# XLS转XLSX
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/spreadsheet.xls",
    "targetFormat": "xlsx"
  }'

# ODS转Excel
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/spreadsheet.ods",
    "targetFormat": "xlsx"
  }'
```

#### 演示文稿格式转换示例
```bash
# PowerPoint转PDF
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/presentation.pptx",
    "targetFormat": "pdf"
  }'

# PPT转PPTX
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/presentation.ppt",
    "targetFormat": "pptx"
  }'

# ODP转PowerPoint
curl -X POST http://localhost:8080/api/conversion/convert-url \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/presentation.odp",
    "targetFormat": "pptx"
  }'
```

### 使用curl进行文件上传转换

#### 文档格式上传转换示例
```bash
# 上传PDF转Word
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.pdf" \
  -F "targetFormat=docx"

# 上传Word转PDF
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.docx" \
  -F "targetFormat=pdf"

# 上传DOC转DOCX
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.doc" \
  -F "targetFormat=docx"

# 上传ODT转PDF
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.odt" \
  -F "targetFormat=pdf"

# 上传RTF转Word
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.rtf" \
  -F "targetFormat=docx"

# 上传纯文本转PDF
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.txt" \
  -F "targetFormat=pdf"

# 上传HTML转PDF
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/document.html" \
  -F "targetFormat=pdf"
```

#### 表格格式上传转换示例
```bash
# 上传Excel转PDF
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/spreadsheet.xlsx" \
  -F "targetFormat=pdf"

# 上传XLS转XLSX
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/spreadsheet.xls" \
  -F "targetFormat=xlsx"

# 上传ODS转Excel
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/spreadsheet.ods" \
  -F "targetFormat=xlsx"
```

#### 演示文稿格式上传转换示例
```bash
# 上传PowerPoint转PDF
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/presentation.pptx" \
  -F "targetFormat=pdf"

# 上传PPT转PPTX
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/presentation.ppt" \
  -F "targetFormat=pptx"

# 上传ODP转PowerPoint
curl -X POST http://localhost:8080/api/conversion/convert-upload \
  -F "file=@/path/to/presentation.odp" \
  -F "targetFormat=pptx"
```

## 故障排除

### 1. Docker容器问题
```bash
# 检查容器是否运行
docker ps | grep pdf-converter-libreoffice

# 查看容器日志
docker logs pdf-converter-libreoffice

# 重启容器
docker-compose -f docker-compose.libreoffice.yml restart
```

### 2. 文件转换失败
- 检查源文件是否损坏
- 确认目标格式是否支持
- 查看应用日志获取详细错误信息
- 确保Docker容器中的LibreOffice正常运行

### 3. 文件路径问题
- 确保 `uploads` 和 `outputs` 目录存在
- 检查Docker卷挂载是否正确
- 验证文件权限设置

### 4. 内存不足
- 增加JVM堆内存: `-Xmx2g`
- 处理大文件时可能需要更多内存
- 确保Docker容器有足够的内存分配

### 5. 网络连接问题
- 确保Docker容器可以访问外部网络（用于URL下载）
- 检查防火墙设置
- 验证端口映射配置

## 开发说明

### 项目结构
```
src/main/java/com/ubanillx/pdfconverter/
├── PdfConverterApplication.java          # 主应用类
├── controller/
│   ├── ConversionController.java        # 转换API控制器
│   └── FileController.java              # 文件下载控制器
├── model/
│   ├── ConversionRequest.java           # 转换请求模型
│   └── ConversionResponse.java          # 转换响应模型
└── service/
    ├── LibreOfficeService.java          # LibreOffice UNO API服务（本地）
    ├── DockerLibreOfficeService.java    # Docker LibreOffice服务
    └── FileStorageService.java          # 文件存储服务

src/main/resources/
├── application.properties               # 默认配置
├── application-dev.properties          # 开发环境配置
└── application-prod.properties         # 生产环境配置

# Docker配置文件
├── docker-compose.libreoffice.yml      # LibreOffice Docker服务
├── Dockerfile.libreoffice              # LibreOffice Docker镜像
└── start-dev.sh                        # 开发环境启动脚本
```

### 技术架构
- **Spring Boot**: 主应用框架
- **Docker**: LibreOffice容器化部署
- **LinuxServer LibreOffice**: 官方Docker镜像
- **Maven**: 项目构建工具
- **Java 21**: 运行环境

### 扩展功能
- 可以添加更多文件格式支持
- 可以实现批量转换功能
- 可以添加转换进度跟踪
- 可以集成云存储服务
- 可以添加转换队列管理
- 可以支持更多Docker镜像版本

## 许可证

本项目采用MIT许可证。

## 测试验证

### 功能测试
1. **文件上传转换测试**:
   ```bash
   # 创建测试文件
   echo "Hello, World!" > test.txt
   
   # 测试转换
   curl -X POST http://localhost:8080/api/conversion/convert-upload \
     -F "file=@test.txt" \
     -F "targetFormat=pdf"
   ```

2. **URL转换测试**:
   ```bash
   # 启动测试服务器
   python3 -m http.server 9000 --directory . &
   
   # 测试URL转换
   curl -X POST http://localhost:8080/api/conversion/convert-url \
     -H "Content-Type: application/json" \
     -d '{"url":"http://localhost:9000/test.txt","targetFormat":"pdf"}'
   ```

3. **健康检查**:
   ```bash
   curl http://localhost:8080/api/conversion/health
   ```

### 验证转换结果
- 检查生成的PDF文件是否为有效的PDF格式
- 验证文件内容是否正确转换
- 确认下载链接可以正常访问

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

### 开发指南
1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

### 代码规范
- 遵循Java编码规范
- 添加适当的注释
- 编写单元测试
- 更新相关文档
