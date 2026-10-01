package com.ri.artificial.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ri.artificial.domain.po.SysKnowledge;
import com.ri.artificial.domain.query.KnowledgePageQuery;
import com.ri.artificial.mapper.SysKnowledgeMapper;
import com.ri.artificial.service.IKnowledgeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-01 11:14
 */
@Service
public class KnowledgeServiceImpl extends ServiceImpl<SysKnowledgeMapper, SysKnowledge> implements IKnowledgeService {
    @Override
    public Page<SysKnowledge> pageKnowledge(KnowledgePageQuery query) {
        return null;
    }

    @Override
    public void uploadFiles(List<MultipartFile> files) {

    }

    @Override
    public void deleteKnowledge(List<Long> ids) {

    }

    @Override
    public List<String> listVectorIds(List<Long> docIds) {
        return List.of();
    }
}
