package com.ri.artificial.domain.vo;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author Ri
 * @date 2026-10-02 15:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    private Long total;
    private Long pages;
    private List<T> data;

    /**
     *  创建一个空的分页结果，但是保留 total 和 pages
     */
    public static <T> PageVO<T> empty(Long total, Long pages) {
        return new PageVO<>(total, pages, Collections.emptyList());
    }

    public static <T> PageVO<T> empty(Page<?> page) {
        return new PageVO<>(page.getTotal(), page.getPages(), Collections.emptyList());
    }


    /**
     *  把 mybatisPlus 分页结果转换为 PageVO 类型
     */
    public static <T> PageVO<T> of(Page<T> page) {
        if(page == null){
            return new PageVO<>();
        }
        if(CollUtil.isEmpty(page.getRecords())){
            return empty(page);
        }
        return new PageVO<>(page.getTotal(), page.getPages(), page.getRecords());
    }

    /**
     *  total 和 pages 都用 mybatisPlus 的分页结果，data 用自定义的 list
     */
    public static <T> PageVO <T> of(Page<T> page, List<T> data){
        return new PageVO<>(page.getTotal(), page.getPages(), data);
    }
}
