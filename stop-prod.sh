#!/bin/bash

# PDF Converter 生产环境停止脚本

APP_NAME="pdf-converter"
PID_FILE="logs/${APP_NAME}.pid"

echo "🛑 停止 PDF Converter 生产环境应用..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "正在停止进程 (PID: $PID)..."
        kill $PID
        
        # 等待进程结束
        for i in {1..10}; do
            if ! ps -p $PID > /dev/null 2>&1; then
                echo "✅ 应用已成功停止"
                rm -f "$PID_FILE"
                exit 0
            fi
            echo "等待进程结束... ($i/10)"
            sleep 1
        done
        
        # 如果进程仍在运行，强制杀死
        echo "⚠️  进程未正常结束，强制终止..."
        kill -9 $PID
        rm -f "$PID_FILE"
        echo "✅ 应用已强制停止"
    else
        echo "⚠️  进程不存在，清理PID文件..."
        rm -f "$PID_FILE"
    fi
else
    echo "⚠️  PID文件不存在，应用可能未运行"
fi
