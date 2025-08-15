package com.knots.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public String uploadFile(MultipartFile file) throws IOException {
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回文件访问路径
        return "/uploads/" + filename;
    }
    
    public boolean deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/uploads/")) {
                String filename = filePath.substring("/uploads/".length());
                Path path = Paths.get(uploadDir, filename);
                return Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
