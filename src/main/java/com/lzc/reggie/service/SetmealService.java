package com.lzc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzc.reggie.dto.SetmealDto;
import com.lzc.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal>
{
    /**
     * 扩展 ,保存套餐和菜品之间的关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐,扩展其内容
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);
}
