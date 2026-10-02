package com.ri.artificial.domain.vo.preview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ri
 * @date 2026-10-02 23:28
 * 分片统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    /** 分片总数 */
    private Integer total;
    /** 最短分片字符数 */
    private Integer minChars;
    /** 平均分片字符数 */
    private Integer avgChars;
    /** 最长分片字符数 */
    private Integer maxChars;
    /** 解析 + 分片总耗时(ms) */
    private Long costMs;
}
