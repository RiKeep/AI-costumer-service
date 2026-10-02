package com.ri.artificial.splitter;

import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.domain.dto.SplitterForm;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-02 20:57
 */
public interface DocumentSplitter {
    /** 策略名，前端展示用，也是注册中心的 key */
    String name();

    /** 说明，前端展示用 */
    String description();

    /** 参数定义，前端动态渲染表单用 */
    List<SplitterOption> options();

    /** 执行分片 */
    List<Document> split(List<Document> documents, SplitterForm params);
}
