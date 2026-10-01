package com.ri.artificial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ri.artificial.domain.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ri
 * @date 2026-10-01 19:49
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
