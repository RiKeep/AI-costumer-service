package com.ri.artificial.domain.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ri
 * @date 2026-10-02 15:58
 */
@Data
@Accessors(chain = true)
public class PageQuery {
    public static final Integer DEFAULT_PAGE_NUM = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = DEFAULT_PAGE_NUM;

    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    private Boolean isAsc = true;

    private String defaultSortBy = "update_time";

    //
    public <T> Page<T> toPage(String sortBy, boolean isAsc) {
        // 如果没有排序字段，则使用默认字段
        if(StrUtil.isBlank(sortBy)){
            sortBy = defaultSortBy;
        }
        // 创建一个 page 对象
        Page<T> page = new Page<>(pageNum, pageSize);
        // 创建 OrderItem 对象，并设置排序字段和排序方式
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(isAsc);
        orderItem.setColumn(sortBy);
        // 把 OrderItem 对象添加到 page 对象中，实现排序的操作
        page.addOrder(orderItem);
        return page;
    }

    // 方法重载：只传排序字段，默认升序
    public <T> Page<T> toPage(String sortBy) {
        return toPage(sortBy, true);
    }

    // 方法重载：不传排序字段，用默认字段 + 默认升序
    public <T> Page<T> toPage() {
        return toPage(defaultSortBy, true);
    }

}