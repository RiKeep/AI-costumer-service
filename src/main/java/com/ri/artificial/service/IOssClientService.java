package com.ri.artificial.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-02 19:04
 */
public interface IOssClientService {
    List<String> uploadFiles(List<MultipartFile> files);

    void deleteFiles(List<String> fileUrls);

}
