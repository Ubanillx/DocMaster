#!/bin/bash

# PDF Converter 生产环境后台启动脚本
# 自动设置环境变量并在后台启动应用

echo "🚀 启动 PDF Converter 生产环境应用..."

# 设置应用名称和日志文件
APP_NAME="pdf-converter"
LOG_FILE="logs/${APP_NAME}.log"
PID_FILE="logs/${APP_NAME}.pid"

# 创建日志目录
mkdir -p logs

# 设置国内镜像源环境变量
export MAVEN_OPTS="-Dmaven.repo.remote=https://maven.aliyun.com/repository/public"
export DOCKER_REGISTRY_MIRROR="https://registry.cn-hangzhou.aliyuncs.com"

# 设置 Docker LibreOffice 环境变量
export USE_DOCKER_LIBREOFFICE=true

# 设置生产环境配置
export SPRING_PROFILES_ACTIVE=prod

echo "✅ 已设置环境变量:"
echo "   - USE_DOCKER_LIBREOFFICE=true"
echo "   - SPRING_PROFILES_ACTIVE=prod"
echo "   - MAVEN_OPTS=${MAVEN_OPTS}"
echo "   - DOCKER_REGISTRY_MIRROR=${DOCKER_REGISTRY_MIRROR}"

# 检查是否已有进程在运行
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "⚠️  应用已在运行 (PID: $PID)"
        echo "如需重启，请先运行: ./stop-prod.sh"
        exit 1
    else
        echo "清理旧的PID文件..."
        rm -f "$PID_FILE"
    fi
fi

# 检查 Docker 容器是否运行
if ! docker ps --filter "name=pdf-converter-libreoffice" --format "{{.Names}}" | grep -q "pdf-converter-libreoffice"; then
    echo "⚠️  LibreOffice Docker 容器未运行，请先运行: ./docker-start.sh"
    exit 1
fi

echo "✅ LibreOffice Docker 容器正在运行"

# 启动 Spring Boot 应用（后台运行）
echo "🏃 在后台启动 Spring Boot 应用..."
nohup mvn spring-boot:run -Dspring-boot.run.profiles=prod > "$LOG_FILE" 2>&1 &

# 保存进程ID
echo $! > "$PID_FILE"

echo "✅ 应用已启动"
echo "   - 进程ID: $(cat $PID_FILE)"
echo "   - 日志文件: $LOG_FILE"
echo "   - 访问地址: http://121.229.205.96:8080"
echo ""
echo "📋 常用命令:"
echo "   查看日志: tail -f $LOG_FILE"
echo "   停止应用: ./stop-prod.sh"
echo "   查看状态: ./status-prod.sh"
