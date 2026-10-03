package com.ri.artificial.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.aliyun.oss.OSS;
import com.ri.artificial.exception.UploadFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Ri
 * @date 2026-10-03 16:47
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssClientUtil {
    private final OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.bucket-url}")
    private String bucketUrl;

    // 最大文件大小 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    // 允许上传的文件扩展名和 MIME 类型
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".txt", ".md", ".pdf",
            ".doc", ".docx",
            ".xls", ".xlsx"
    );

    private static final Set<String> ALLOWED_MIME_TYPE = Set.of(
            "text/plain", "text/markdown", "text/x-markdown",
            "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            // 浏览器对 md 等无法识别的格式统一给 octet-stream，此时以扩展名白名单为准
            "application/octet-stream"
    );

    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            urls.add(uploadFile(file));
        }
        return urls;
    }

    private String uploadFile(MultipartFile file) {
        // 判断文件是否满足要求
        checkFileConstrain(file);
        // 生成唯一文件名
        String originFilename = file.getOriginalFilename();
        String ext = originFilename.substring(originFilename.lastIndexOf("."));
        String filename = "files/" + UUID.randomUUID() + ext;

        try {
            ossClient.putObject(bucketName, filename, file.getInputStream());
        } catch (IOException e) {
            throw new UploadFileException("文件上传失败: ", e, HttpStatus.HTTP_INTERNAL_ERROR);
        }

        // 返回文件地址
        return "https://" + bucketUrl + '/' + filename;
    }

    public void deleteFiles(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    private void deleteFile(String fileUrl) {
        // 1. 解析出 OSS 对象 key
        String objectKey = extractObjectKey(fileUrl);
        // 2. 从 OSS 删除文件
        try {
            ossClient.deleteObject(bucketName, objectKey);
            log.info("成功删除OSS文件: bucket={}, key={}", bucketName, objectKey);
        } catch (Exception e) {
            log.error("删除OSS文件失败: bucket={}, key={}", bucketName, objectKey, e);
            throw new UploadFileException("文件删除失败，请稍后重试", e, HttpStatus.HTTP_BAD_REQUEST);
        }
    }


    private String extractObjectKey(String fileUrl) {
        try {
            if(StrUtil.isBlank(fileUrl)){
                throw new UploadFileException("文件URL不能为空", HttpStatus.HTTP_BAD_REQUEST);
            }
            URI uri = new URI(fileUrl);
            // 获取路径部分，例如：/files/uuid.txt
            String path = uri.getPath();
            if (ObjectUtil.isNull(path) || path.isEmpty()) {
                throw new UploadFileException("文件URL有误，请重新上传", HttpStatus.HTTP_BAD_REQUEST);
            }
            // 去掉开头的斜杠 /
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            log.error("解析文件URL失败: {}", fileUrl, e);
            throw new UploadFileException("解析文件URL失败 " + fileUrl, e, HttpStatus.HTTP_BAD_REQUEST);
        }
    }

    private void checkFileConstrain(MultipartFile file) {
        if(file.isEmpty()){
            throw new UploadFileException("文件不能为空", HttpStatus.HTTP_BAD_REQUEST);
        }
        // 不能大于 50mb
        if(file.getSize() > MAX_FILE_SIZE){
            throw new UploadFileException("文件大小超过限制，最大允许50MB", HttpStatus.HTTP_BAD_REQUEST);
        }

        String originalFilename = file.getOriginalFilename();
        if(ObjectUtil.isNull(originalFilename)) {
            throw new UploadFileException("文件名无效", HttpStatus.HTTP_BAD_REQUEST);
        }

        // 提取文件扩展名并转换为小写
        int index = originalFilename.lastIndexOf(".");
        if(index < 0) {
            throw new UploadFileException("文件缺少扩展名", HttpStatus.HTTP_BAD_REQUEST);
        }
        String ext = originalFilename.substring(index).toLowerCase();
        if(!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new UploadFileException("不允许的文件类型，允许的扩展名有: " + ALLOWED_EXTENSIONS, HttpStatus.HTTP_BAD_REQUEST);
        }

        String mimeType = file.getContentType();
        if(!ALLOWED_MIME_TYPE.contains(mimeType) && !ObjectUtil.isNull(mimeType)) {
            throw new UploadFileException("文件MIME类型不合法，仅允许文本或PDF格式", HttpStatus.HTTP_BAD_REQUEST);
        }
    }
}
