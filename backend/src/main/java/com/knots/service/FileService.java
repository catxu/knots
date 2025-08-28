package com.knots.service;

import cn.hutool.core.util.StrUtil;
import com.knots.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ResourceLoader resourceLoader;

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
        String filename = UUID.randomUUID() + fileExtension;
        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        // 返回文件访问路径
        return filename;
    }

    public boolean deleteFile(String filename) {
        try {
            if (StrUtil.isNotBlank(filename)) {
                Path path = Paths.get(uploadDir + filename);
                return Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.error("文件删除失败:", e);
        }
        return false;
    }

    public String loadImage2Base64(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            byte[] fileBytes = Files.readAllBytes(resource.getFile().toPath());
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + location, e);
        }
    }

    public static String buildImageUrl(String filename) {
        return Constants.IMG_URL_PREFIX + filename;
    }
}
