# 国内镜像源配置说明

本项目已配置国内镜像源，以加速依赖下载和Docker镜像拉取。

## 配置的镜像源

### Maven 镜像源
- **阿里云Maven仓库**: https://maven.aliyun.com/repository/public
- **阿里云Spring仓库**: https://maven.aliyun.com/repository/spring
- **阿里云Google仓库**: https://maven.aliyun.com/repository/google

### Docker 镜像源
- **阿里云Docker镜像**: registry.cn-hangzhou.aliyuncs.com
- **中科大镜像**: docker.mirrors.ustc.edu.cn
- **网易镜像**: hub-mirror.c.163.com
- **百度镜像**: mirror.baidubce.com

## 配置文件说明

### 1. Maven 配置
- `pom.xml`: 项目级别的仓库配置
- `.mvn/settings.xml`: 全局Maven设置文件

### 2. Docker 配置
- `Dockerfile.libreoffice`: 使用阿里云镜像源
- `daemon.json`: Docker守护进程镜像源配置

### 3. 启动脚本
- `start-app.sh`: 应用启动脚本，包含镜像源环境变量
- `docker-start.sh`: Docker启动脚本，包含镜像源环境变量

## 使用方法

### 1. 使用Maven构建项目
```bash
# 使用项目内置的Maven配置
mvn clean install

# 或者使用自定义settings.xml
mvn clean install -s .mvn/settings.xml
```

### 2. 启动Docker环境
```bash
# 使用配置了镜像源的启动脚本
./docker-start.sh
```

### 3. 启动应用
```bash
# 使用配置了镜像源的启动脚本
./start-app.sh
```

## 手动配置Docker镜像源

如果需要手动配置Docker镜像源，请将 `daemon.json` 文件复制到以下位置：

### Linux/macOS
```bash
sudo cp daemon.json /etc/docker/daemon.json
sudo systemctl restart docker
```

### Windows
将 `daemon.json` 复制到 `%USERPROFILE%\.docker\daemon.json`，然后重启Docker Desktop。

## 验证配置

### 验证Maven镜像源
```bash
mvn help:effective-settings
```

### 验证Docker镜像源
```bash
docker info | grep -A 10 "Registry Mirrors"
```

## 故障排除

### 1. Maven下载缓慢
- 检查网络连接
- 确认镜像源配置正确
- 清理本地Maven缓存：`mvn dependency:purge-local-repository`

### 2. Docker镜像拉取失败
- 检查Docker守护进程配置
- 重启Docker服务
- 尝试其他镜像源

### 3. 构建失败
- 检查防火墙设置
- 确认代理配置（如果使用代理）
- 查看详细错误日志

## 其他镜像源选择

如果阿里云镜像源不可用，可以尝试以下替代方案：

### Maven镜像源
- 腾讯云: https://mirrors.cloud.tencent.com/nexus/repository/maven-public/
- 华为云: https://repo.huaweicloud.com/repository/maven/
- 清华大学: https://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/

### Docker镜像源
- 腾讯云: https://mirror.ccs.tencentyun.com
- 华为云: https://swr.cn-north-4.myhuaweicloud.com
- 七牛云: https://reg-mirror.qiniu.com

## 注意事项

1. 镜像源配置可能会影响依赖的版本和可用性
2. 建议定期更新镜像源配置
3. 在生产环境中，请确保镜像源的稳定性和安全性
4. 某些私有依赖可能需要特殊配置

