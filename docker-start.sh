#!/bin/bash

# PDF Converter 开发环境 Docker 启动脚本
# 用于启动 LibreOffice Docker 容器，替代本地 LibreOffice 安装

echo "🚀 启动 PDF Converter 开发环境..."

# 设置国内镜像源环境变量
export DOCKER_REGISTRY_MIRROR="https://registry.cn-hangzhou.aliyuncs.com"
export MAVEN_OPTS="-Dmaven.repo.remote=https://maven.aliyun.com/repository/public"

echo "✅ 已设置国内镜像源环境变量:"
echo "   - DOCKER_REGISTRY_MIRROR=${DOCKER_REGISTRY_MIRROR}"
echo "   - MAVEN_OPTS=${MAVEN_OPTS}"

# 创建必要的目录
mkdir -p uploads outputs

# 构建并启动 LibreOffice Docker 容器
echo "📦 构建 LibreOffice Docker 镜像（使用国内镜像源）..."
docker-compose build libreoffice

echo "🏃 启动 LibreOffice 容器..."
docker-compose up -d libreoffice

echo "⏳ 等待容器启动..."
sleep 5

# 检查容器状态
echo "🔍 检查容器状态..."
docker-compose ps

# 检查容器是否运行
if docker ps --filter "name=pdf-converter-libreoffice" --format "{{.Names}}" | grep -q "pdf-converter-libreoffice"; then
    echo "✅ LibreOffice Docker 容器已启动！"
    echo ""
    echo "📋 使用说明："
    echo "1. 设置环境变量启用 Docker 模式："
    echo "   export USE_DOCKER_LIBREOFFICE=true"
    echo ""
    echo "2. 启动您的 Spring Boot 应用："
    echo "   mvn spring-boot:run -Dspring-boot.run.profiles=dev"
    echo ""
    echo "3. 或者使用 IDE 运行 PdfConverterApplication，并设置环境变量："
    echo "   USE_DOCKER_LIBREOFFICE=true"
    echo ""
    echo "4. 或者直接运行应用启动脚本："
    echo "   ./start-app.sh"
    echo ""
    echo "🔧 管理命令："
    echo "📝 查看容器日志: docker-compose logs -f libreoffice"
    echo "🛑 停止容器: docker-compose down"
    echo "🔄 重启容器: docker-compose restart libreoffice"
    echo "📊 进入容器: docker exec -it pdf-converter-libreoffice bash"
else
    echo "❌ LibreOffice 容器启动失败！"
    echo "📝 查看错误日志: docker-compose logs libreoffice"
    exit 1
fi
