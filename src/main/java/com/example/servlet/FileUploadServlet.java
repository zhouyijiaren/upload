package com.example.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";
    
    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final long MAX_FILE_SIZE      = 1024L * 1024L * 1024L * 40L; // 40GB
    private static final long MAX_REQUEST_SIZE   = 1024L * 1024L * 1024L * 50L; // 50GB
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        System.out.println("zhouxiang======>开始处理文件上传请求");
        
        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        System.out.println("zhouxiang======>临时文件目录: " + System.getProperty("java.io.tmpdir"));
        
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        System.out.println("zhouxiang======>设置最大文件大小: " + MAX_FILE_SIZE + " bytes");
        
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        System.out.println("zhouxiang======>设置最大请求大小: " + MAX_REQUEST_SIZE + " bytes");
        
        // 中文处理
        upload.setHeaderEncoding("UTF-8");
        
        // 构造临时路径来存储上传的文件
        String uploadPath = getServletContext().getRealPath("/") + File.separator + UPLOAD_DIRECTORY;
        System.out.println("zhouxiang======>上传文件目录: " + uploadPath);
        
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
            System.out.println("zhouxiang======>创建上传目录: " + uploadPath);
        }
        
        try {
            // 解析请求的内容提取文件数据
            System.out.println("zhouxiang======>开始解析请求内容");
            List<FileItem> formItems = upload.parseRequest(request);
            System.out.println("zhouxiang======>解析到 " + formItems.size() + " 个表单项");
            
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        System.out.println("zhouxiang======>准备保存文件: " + fileName);
                        System.out.println("zhouxiang======>文件大小: " + item.getSize() + " bytes");
                        
                        // 保存文件到硬盘
                        item.write(storeFile);
                        System.out.println("zhouxiang======>文件保存成功: " + filePath);
                        
                        out.println("文件上传成功!");
                        out.println("<br/>文件名: " + fileName);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("zhouxiang======>文件上传失败: " + ex.getMessage());
            ex.printStackTrace();
            out.println("错误信息: " + ex.getMessage());
        }
    }
}
