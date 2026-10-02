package com.ri.artificial.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ri
 * @date 2026-10-02 21:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SplitterOption {
    // 参数名，如 "chunkSize"
    private String key;
    // 显示名，如 "分片长度(字符)"
    private String label;
    // 表单初始值
    private Object defaultValue;
    // 最小值（数字类型用）
    private Object min;
    // 最大值
    private Object max;
}
