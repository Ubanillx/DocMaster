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

### 2. 设置环境变量
在启动应用之前，需要设置环境变量来启用Docker LibreOffice模式：

```bash
# 设置环境变量启用Docker LibreOffice
export USE_DOCKER_LIBREOFFICE=true
```

### 3. 启动Docker LibreOffice服务
使用提供的启动脚本自动启动LibreOffice Docker容器：

```bash
# 使用启动脚本启动Docker LibreOffice服务
./docker-start.sh
```

或者手动启动：
```bash
# 手动启动LibreOffice Docker容器
docker-compose up -d

# 验证容器运行状态
docker ps
```

### 4. 启动应用
有两种方式启动应用：

#### 方式一：使用启动脚本（推荐）
```bash
# 使用启动脚本自动设置环境变量并启动应用
./start-app.sh
```

#### 方式二：手动启动
```bash
# 确保已设置环境变量
export USE_DOCKER_LIBREOFFICE=true

# 编译项目
mvn clean compile

# 运行应用（开发模式）
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. 访问Web界面
打开浏览器访问: http://localhost:8080

## 环境变量配置

### 必需的环境变量
应用需要以下环境变量来正确运行：

| 环境变量 | 说明 | 默认值 | 必需 |
|---------|------|--------|------|
| `USE_DOCKER_LIBREOFFICE` | 启用Docker LibreOffice模式 | `false` | 是 |

### 设置环境变量
```bash
# 启用Docker LibreOffice模式
export USE_DOCKER_LIBREOFFICE=true

# 验证环境变量设置
echo $USE_DOCKER_LIBREOFFICE
```

### 永久设置环境变量
将环境变量添加到您的shell配置文件中：

```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
echo 'export USE_DOCKER_LIBREOFFICE=true' >> ~/.bashrc
source ~/.bashrc
```

## 启动脚本说明

项目提供了两个便捷的启动脚本来简化部署过程：

### 1. docker-start.sh - Docker LibreOffice启动脚本
这个脚本用于启动LibreOffice Docker容器：

**功能：**
- 自动创建必要的目录（uploads、outputs）
- 构建并启动LibreOffice Docker容器
- 检查容器运行状态
- 提供使用说明

**使用方法：**
```bash
# 给脚本执行权限
chmod +x docker-start.sh

# 运行脚本
./docker-start.sh
```

**脚本输出示例：**
```
🚀 启动 PDF Converter 开发环境...
📦 构建 LibreOffice Docker 镜像...
🏃 启动 LibreOffice 容器...
⏳ 等待容器启动...
🔍 检查容器状态...
✅ LibreOffice Docker 容器已启动！

📋 使用说明：
1. 设置环境变量启用 Docker 模式：
   export USE_DOCKER_LIBREOFFICE=true

2. 启动您的 Spring Boot 应用：
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

3. 或者直接运行应用启动脚本：
   ./start-app.sh
```

### 2. start-app.sh - 应用启动脚本
这个脚本用于启动Spring Boot应用：

**功能：**
- 自动设置必需的环境变量
- 检查Docker容器是否运行
- 启动Spring Boot应用

**使用方法：**
```bash
# 给脚本执行权限
chmod +x start-app.sh

# 运行脚本
./start-app.sh
```

**脚本输出示例：**
```
🚀 启动 PDF Converter 应用...
✅ 已设置环境变量: USE_DOCKER_LIBREOFFICE=true
✅ LibreOffice Docker 容器正在运行
🏃 启动 Spring Boot 应用...
```

### 脚本权限设置
首次使用前，需要给脚本添加执行权限：

```bash
# 设置脚本执行权限
chmod +x docker-start.sh
chmod +x start-app.sh

# 验证权限
ls -la *.sh
```

## 部署方式

### 开发环境
使用开发配置文件，连接Docker中的LibreOffice：

#### 使用启动脚本（推荐）
```bash
# 1. 启动LibreOffice Docker容器
./docker-start.sh

# 2. 启动应用（自动设置环境变量）
./start-app.sh
```

#### 手动部署
```bash
# 1. 设置环境变量
export USE_DOCKER_LIBREOFFICE=true

# 2. 启动LibreOffice容器
docker-compose up -d

# 3. 运行应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 生产环境
使用生产配置文件，连接Docker中的LibreOffice：

#### 使用启动脚本（推荐）
```bash
# 1. 启动LibreOffice Docker容器
./docker-start.sh

# 2. 设置环境变量并启动应用
export USE_DOCKER_LIBREOFFICE=true
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### 手动部署
```bash
# 1. 设置环境变量
export USE_DOCKER_LIBREOFFICE=true

# 2. 启动LibreOffice容器
docker-compose up -d

# 3. 运行应用
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

### 1. 环境变量问题
```bash
# 检查环境变量是否设置
echo $USE_DOCKER_LIBREOFFICE

# 如果未设置，重新设置
export USE_DOCKER_LIBREOFFICE=true

# 永久设置环境变量
echo 'export USE_DOCKER_LIBREOFFICE=true' >> ~/.bashrc
source ~/.bashrc
```

### 2. 启动脚本问题
```bash
# 检查脚本权限
ls -la *.sh

# 如果没有执行权限，添加权限
chmod +x docker-start.sh
chmod +x start-app.sh

# 检查脚本内容
cat docker-start.sh
cat start-app.sh
```

### 3. Docker容器问题
```bash
# 检查容器是否运行
docker ps | grep pdf-converter-libreoffice

# 查看容器日志
docker logs pdf-converter-libreoffice

# 重启容器
docker-compose restart

# 完全重新启动
docker-compose down
docker-compose up -d
```

### 4. 文件转换失败
- 检查源文件是否损坏
- 确认目标格式是否支持
- 查看应用日志获取详细错误信息
- 确保Docker容器中的LibreOffice正常运行
- 验证环境变量 `USE_DOCKER_LIBREOFFICE=true` 已设置

### 5. 文件路径问题
- 确保 `uploads` 和 `outputs` 目录存在
- 检查Docker卷挂载是否正确
- 验证文件权限设置
- 使用启动脚本自动创建目录

### 6. 内存不足
- 增加JVM堆内存: `-Xmx2g`
- 处理大文件时可能需要更多内存
- 确保Docker容器有足够的内存分配

### 7. 网络连接问题
- 确保Docker容器可以访问外部网络（用于URL下载）
- 检查防火墙设置
- 验证端口映射配置

### 8. 常见错误及解决方案

#### 错误：容器未运行
```
⚠️  LibreOffice Docker 容器未运行，请先运行: ./docker-start.sh
```
**解决方案：**
```bash
./docker-start.sh
```

#### 错误：环境变量未设置
```
Environment variable USE_DOCKER_LIBREOFFICE is not set
```
**解决方案：**
```bash
export USE_DOCKER_LIBREOFFICE=true
```

#### 错误：脚本权限不足
```
Permission denied: ./docker-start.sh
```
**解决方案：**
```bash
chmod +x docker-start.sh
chmod +x start-app.sh
```

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
├── docker-compose.yml                  # Docker Compose配置
├── Dockerfile.libreoffice              # LibreOffice Docker镜像
├── docker-start.sh                     # Docker LibreOffice启动脚本
└── start-app.sh                        # 应用启动脚本
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
