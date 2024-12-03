package com.example.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

public class ChunkUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final String CHUNKS_DIRECTORY = "chunks";
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 配置上传参数
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");

            // 创建保存目录
            String uploadPath = getServletContext().getRealPath("/") + File.separator + UPLOAD_DIRECTORY;
            String chunksPath = uploadPath + File.separator + CHUNKS_DIRECTORY;
            createDirectoryIfNotExists(uploadPath);
            createDirectoryIfNotExists(chunksPath);

            // 解析请求
            List<FileItem> formItems = upload.parseRequest(request);
            
            String fileName = null;
            String chunkIndex = null;
            String totalChunks = null;
            FileItem fileItem = null;

            // 获取表单数据
            for (FileItem item : formItems) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String value = item.getString("UTF-8");
                    switch (fieldName) {
                        case "fileName":
                            fileName = value;
                            break;
                        case "chunkIndex":
                            chunkIndex = value;
                            break;
                        case "totalChunks":
                            totalChunks = value;
                            break;
                    }
                } else {
                    fileItem = item;
                }
            }

            if (fileName == null || chunkIndex == null || totalChunks == null || fileItem == null) {
                throw new ServletException("Missing required parameters");
            }

            // 保存分片文件
            String chunkFileName = fileName + "_chunk_" + chunkIndex;
            String chunkPath = chunksPath + File.separator + chunkFileName;
            File chunkFile = new File(chunkPath);
            fileItem.write(chunkFile);

            System.out.println("zhouxiang======>Received chunk " + chunkIndex + " of " + totalChunks + " for file " + fileName);

            // 检查是否所有分片都已上传
            if (areAllChunksUploaded(chunksPath, fileName, Integer.parseInt(totalChunks))) {
                // 合并文件
                mergeChunks(fileName, Integer.parseInt(totalChunks), chunksPath, uploadPath);
                // 删除分片文件
                deleteChunks(chunksPath, fileName, Integer.parseInt(totalChunks));
                
                out.println("{\"status\": \"success\", \"message\": \"File upload completed\"}");
            } else {
                out.println("{\"status\": \"success\", \"message\": \"Chunk " + chunkIndex + " uploaded\"}");
            }

        } catch (Exception ex) {
            System.out.println("zhouxiang======>Error: " + ex.getMessage());
            ex.printStackTrace();
            out.println("{\"status\": \"error\", \"message\": \"" + ex.getMessage() + "\"}");
        }
    }

    private void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private boolean areAllChunksUploaded(String chunksPath, String fileName, int totalChunks) {
        for (int i = 0; i < totalChunks; i++) {
            File chunk = new File(chunksPath + File.separator + fileName + "_chunk_" + i);
            if (!chunk.exists()) {
                return false;
            }
        }
        return true;
    }

    private void mergeChunks(String fileName, int totalChunks, String chunksPath, String uploadPath) throws IOException {
        File outputFile = new File(uploadPath + File.separator + fileName);
        try (FileChannel outputChannel = new FileOutputStream(outputFile).getChannel()) {
            for (int i = 0; i < totalChunks; i++) {
                File chunk = new File(chunksPath + File.separator + fileName + "_chunk_" + i);
                try (FileChannel inputChannel = new FileInputStream(chunk).getChannel()) {
                    outputChannel.position(outputChannel.size());
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                }
            }
        }
        System.out.println("zhouxiang======>File merged successfully: " + fileName);
    }

    private void deleteChunks(String chunksPath, String fileName, int totalChunks) {
        for (int i = 0; i < totalChunks; i++) {
            File chunk = new File(chunksPath + File.separator + fileName + "_chunk_" + i);
            chunk.delete();
        }
    }
}
