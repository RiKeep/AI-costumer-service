package com.ri.artificial.domain.vo.preview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ri
 * @date 2026-10-02 23:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chunk {
    /** 片序号（从 0 开始） */
    private Integer index;
    /** 分片正文 */
    private String text;
    /** 字符数 */
    private Integer charCount;
    /** 在原文中的起始偏移（仅 Fixed 等提供 offset 信息的策略有值） */
    private Long start;
    /** 结束偏移 */
    private Long end;
}
