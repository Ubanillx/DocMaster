#!/bin/bash

# PDF Converter 应用启动脚本
# 自动设置 Docker LibreOffice 环境变量并启动应用

echo "🚀 启动 PDF Converter 应用..."

# 设置国内镜像源环境变量
export MAVEN_OPTS="-Dmaven.repo.remote=https://maven.aliyun.com/repository/public"
export DOCKER_REGISTRY_MIRROR="https://registry.cn-hangzhou.aliyuncs.com"

# 设置 Docker LibreOffice 环境变量
export USE_DOCKER_LIBREOFFICE=true

echo "✅ 已设置环境变量:"
echo "   - USE_DOCKER_LIBREOFFICE=true"
echo "   - MAVEN_OPTS=${MAVEN_OPTS}"
echo "   - DOCKER_REGISTRY_MIRROR=${DOCKER_REGISTRY_MIRROR}"

# 检查 Docker 容器是否运行
if ! docker ps --filter "name=pdf-converter-libreoffice" --format "{{.Names}}" | grep -q "pdf-converter-libreoffice"; then
    echo "⚠️  LibreOffice Docker 容器未运行，请先运行: ./docker-start.sh"
    exit 1
fi

echo "✅ LibreOffice Docker 容器正在运行"

# 启动 Spring Boot 应用
echo "🏃 启动 Spring Boot 应用..."
mvn spring-boot:run -Dspring-boot.run.profiles=prod
