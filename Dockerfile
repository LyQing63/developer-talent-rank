# 选择基础镜像
FROM openjdk:8 AS build

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .

# 下载 Maven 依赖
RUN mvn dependency:go-offline

# 复制后端代码
COPY src ./src

# 构建后端项目
RUN mvn clean package -DskipTests

# 使用 Nginx 来服务前端静态文件
FROM node:14 AS frontend-build

# 设置工作目录
WORKDIR /app/web

# 复制前端代码
COPY web/package.json .
COPY web/package-lock.json .

# 安装前端依赖
RUN npm install

# 复制前端源代码并构建
COPY web/ .
RUN npm run build

# 使用 Nginx 来服务前端静态文件
FROM nginx:latest

# 复制前端构建结果到 Nginx
COPY --from=frontend-build /app/web/build /usr/share/nginx/html

# 复制后端 JAR 文件
COPY --from=build /app/target/developer-talent-rank-0.0.1-SNAPSHOT.jar /usr/local/lib/developer-talent-rank.jar

# 暴露后端端口
EXPOSE 8080

# 设置启动命令
CMD ["java", "-jar", "/usr/local/lib/developer-talent-rank.jar"]
