#!/bin/bash

# PDF Converter 生产环境状态检查脚本

APP_NAME="pdf-converter"
PID_FILE="logs/${APP_NAME}.pid"
LOG_FILE="logs/${APP_NAME}.log"

echo "📊 PDF Converter 生产环境状态检查"
echo "=================================="

# 检查PID文件
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "✅ 应用状态: 运行中"
        echo "   - 进程ID: $PID"
        echo "   - 运行时间: $(ps -o etime= -p $PID | tr -d ' ')"
        echo "   - 内存使用: $(ps -o rss= -p $PID | tr -d ' ') KB"
    else
        echo "❌ 应用状态: 已停止 (PID文件存在但进程不存在)"
        echo "   建议清理PID文件: rm -f $PID_FILE"
    fi
else
    echo "❌ 应用状态: 未运行 (无PID文件)"
fi

echo ""

# 检查Docker容器
echo "🐳 Docker LibreOffice 容器状态:"
if docker ps --filter "name=pdf-converter-libreoffice" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -q "pdf-converter-libreoffice"; then
    docker ps --filter "name=pdf-converter-libreoffice" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo "✅ LibreOffice 容器正在运行"
else
    echo "❌ LibreOffice 容器未运行"
    echo "   请运行: ./docker-start.sh"
fi

echo ""

# 检查端口占用
echo "🌐 端口状态:"
if netstat -tlnp 2>/dev/null | grep -q ":8080 "; then
    echo "✅ 端口 8080 已被占用"
    netstat -tlnp 2>/dev/null | grep ":8080 "
else
    echo "❌ 端口 8080 未被占用"
fi

echo ""

# 显示访问信息
echo "🔗 访问信息:"
echo "   - 应用地址: http://121.229.205.96:8080"
echo "   - 日志文件: $LOG_FILE"

if [ -f "$LOG_FILE" ]; then
    echo "   - 日志大小: $(du -h "$LOG_FILE" | cut -f1)"
    echo "   - 最后更新: $(stat -c %y "$LOG_FILE" 2>/dev/null || stat -f %Sm "$LOG_FILE" 2>/dev/null)"
fi

echo ""
echo "📋 常用命令:"
echo "   启动应用: ./start-prod.sh"
echo "   停止应用: ./stop-prod.sh"
echo "   查看日志: tail -f $LOG_FILE"
echo "   重启应用: ./stop-prod.sh && ./start-prod.sh"
