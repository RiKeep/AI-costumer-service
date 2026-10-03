package com.ri.artificial.domain.dto;


import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
/**
 * @author Ri
 * @date 2026-10-02
 * 分片参数表单。
 */
@Data
public class SplitterForm {

    /** 分片长度：token 或字符，不同策略含义不同 */
    @Min(value = 100, message = "分片长度不能小于100")
    @Max(value = 2000, message = "分片长度不能超过2000")
    private int chunkSize = 500;

    /** 重叠长度（字符） */
    @Min(value = 0, message = "重叠长度不能小于0")
    @Max(value = 500, message = "重叠长度不能超过500")
    private int overlap = 50;

    /** 句子分片：每片最多句子数 */
    @Min(value = 1, message = "每片句子数不能小于1")
    @Max(value = 50, message = "每片句子数不能超过50")
    private int maxSentences = 10;

    /** 句子分片：重叠句子数 */
    @Min(value = 0, message = "重叠句子数不能小于0")
    @Max(value = 5, message = "重叠句子数不能超过5")
    private int overlapSentences = 1;

    /** AI 分片：最多分片数 */
    @Min(value = 1, message = "最多分片数不能小于1")
    @Max(value = 30, message = "最多分片数不能超过30")
    private int maxChunks = 10;
}
