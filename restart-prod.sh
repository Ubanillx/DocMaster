#!/bin/bash

# PDF Converter 生产环境重启脚本

echo "🔄 重启 PDF Converter 生产环境应用..."

# 停止应用
echo "1️⃣ 停止当前应用..."
./stop-prod.sh

# 等待2秒
echo "⏳ 等待2秒..."
sleep 2

# 启动应用
echo "2️⃣ 启动应用..."
./start-prod.sh

echo "✅ 重启完成！"
echo "   访问地址: http://121.229.205.96:9999"
