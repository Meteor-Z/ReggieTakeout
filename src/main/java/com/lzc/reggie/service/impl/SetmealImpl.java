package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.entity.Setmeal;
import com.lzc.reggie.mapper.SetmealMapper;
import com.lzc.reggie.service.SetmealService;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Service
public class SetmealImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService
{
}
