package com.ri.artificial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ri.artificial.domain.po.SysKnowledge;
import com.ri.artificial.domain.query.KnowledgePageQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
public interface IKnowledgeService extends IService<SysKnowledge> {
    /**
     * 分页查询知识库文件
     */
    Page<SysKnowledge> pageKnowledge(KnowledgePageQuery query);

    /**
     * 上传知识库文件
     */
    void uploadFiles(List<MultipartFile> files);

    /**
     * 删除知识库文件（同步删除 OSS 中的文件）
     */
    void deleteKnowledge(List<Long> ids);

    /**
     * 查询对应的知识库列表ID
     */
    List<String> listVectorIds(List<Long> docIds);
}

