# Large File Upload System

一个支持大文件分片上传的Java Web应用。

## 功能特点

- 支持普通文件上传
- 支持大文件分片上传
- 实时显示上传进度
- 自动合并文件分片
- 支持GB级别的文件上传

## 技术栈

- Java 8
- Servlet 4.0
- Apache Commons FileUpload 1.5
- Apache Commons IO 2.11.0
- Tomcat 8.5

## 系统要求

- JDK 8+
- Maven 3.x
- Tomcat 8.5+

## 安装部署

1. 克隆项目：
```bash
git clone [repository-url]
```

2. 编译项目：
```bash
mvn clean package
```

3. 部署到Tomcat：
将生成的war文件复制到Tomcat的webapps目录

4. 启动Tomcat：
```bash
startup.sh
```

## 使用说明

1. 普通上传：
   - 访问：`http://localhost:8080/file-upload/index.html`
   - 选择文件并上传

2. 分片上传：
   - 访问：`http://localhost:8080/file-upload/chunk-upload.html`
   - 选择大文件
   - 系统会自动进行分片上传
   - 显示上传进度
   - 上传完成后自动合并文件

## 配置说明

1. 文件大小限制：
   - 普通上传：最大40GB
   - 分片上传：每片100MB
   - 可在web.xml中调整

2. 上传目录：
   - 默认在webapps/file-upload/uploads/目录下
   - 分片临时文件在chunks/目录下

## 注意事项

1. 确保上传目录有足够的磁盘空间
2. 对于特大文件，建议使用分片上传功能
3. 上传完成后会自动清理临时分片文件

## License

MIT License
