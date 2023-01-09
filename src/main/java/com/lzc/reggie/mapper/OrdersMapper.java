package com.lzc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzc.reggie.common.BaseContext;
import com.lzc.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders>
{
}
