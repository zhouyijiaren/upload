<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>文件上传</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .upload-container {
            border: 2px dashed #ccc;
            padding: 20px;
            text-align: center;
            margin: 20px 0;
            border-radius: 5px;
        }
        .upload-btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .upload-btn:hover {
            background-color: #45a049;
        }
        #file-input {
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <div class="upload-container">
        <h2>文件上传</h2>
        <form method="post" action="upload" enctype="multipart/form-data">
            <input type="file" name="file" id="file-input" required><br>
            <input type="submit" value="上传文件" class="upload-btn">
        </form>
    </div>
</body>
</html>
