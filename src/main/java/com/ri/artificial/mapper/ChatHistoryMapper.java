package com.ri.artificial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ri.artificial.domain.po.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ri
 * @date 2026-10-01 13:22
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
